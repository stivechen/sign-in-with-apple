package com.stivechen.java.signinwithapple.dto;

import lombok.Data;

/**
 * apple验证后返回DTO数据
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Data
public class AppleIDResDTO {

    private String access_token;

    private String expires_in;

    private String id_token;

    private String refresh_token;

    private String token_type;

    private String error;
}
