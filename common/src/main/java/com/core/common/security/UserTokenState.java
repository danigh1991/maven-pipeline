package com.core.common.security;

import com.core.common.util.Utils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.Locale;


public class UserTokenState implements Serializable{
    private String access_token;
    private String refresh_token;
    private Long expires_in;
    private Long userId;
    private String name;
    private String family;
    private String logoPath;


    public UserTokenState() {
        this.access_token = null;
        this.expires_in = null;
        this.name=null;
        this.family=null;
        this.userId=null;
        this.logoPath=null;
    }

    public UserTokenState(String access_token,String refresh_token, long expires_in,Long userId,String name, String family, String logoPath) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
        this.logoPath = logoPath;

        int i=0;
        if (name==null){
            i++;
            this.name="";
        }
        else {
            this.name = name;
        }

        if (family==null){
            i++;
            this.family="";
        }
        else {
            this.family = family;
        }
        /*if (i==2)
            this.name= Utils.getMessageResource("common.userTokenState.name_default" );*/

        this.userId=userId;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
