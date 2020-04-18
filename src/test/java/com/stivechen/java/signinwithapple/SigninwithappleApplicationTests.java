package com.stivechen.java.signinwithapple;

import com.stivechen.java.signinwithapple.config.AppleIDConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

@SpringBootTest
@PropertySource(value = {"classpath:config/appleID.properties"})//使用@Value注解方式
class SigninwithappleApplicationTests {

	@Autowired
	private AppleIDConfig appleIDConfig;

	//Spring传统注解方式
	@Value("${autoValue}")
	private String autoValue;

	@Test
	void contextLoads() {
		String privateKey = appleIDConfig.getPrivateKey();
		System.out.println("privateKey:"+privateKey);

		System.out.println("autoValue:"+autoValue);
	}

}
