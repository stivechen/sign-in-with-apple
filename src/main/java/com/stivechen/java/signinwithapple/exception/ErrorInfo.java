package com.stivechen.java.signinwithapple.exception;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常信息处理后返回类
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@Data
public class ErrorInfo {
    private String code;
    private String msg;
    private String uri;

    public ErrorInfo() {
    }

    public ErrorInfo(String code, String msg, String uri) {
        this.code = code;
        this.msg = msg;
        this.uri = uri;
    }

    public ErrorInfo(ResponseCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    /**
     * 封装业务异常信息
     *
     * @param req
     * @param e
     * @return
     */
    public static ErrorInfo bizError(HttpServletRequest req, BizException e) {
        if (null == e) {
            return new ErrorInfo(AppleIDResponseCodeEnum.SYSTEM_ERROR);
        }

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMsg(e.getMsg());
        errorInfo.setCode(e.getCode());
        errorInfo.setUri(req.getRequestURI());
        return errorInfo;
    }

    /**
     * 封装非业务异常信息
     * @param req
     * @param e
     * @return
     */
    public static ErrorInfo error(HttpServletRequest req, Exception e) {
        if (null == e) {
            return new ErrorInfo(AppleIDResponseCodeEnum.SYSTEM_ERROR);
        }

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setUri(req.getRequestURI());
        errorInfo.setMsg(e.getMessage());
        errorInfo.setCode("500000");
        return errorInfo;
    }
}
