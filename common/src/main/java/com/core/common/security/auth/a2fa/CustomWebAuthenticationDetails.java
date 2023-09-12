package com.core.common.security.auth.a2fa;

import javax.servlet.http.HttpServletRequest;

import com.core.common.util.Utils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = 1L;

    private final String verificationCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = !Utils.isStringSafeEmpty(request.getParameter("verificationCode")) ? request.getParameter("verificationCode"):request.getParameter("activationCode");
    }

    public String getVerificationCode() {
        if (!Utils.isStringSafeEmpty(verificationCode))
           return verificationCode;
        return "";
    }
}