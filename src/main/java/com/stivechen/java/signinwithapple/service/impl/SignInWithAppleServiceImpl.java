package com.stivechen.java.signinwithapple.service.impl;

import com.stivechen.java.signinwithapple.controller.form.AppleIDValidateForm;
import com.stivechen.java.signinwithapple.controller.vo.AppleIDValidateVO;
import com.stivechen.java.signinwithapple.service.SignInWithAppleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * sign in with apple后端请求
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
@Service
@Slf4j
public class SignInWithAppleServiceImpl extends BaseSignInWithApple implements SignInWithAppleService {



    @Override
    public AppleIDValidateVO validateAppleIdToken(AppleIDValidateForm form) {
        return null;
    }
}
