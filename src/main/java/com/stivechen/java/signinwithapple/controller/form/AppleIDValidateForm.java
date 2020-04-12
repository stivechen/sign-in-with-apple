package com.stivechen.java.signinwithapple.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * Sign in with Apple请求form
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Data
public class AppleIDValidateForm extends BaseHttpRequestForm {

    @NotEmpty(message = "authorizationCode不能为空")
    private String authorizationCode;

    /**
     * JWT格式，需要解密
     */
    @NotEmpty(message = "identityToken不能为空")
    private String identityToken;

    /**
     * Apple的fullname，非首次授权无值
     */
    private String fullName;
}
