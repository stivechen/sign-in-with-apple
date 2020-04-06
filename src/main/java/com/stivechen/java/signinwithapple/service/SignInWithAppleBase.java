package com.stivechen.java.signinwithapple.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stivechen.java.signinwithapple.config.AppleIDConfig;
import com.stivechen.java.signinwithapple.controller.form.AppleIDValidateForm;
import com.stivechen.java.signinwithapple.dto.AppleIDTokenClaims;
import com.stivechen.java.signinwithapple.dto.AppleIDValidateInfo;
import com.stivechen.java.signinwithapple.dto.ApplePublicKey;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;

/**
 * AppleID授权基础加解密层
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Slf4j
public abstract class SignInWithAppleBase {

    @Autowired
    private AppleIDConfig appleIDConfig;


    protected AppleIDTokenClaims createAndValidateTokens(AppleIDValidateForm form) {
        //首次解析从Apple设备终端校验生成的identityToken
        AppleIDTokenClaims identityClaims = this.verifyToken(form.getIdentityToken(), "verifyIdentityToken");

        //生成即将与appleid.apple.com交互的必要参数
        AppleIDValidateInfo appleIDValidateInfo = new AppleIDValidateInfo();
        appleIDValidateInfo.setCode(form.getAuthorizationCode());//code即Apple设备终端生成的authoriztionCode
        appleIDValidateInfo.setClient_id(identityClaims.getAud());
        appleIDValidateInfo.setGrant_type("authorization_code");//本次为授权验证
        appleIDValidateInfo.setRedirect_uri(appleIDConfig.getRedirectURI());
        appleIDValidateInfo.setClient_secret(this.buildClientSecret(identityClaims.getAud()));

        return identityClaims;
    }

    /**
     * 构建client_secret
     * JWT格式
     *
     * @param clientId
     * @return
     */
    private String buildClientSecret(String clientId) {
        //heander
        Map<String, Object> header = Jwts.header();
        header.put("alg", "ES256");
        header.put("kid", appleIDConfig.getKid());

        //荷载
        Map<String, Object> claims = Jwts.claims();
        long now = System.currentTimeMillis() / 1000;
        claims.put("iss", appleIDConfig.getIss());
        claims.put("iat", now);
        claims.put("exp", now + appleIDConfig.getExpSeconds());
        claims.put("aud","https://appleid.apple.com");//固定
        claims.put("sub",clientId);//注意！这里是IdentityToken获取到的client_id

        //通过JWT加密生成
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .signWith(this.processPrivateKey(appleIDConfig.getPrivateKey()), SignatureAlgorithm.ES256)
                .compact();
    }


    /**
     * 校验Token
     * 获取客户唯一表示sub
     *
     * @param token
     * @param scene 校验场景：首次交互+apple服务交互
     * @return
     */
    private AppleIDTokenClaims verifyToken(String token, String scene) {
        AppleIDTokenClaims tokenClaims = new AppleIDTokenClaims();
        String publicKey = appleIDConfig.getPublicKey();
        //因为https://appleid.apple.com/auth/keys提供了两个key，且会变，差点出事故，f**k
        int retryTimes = 0;
        int totalRetryTimes = -1;

        if (log.isDebugEnabled()) {
            log.debug("AppleID verifyToken scene:[{}] publicKey:{}", scene, publicKey);
        }

        ApplePublicKey applePublicKey = JSONObject.parseObject(publicKey, ApplePublicKey.class);
        if (null != applePublicKey) {
            List<ApplePublicKey.PKey> keys = applePublicKey.getKeys();
            if (keys != null && !keys.isEmpty()) {
                totalRetryTimes = keys.size();
            }
        }

        //循环匹配publicKey进行解析
        do {
            try {
                JwtParser jwtParser = Jwts.parser().setSigningKey(this.processPublicKey(applePublicKey, retryTimes));
                jwtParser.requireIssuer("https://appleid.apple.com");//官网要求
                jwtParser.requireAudience(appleIDConfig.getClientID());//必须包配置的client_id
                Claims body = jwtParser.parseClaimsJws(token).getBody();//获取到JWT的body内容

                BeanUtils.copyProperties(body, tokenClaims);
                Assert.hasLength(tokenClaims.getSub(), "解析后的sub为空");
                Assert.hasLength(tokenClaims.getAud(), "解析后的aud为空");
                break;
            } catch (Exception e) {
                log.error("AppleID verifyToken scene:[{}] totalRetryTimes:[{}] retryTimes:[{}] has excption:{}",
                        scene, totalRetryTimes, retryTimes, e.getMessage());

                retryTimes++;
            }

        } while (retryTimes < totalRetryTimes);//循环publicKey的个数

        //若官网修改了publicKey，本方法无法实时获取，需要抛出异常
        //失败次数达到publicKey的size
        if (-1 == totalRetryTimes || totalRetryTimes == retryTimes) {
            //TODO 可自定义异常
            log.error("AppleID verifyToken scene:[{}] totalRetryTimes:[{}]  retryTimes:[{}]  is failed!",
                    scene, totalRetryTimes, retryTimes);
        }

        if (log.isDebugEnabled()) {
            log.debug("AppleID verifyToken result tokenClaims:{}", JSON.toJSONString(tokenClaims));
        }

        return tokenClaims;
    }


    /**
     * 加工公钥
     *
     * @param applePublicKey
     * @param retryTimes
     * @return
     */
    private PublicKey processPublicKey(ApplePublicKey applePublicKey, int retryTimes) throws Exception {
        String modulus = null;
        String exponent = null;

        //参考JWT格式校验
        ApplePublicKey.PKey pKey = applePublicKey.getKeys().get(retryTimes);
        modulus = pKey.getN();
        exponent = pKey.getE();

        Assert.hasLength(exponent, "publicKey中e值为空");
        Assert.hasLength(modulus, "publicKey中n值为空");

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(
                new BigInteger(1, Base64.decodeBase64(modulus)),
                new BigInteger(1, Base64.decodeBase64(exponent)));
        KeyFactory keyFactory = null;
        PublicKey fianlKey = null;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            fianlKey = keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("AppleID processPublicKey NoSuchAlgorithmException{}", e.getMessage());
            throw e;//TODO 可自定义异常
        } catch (InvalidKeySpecException e) {
            log.error("AppleID processPublicKey InvalidKeySpecException{}", e.getMessage());
            throw e;//TODO 可自定义异常
        }

        Assert.notNull(fianlKey, "AppleID processPublicKey pulicKey is null");

        return fianlKey;
    }

    /**
     * 加工私钥
     * @param sourceKey
     * @return
     */
    private PrivateKey processPrivateKey(String sourceKey) {
        PrivateKey privateKey = null;



        return privateKey;
    }


}
