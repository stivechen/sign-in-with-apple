package com.stivechen.java.signinwithapple.controller.form;

import lombok.Data;

/**
 * http请求基础form
 *
 * @author chenbingran
 * @create 2020/4/12
 * @since 1.0.0
 */
@Data
public abstract class BaseHttpRequestForm {

    /**请求ip地址*/
    private String clientIP;

    /**请求主机名 */
    private String remoteHost;

    /**请求渠道*/
    private String channelId;

}
