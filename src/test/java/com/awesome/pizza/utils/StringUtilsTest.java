package com.awesome.pizza.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StringUtilsTest {

    @Test
    public void generateSecureRandomStringTest() {
        String randomString = StringUtils.generateSecureRandomString();
        Assertions.assertEquals(randomString.length(), 32);
    }

    @Test
    public void generateRandomStringTest() {
        for (int i = 0; i < 1000; i++) {
            String randomString = StringUtils.generateRandomString(i);
            Assertions.assertEquals(randomString.length(), i);
        }
    }
}
