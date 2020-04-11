package com.stivechen.java.signinwithapple.exception;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public ErrorInfo bizExceptionHandler (HttpServletRequest req, BizException e) {
        return ErrorInfo.bizError(req,e);
    }

    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public ErrorInfo exceptionHandler (HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler exceptionHandler e:{}", JSON.toJSONString(e));
        return ErrorInfo.error(req, e);
    }
}
