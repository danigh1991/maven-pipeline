package com.core.common.services.impl;

import com.core.common.services.SecureCodeGenerator;
import com.core.common.util.Utils;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Clock;
import org.springframework.stereotype.Service;

@Service("secureCodeGeneratorImpl")
public class SecureCodeGeneratorImpl implements SecureCodeGenerator {

    @Override
    public String nowCode(String secret,Integer interval) {
        final Totp totp = new Totp(secret,new Clock(interval));
        String code = totp.now();
        return code;
    }

    @Override
    public boolean verifyCode(String verificationCode,String secret,Integer interval ) {
        if(Utils.isStringSafeEmpty(secret))
            secret="empty";
        final Totp totp = new Totp(secret,new Clock(interval));
        if (!Utils.isValidLong(verificationCode)|| !totp.verify(verificationCode))
            return false;
        return true;
    }
}
