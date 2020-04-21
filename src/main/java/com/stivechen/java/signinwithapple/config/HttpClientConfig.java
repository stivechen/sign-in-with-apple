package com.stivechen.java.signinwithapple.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * HttpClient和请求线程池的配置参数
 *
 * @author chenbingran
 */

@Component
@PropertySource(value = {"classpath:config/httpClientConfig.properties"})
@ConfigurationProperties
@Data
public class HttpClientConfig {

    /**最大连接数*/
    private int connManagerMaxTotal;

    /**每个路由最大连接数*/
    private int connManagerDefaultMaxPerRoute;

    /**接受数据等待超时时间ms*/
    private int connManagerSoTimeout;

    /**连接超时时间ms*/
    private int reqConnectTimeout;

    /**读取数据超时ms*/
    private int reqSocketTimeout;

    /**从连接池获取连接超时时间ms*/
    private int connRequestTimeout;

    /**重试次数*/
    private int retryCount;

}
