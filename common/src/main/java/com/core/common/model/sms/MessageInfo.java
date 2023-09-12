package com.core.common.model.sms;

import java.io.Serializable;
import java.util.Date;

public class MessageInfo implements Serializable {
    private Long msgID;
    private String  body;
    private Date sendDate;
    private String senderNumber;
    private String receiverNumber;
    private String firstLocation;
    private String currentLocation;
    private Integer parts;
    private Integer recCount;
    private Integer recFailed;
    private Integer recSuccess;
    private Boolean isUnicode;

    public MessageInfo() {
    }

    public Long getMsgID() {
        return msgID;
    }

    public void setMsgID(Long msgID) {
        this.msgID = msgID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(String firstLocation) {
        this.firstLocation = firstLocation;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Integer getParts() {
        return parts;
    }

    public void setParts(Integer parts) {
        this.parts = parts;
    }

    public Integer getRecCount() {
        return recCount;
    }

    public void setRecCount(Integer recCount) {
        this.recCount = recCount;
    }

    public Integer getRecFailed() {
        return recFailed;
    }

    public void setRecFailed(Integer recFailed) {
        this.recFailed = recFailed;
    }

    public Integer getRecSuccess() {
        return recSuccess;
    }

    public void setRecSuccess(Integer recSuccess) {
        this.recSuccess = recSuccess;
    }

    public Boolean getUnicode() {
        return isUnicode;
    }

    public void setUnicode(Boolean unicode) {
        isUnicode = unicode;
    }
}
