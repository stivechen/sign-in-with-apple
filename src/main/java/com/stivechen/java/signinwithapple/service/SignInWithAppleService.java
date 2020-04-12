package com.stivechen.java.signinwithapple.service;

import com.stivechen.java.signinwithapple.controller.form.AppleIDValidateForm;
import com.stivechen.java.signinwithapple.controller.vo.AppleIDValidateVO;

/**
 * sign in with apple后端请求
 *
 * @author chenbingran
 * @create 2020/4/11
 * @since 1.0.0
 */
public interface SignInWithAppleService {


    /**
     * 处理授权请求后与系统信息交互
     * @param form
     * @return
     */
    AppleIDValidateVO validateAppleIdToken(AppleIDValidateForm form);

}
