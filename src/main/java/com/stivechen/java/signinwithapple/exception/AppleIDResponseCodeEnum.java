package com.stivechen.java.signinwithapple.exception;

/**
 * sign in with apple响应码枚举
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
public enum AppleIDResponseCodeEnum implements ResponseCodeEnum {

    BAD_RESPONSE("400001","校验返回结果异常"),

    ILLEGAL_PUBLICKEY("400002","publiceKey匹配不成功"),

    NOTEQUAL_TOKEN("400002","验签不一致"),

    SYSTEM_ERROR("500000","系统异常,请稍后重试");

    private String code;
    private String msg;

    AppleIDResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMsg() {
        return null;
    }
}
