package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CContactUsReply implements Serializable{

    @NotNull(message = "{common.contactUs.id_required}")
    private Long contactUsId;

    @NotNullStr(message = "{common.contactUs.replyComment_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String replyComment;

    @NotNull(message = "{common.contactUs.sendEmail_required}")
    private Boolean sendEmail;

    @NotNull(message = "{common.contactUs.sendSms_required}")
    private Boolean sendSms;

    public Long getContactUsId() {
        return contactUsId;
    }

    public void setContactUsId(Long contactUsId) {
        this.contactUsId = contactUsId;
    }

    public String getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(String replyComment) {
        this.replyComment = replyComment;
    }

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Boolean getSendSms() {
        return sendSms;
    }

    public void setSendSms(Boolean sendSms) {
        this.sendSms = sendSms;
    }
}
