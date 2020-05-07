package com.stivechen.java.signinwithapple.constant;

/**
 * AppleID相关常理
 *
 * @author chenbingran
 * @create 2020/4/6
 * @since 1.0.0
 */
public class AppleIDConstant {

    /**
     * 生成私钥加密算法：EC
     */
    public static final String PRIVATEKEY_ALGORITHM_EC = "EC";

    /**
     * 生成公钥加密算法：RSA
     */
    public static final String PUBLICKEY_ALGORITHM_RSA = "RSA";

    /**
     * 校验iss固定值
     */
    public static final String ISS_FIELD = "https://appleid.apple.com";

    /**
     * guava Cache 缓存key：applePublicKey
     */
    public static final String GUAVACACHEKEY_APPLEPUBLICKEY= "ApplePublicKey";

}