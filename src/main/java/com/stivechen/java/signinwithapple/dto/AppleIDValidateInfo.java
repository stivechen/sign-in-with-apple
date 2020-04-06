package com.stivechen.java.signinwithapple.dto;

import lombok.Data;

/**
 * 与appleid.apple.com交互所需要的数据
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Data
public class AppleIDValidateInfo {
    private String client_id;
    private String client_secret;
    private String code;
    private String grant_type;
    private String refresh_token;
    private String redirect_uri;
}
