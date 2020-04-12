package com.stivechen.java.signinwithapple.sao.impl;

import com.alibaba.fastjson.JSON;
import com.stivechen.java.signinwithapple.config.AppleIDConfig;
import com.stivechen.java.signinwithapple.dto.AppleIDResDTO;
import com.stivechen.java.signinwithapple.dto.AppleIDValidateInfo;
import com.stivechen.java.signinwithapple.exception.AppleIDResponseCodeEnum;
import com.stivechen.java.signinwithapple.exception.SignInWithAppleException;
import com.stivechen.java.signinwithapple.sao.AppleIDValidateSAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 通过httpclient请求apple服务器
 * 验证token
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Service
@Slf4j
public class AppleIDValidateSAOImpl implements AppleIDValidateSAO {

    @Autowired
    private AppleIDConfig appleIDConfig;

    private volatile CloseableHttpClient httpClient;
    private volatile PoolingHttpClientConnectionManager connManager;//http请求线程池

    @Override
    public AppleIDResDTO validateAppleIDTokens(AppleIDValidateInfo validateInfo) {
        AppleIDResDTO appleIDResDTO = new AppleIDResDTO();
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = new HttpPost(appleIDConfig.getAppleIDValidateURL());
        List<BasicNameValuePair> valuePairs = new ArrayList<>();

        try {
            valuePairs.add(new BasicNameValuePair("client_id", validateInfo.getClient_id()));
            valuePairs.add(new BasicNameValuePair("client_secret", validateInfo.getClient_secret()));
            valuePairs.add(new BasicNameValuePair("code", validateInfo.getCode()));
            valuePairs.add(new BasicNameValuePair("grant_type", validateInfo.getGrant_type()));
            valuePairs.add(new BasicNameValuePair("redirect_uri", validateInfo.getRedirect_uri()));

            if (log.isDebugEnabled()) {
                log.debug("AppleID validateAppleIDTokens getHTTPRequest valuePairs:[{}]", valuePairs);
            }

            String responseResultData = null;
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
            httpPost.setEntity(entity);
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");//Apple官网定义

            httpResponse = httpClient.execute(httpPost);

            if (null != httpResponse) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                HttpEntity httpEntity = httpResponse.getEntity();
                //4XX-5XX
                if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                    log.error("AppleID validateAppleIDTokens http statusCode:{}", statusCode);
                    throw new SignInWithAppleException(AppleIDResponseCodeEnum.APPLEID_RESPONSE_ERROR);

                }else {
                    responseResultData = EntityUtils.toString(httpEntity, Consts.UTF_8);

                    if (log.isDebugEnabled()) {
                        log.debug("AppleID validateAppleIDTokens get httpRequest result:[{}]", responseResultData);
                    }
                }
            }

            if (StringUtils.isNoneEmpty(responseResultData)) {
                appleIDResDTO = JSON.parseObject(responseResultData,AppleIDResDTO.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭，放回线程池
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("AppleID validateAppleIDTokens httpResponse close has error!");
                }
            }
        }

        return appleIDResDTO;
    }
}
