package com.stivechen.java.signinwithapple.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证Token需要的publick要素
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
@Getter
@Setter
public class ApplePublicKey {
    private List<PKey> keys = new ArrayList();

    @Getter
    @Setter
    public static class PKey {
        private String alg;
        private String e;
        private String kid;
        private String kty;
        private String n;
        private String use;
    }
}
