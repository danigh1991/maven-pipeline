package com.core.common.model.contextmodel;


import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.core.common.util.validator.annotation.Password;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class ChangePassword {

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String oldPassword;

    @NotNullStr(message = "{common.changePassword.newPassword_required}")
    @Password(message = "{common.changePassword.newPassword_len}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String newPassword;

    @NotNullStr(message = "common.changePassword.confirmNewPassword_required")
    @Password(message = "{common.changePassword.confirmNewPassword_len}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String confirmNewPassword;


    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
