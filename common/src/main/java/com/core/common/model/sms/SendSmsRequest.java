package com.core.common.model.sms;

import com.core.common.util.Utils;
import com.core.common.util.validator.annotation.NotNullStr;

public class SendSmsRequest /*extends BaseSmsRequest*/ {
    private String username;
    private String password;
    @NotNullStr(message = "{common.sendSmsRequest.to_required}")
    private String to;
    @NotNullStr(message = "{common.sendSmsRequest.from_required}")
    private String from;
    @NotNullStr(message = "{common.sendSmsRequest.text_required}")
    private String text;
    private Boolean isFlash=false;

    public SendSmsRequest() {
    }


    public String getUsername() {
        if (username==null)
            username= Utils.getAppPropery("app.sms.username","");
        return username;
    }


    public String getPassword() {
        if (password==null)
            password= Utils.getAppPropery("app.sms.password", "");
        return password;
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsFlash() {
        return isFlash;
    }

    public void setIsFlash(Boolean flash) {
        isFlash = flash;
    }



    public String toStringForSend(){
        return "username=" + getUsername().trim() + "&" +
               "password=" + getPassword().trim() + "&" +
               "to=" + getTo().trim() + "&" +
               "from=" + getFrom().trim() + "&" +
               "text=" + getText().trim() + "&" +
               "isflash=" + getIsFlash() ;
    }
}
