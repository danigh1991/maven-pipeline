package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CContactUs implements Serializable{

    @NotNull(message ="{common.contactUs.contactTargetId_required}")
    private Long contactTargetId;
    @NotNullStr(message = "{common.contactUs.comment_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String comment;
    @NotNullStr(message = "{common.contactUs.email_required}")
    @Email(message = "{common.contactUs.email_inValid}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String email;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String mobileNumber;

    public Long getContactTargetId() {
        return contactTargetId;
    }

    public void setContactTargetId(Long contactTargetId) {
        this.contactTargetId = contactTargetId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
