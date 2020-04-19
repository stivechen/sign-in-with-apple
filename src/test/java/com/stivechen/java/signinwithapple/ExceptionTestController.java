package com.stivechen.java.signinwithapple;

import com.stivechen.java.signinwithapple.exception.AppleIDResponseCodeEnum;
import com.stivechen.java.signinwithapple.exception.SignInWithAppleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理测试Contrlller
 *
 * @author chenbingran
 */
@RestController
@Slf4j
@RequestMapping("/exceptionTest")
public class ExceptionTestController {

    @RequestMapping(value = "throwSignInWithAppleException.do")
    public void throwSignInWithAppleException() throws Exception {
        log.error("ExceptionTestController throwSignInWithAppleException!");
        throw new SignInWithAppleException(AppleIDResponseCodeEnum.BAD_RESPONSE);
    }

}
