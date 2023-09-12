package com.core.common.security.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;


public class OtpBasedAuthentication extends AbstractAuthenticationToken {

    private String otp;
    private final UserDetails principle;

    public OtpBasedAuthentication(UserDetails principle ) {
        super( principle.getAuthorities() );
        this.principle = principle;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp( String token ) {
        this.otp = token;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return otp;
    }

    @Override
    public UserDetails getPrincipal() {
        return principle;
    }
}
