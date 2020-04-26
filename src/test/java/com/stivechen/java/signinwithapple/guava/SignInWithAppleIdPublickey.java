package com.stivechen.java.signinwithapple.guava;

import com.stivechen.java.signinwithapple.common.BaseGuavaCache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


/**
 * 获取公钥模拟方法
 *
 * @author chenbingran
 */
public class SignInWithAppleIdPublickey extends BaseGuavaCache<String, String> {


    @Override
    protected String getValueWhenExpired(String key) throws InterruptedException {
        System.out.println("load..");
        Thread.sleep(1000);
        return "publicKey";
    }


    @Test
    public void test_Cache  () throws Exception {
        this.setRefreshDuration(50);
        this.setRefreshTimeUnit(TimeUnit.MILLISECONDS);
        String key1 = this.getValue("key1");
        System.out.println(key1);
        Thread.sleep(1000);
        String key2 = this.getValue("key1");
        System.out.println(key2);
        Thread.sleep(1000);
        String key3 = this.getValue("key1");
        System.out.println(key3);
    }
}
