package com.stivechen.java.signinwithapple.exception;

import lombok.Data;

/**
 * AppleID校验过程异常
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@Data
public class SignInWithAppleException extends BizException {

    public SignInWithAppleException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SignInWithAppleException(ResponseCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }


}
