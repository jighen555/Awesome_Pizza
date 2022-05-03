package com.awesome.pizza.utils;

import java.security.SecureRandom;

public class StringUtils {

    static final String ALL_SIMPLE_CHARACHTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom secureRandom = new SecureRandom();

    /**
     * Method for generating a random string
     * @param lenght: Number of charachter
     * @return Random string
     */
    public static String generateRandomString(int lenght) {
        StringBuilder sb = new StringBuilder(lenght);
        for(int i = 0; i < lenght; i++)
            sb.append(ALL_SIMPLE_CHARACHTER.charAt(secureRandom.nextInt(ALL_SIMPLE_CHARACHTER.length())));
        return sb.toString();
    }

    /**
     * Method for generating a random string with 32 charachter
     * @return 32 character random string
     */
    public static String generateSecureRandomString() {
        return generateRandomString(32);
    }
}
