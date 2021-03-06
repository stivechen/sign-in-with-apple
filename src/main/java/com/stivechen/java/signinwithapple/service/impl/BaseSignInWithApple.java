package com.stivechen.java.signinwithapple.service.impl;

import com.alibaba.fastjson.JSON;
import com.stivechen.java.signinwithapple.common.BaseGuavaCache;
import com.stivechen.java.signinwithapple.config.AppleIDConfig;
import com.stivechen.java.signinwithapple.controller.form.AppleIDValidateForm;
import com.stivechen.java.signinwithapple.dto.AppleIDResDTO;
import com.stivechen.java.signinwithapple.dto.AppleIDTokenClaims;
import com.stivechen.java.signinwithapple.dto.AppleIDValidateInfo;
import com.stivechen.java.signinwithapple.dto.ApplePublicKey;
import com.stivechen.java.signinwithapple.exception.AppleIDResponseCodeEnum;
import com.stivechen.java.signinwithapple.exception.SignInWithAppleException;
import com.stivechen.java.signinwithapple.sao.AppleIDValidateSAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;

import static com.stivechen.java.signinwithapple.constant.AppleIDConstant.*;

/**
 * AppleID授权基础加解密层
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Slf4j
public abstract class BaseSignInWithApple extends BaseGuavaCache<String, ApplePublicKey> {

    @Autowired
    private AppleIDConfig appleIDConfig;
    @Autowired
    private AppleIDValidateSAO appleIDValidateSAO;

    protected AppleIDTokenClaims createAndValidateTokens(AppleIDValidateForm form) throws Exception {
        //首次解析从Apple设备终端校验生成的identityToken
        AppleIDTokenClaims identityClaims = this.verifyToken(form.getIdentityToken(), "verifyIdentityToken");

        //生成即将与appleid.apple.com交互的必要参数
        AppleIDValidateInfo appleIDValidateInfo = new AppleIDValidateInfo();
        appleIDValidateInfo.setCode(form.getAuthorizationCode());//code即Apple设备终端生成的authoriztionCode
        appleIDValidateInfo.setClient_id(identityClaims.getAud());
        appleIDValidateInfo.setGrant_type("authorization_code");//本次为授权验证
        appleIDValidateInfo.setRedirect_uri(appleIDConfig.getRedirectURI());
        appleIDValidateInfo.setClient_secret(this.buildClientSecret(identityClaims.getAud()));

        if (log.isDebugEnabled()) {
            log.debug("AppleID createAndValidateTokens param:{}", JSON.toJSONString(appleIDValidateInfo));
        }

        //发起http请求
        AppleIDResDTO appleIDResDTO = this.appleIDValidateSAO.validateAppleIDTokens(appleIDValidateInfo);

        checkResultInfo(appleIDResDTO);

        AppleIDTokenClaims idTokenClaims = this.verifyToken(appleIDResDTO.getId_token(), "verifyId_token");

        //identityClaims与idTokenClaims中的sub要一致
        if (!identityClaims.getSub().equals(idTokenClaims.getSub())) {
            log.error("AppleID createAndValidateTokens identityClaims's sub{}  idTokenClaims's sub{} is not equal",
                    identityClaims.getSub(), idTokenClaims.getSub());
            throw new SignInWithAppleException(AppleIDResponseCodeEnum.NOTEQUAL_TOKEN);
        }

        return identityClaims;
    }

    /**
     * 校验返回值
     * 非空/有值校验
     *
     * @param appleIDResDTO
     */
    private void checkResultInfo(AppleIDResDTO appleIDResDTO) {
        Assert.notNull(appleIDResDTO, "AppleID validateAppleIDTokens return null!");

        if (StringUtils.isNotBlank(appleIDResDTO.getError()) || StringUtils.isBlank(appleIDResDTO.getId_token())) {
            String errorMsg = appleIDResDTO.getError();
            log.error("AppleID validateAppleIDTokens has error msg:{}", errorMsg);
            throw new SignInWithAppleException(AppleIDResponseCodeEnum.BAD_RESPONSE);
        }
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
        claims.put("aud", ISS_FIELD);//固定
        claims.put("sub", clientId);//注意！这里是IdentityToken获取到的client_id

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
    private AppleIDTokenClaims verifyToken(String token, String scene) throws Exception {
        AppleIDTokenClaims tokenClaims = new AppleIDTokenClaims();
        //因为https://appleid.apple.com/auth/keys提供了两个key，且会变，差点出事故，f**k
        int retryTimes = 0;
        int totalRetryTimes = -1;

        super.setExpireDuration(30);//设置guavaCache30天过期
        super.setRefreshDuration(24);//设置guavaCache24小时刷新一次
        ApplePublicKey applePublicKey = super.getValue(GUAVACACHEKEY_APPLEPUBLICKEY);
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
                jwtParser.requireIssuer(ISS_FIELD);//官网要求
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
            log.error("AppleID verifyToken scene:[{}] totalRetryTimes:[{}]  retryTimes:[{}]  is failed!",
                    scene, totalRetryTimes, retryTimes);
            throw new SignInWithAppleException(AppleIDResponseCodeEnum.ILLEGAL_PUBLICKEY);
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
    private PublicKey processPublicKey(ApplePublicKey applePublicKey, int retryTimes) {
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
            keyFactory = KeyFactory.getInstance(PUBLICKEY_ALGORITHM_RSA);
            fianlKey = keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("AppleID processPublicKey NoSuchAlgorithmException{}", e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error("AppleID processPublicKey InvalidKeySpecException{}", e.getMessage());
        }

        Assert.notNull(fianlKey, "AppleID processPublicKey pulicKey is null");

        return fianlKey;
    }

    /**
     * 加工私钥
     *
     * @param sourceKey
     * @return
     */
    private PrivateKey processPrivateKey(String sourceKey) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(sourceKey));

        PrivateKey privateKey = null;
        KeyFactory keyFactory = null;

        try {
            keyFactory = KeyFactory.getInstance(PRIVATEKEY_ALGORITHM_EC);
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("AppleID processPrivateKey NoSuchAlgorithmException e:{}", e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error("AppleID processPrivateKey InvalidKeySpecException e:{}", e.getMessage());
        }

        Assert.notNull(privateKey, "AppleID processPrivateKey privateKey is null");

        return privateKey;
    }

    @Override
    protected ApplePublicKey getValueWhenExpired(String key) throws Exception {
        ApplePublicKey applePublicKey = appleIDValidateSAO.getAppleIdPublicKey();
        Assert.notNull(applePublicKey, "applePulicKey is null!");
        return applePublicKey;
    }

}
