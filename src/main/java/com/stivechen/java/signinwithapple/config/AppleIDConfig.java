package com.stivechen.java.signinwithapple.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * AppleID加解密配置
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */

@Component
@PropertySource(value = {"classpath:config/appleID.properties"})
@ConfigurationProperties
@Data
public class AppleIDConfig {

    /**
     * https://appleid.apple.com/auth/keys获取
     */
    private String publicKey;

    /**
     * developer account private key
     */
    private String privateKey;

    /**
     * developer account
     */
    private String kid;

    /**
     * Apple 10-character Team ID
     */
    private String iss;

    private String redirectURI;

    private String clientID;

    /**
     * client_secret超时时间：秒
     */
    private String expSeconds;

    private String appleIDValidateURL;

}
