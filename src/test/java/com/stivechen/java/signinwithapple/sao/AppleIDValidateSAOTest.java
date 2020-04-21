package com.stivechen.java.signinwithapple.sao;

import com.alibaba.fastjson.JSON;
import com.stivechen.java.signinwithapple.dto.ApplePublicKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * httpClient请求测试
 * <p>
 * Author: stivechen2019
 * Date:  2020/4/21 10:45 PM
 * version:1.0.0
 */
@SpringBootTest
@Slf4j
class AppleIDValidateSAOTest {

    @Autowired
    AppleIDValidateSAO appleIDValidateSAO;

    @Test
    void getAppleIdPublicKey() {

        ApplePublicKey appleIdPublicKey = appleIDValidateSAO.getAppleIdPublicKey();

        Assert.notNull(appleIdPublicKey, "appleIdPublicKey is null");

        log.info("getAppleIdPublicKey test result:{}", JSON.toJSONString(appleIdPublicKey));
    }
}