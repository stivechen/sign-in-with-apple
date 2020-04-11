package com.stivechen.java.signinwithapple.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务类异常
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class BizException extends RuntimeException {

    String code;
    String msg;

}

