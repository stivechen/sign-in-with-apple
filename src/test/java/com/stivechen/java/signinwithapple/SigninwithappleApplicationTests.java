package com.stivechen.java.signinwithapple;

import com.stivechen.java.signinwithapple.config.AppleIDConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SigninwithappleApplicationTests {

	@Autowired
	private AppleIDConfig appleIDConfig;

	@Test
	void contextLoads() {
		String privateKey = appleIDConfig.getPrivateKey();
		System.out.println("privateKey:"+privateKey);
	}

}
