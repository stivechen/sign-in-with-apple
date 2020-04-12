package com.stivechen.java.signinwithapple.controller;

import com.stivechen.java.signinwithapple.controller.form.AppleIDValidateForm;
import com.stivechen.java.signinwithapple.controller.vo.AppleIDValidateVO;
import com.stivechen.java.signinwithapple.service.SignInWithAppleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sign in with apple后端请求
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@RestController
@Slf4j
public class SignInWithAppleController {

    @Autowired
    private SignInWithAppleService signInWithAppleService;

    @RequestMapping(value = "appleIdLogin/validateAppleIdToken.do")
    public AppleIDValidateVO validateAppleIdToken(AppleIDValidateForm form, HttpServletRequest req, HttpServletResponse res){
        AppleIDValidateVO appleIDValidateVO = new AppleIDValidateVO();

        //manageRequestForm(form, req)

        appleIDValidateVO = signInWithAppleService.validateAppleIdToken(form);

        return appleIDValidateVO;
    }
}
