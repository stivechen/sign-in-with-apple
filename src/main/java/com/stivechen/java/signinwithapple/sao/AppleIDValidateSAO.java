package com.stivechen.java.signinwithapple.sao;

import com.stivechen.java.signinwithapple.dto.AppleIDResDTO;
import com.stivechen.java.signinwithapple.dto.AppleIDValidateInfo;
import com.stivechen.java.signinwithapple.dto.ApplePublicKey;

/**
 * 通过httpClient请求apple服务器
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
public interface AppleIDValidateSAO {


    /**
     * appleID授权验证
     *
     * @param validateInfo
     * @return
     */
    AppleIDResDTO validateAppleIDTokens(AppleIDValidateInfo validateInfo);


    /**
     * 获取appleId publickey
     * @return
     */
    ApplePublicKey getAppleIdPublicKey();

}
