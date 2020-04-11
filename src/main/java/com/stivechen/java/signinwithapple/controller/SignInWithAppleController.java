package com.stivechen.java.signinwithapple.controller;

import com.stivechen.java.signinwithapple.controller.form.AppleIDValidateForm;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * sign in with apple后端请求
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@RestController
public class SignInWithAppleController {


    @RequestMapping(value = "appleIdLogin/validateAppleIdToken.do")
    public void validateAppleIdToken(AppleIDValidateForm form){

    }
}
