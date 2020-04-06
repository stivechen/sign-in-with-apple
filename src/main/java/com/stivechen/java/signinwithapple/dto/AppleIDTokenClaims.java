package com.stivechen.java.signinwithapple.dto;

import lombok.Data;

/**
 * AppleID授权登录Token解析要素
 * JWT格式，需要用publicKey解密后
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Data
public class AppleIDTokenClaims {

    private String iss;

    private String aud;

    private String exp;

    private String iat;

    /**
     * The unique identifier for the user.
     */
    private String sub;

    private String email;

    private String email_verified;

    private String is_private_email;

    private String auth_time;

    private String c_hash;

    private String at_hash;

}
