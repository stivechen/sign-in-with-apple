package com.stivechen.java.signinwithapple.controller.vo;

import lombok.Data;

/**
 * AppleID授权验证返回VO
 *
 * @author chenbingran
 * @create 2020/4/12
 * @since 1.0.0
 */
@Data
public class AppleIDValidateVO {
    /**
     * appleid的全名
     * 仅仅在该app首次授权才能获取
     */
    private String fullName;

}
