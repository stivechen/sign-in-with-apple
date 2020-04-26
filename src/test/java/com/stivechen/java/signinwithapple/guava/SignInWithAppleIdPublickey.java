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
    protected String getValueWhenExpired(String key) {
        System.out.println("load");
        return "publicKey";
    }

    @Override
    public BaseGuavaCache<String, String> setRefreshDuration(int refreshDuration) {
        return super.setRefreshDuration(24);
    }

    @Override
    public BaseGuavaCache<String, String> setRefreshTimeUnit(TimeUnit refreshTimeunit) {
        return super.setRefreshTimeUnit(TimeUnit.HOURS);
    }

    @Override
    public BaseGuavaCache<String, String> setExpireDuration(int expireDuration) {
        return super.setExpireDuration(30);
    }

    @Override
    public BaseGuavaCache<String, String> setExpireTimeUnit(TimeUnit expireTimeunit) {
        return super.setExpireTimeUnit(TimeUnit.DAYS);
    }

    @Override
    public BaseGuavaCache<String, String> setMaxSize(int maxSize) {
        return super.setMaxSize(20);
    }

    @Test
    public void test_Cache  () throws Exception {
        String key1 = this.getValue("key1");
        System.out.println(key1);
        String key2 = this.getValue("key2");
        System.out.println(key2);
    }
}
