package com.stivechen.java.signinwithapple;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 异常测试类
 *
 * @author chenbingran
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ExceptionTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void return_signInWithAppleException_bad_response_info() throws Exception {
        mockMvc.perform(get("/exceptionTest/throwSignInWithAppleException.do"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code").value("400001"))
                .andExpect(jsonPath("$.msg").value("校验返回结果异常"));
    }

}
