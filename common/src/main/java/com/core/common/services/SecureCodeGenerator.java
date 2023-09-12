package com.core.common.services;

public interface SecureCodeGenerator {

    String nowCode(String secret,Integer interval);
    boolean verifyCode(String verificationCode,String secret,Integer interval);
}
