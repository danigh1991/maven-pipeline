package com.core.common.model.sms;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SmsServiceResponse implements Serializable {

   @JsonProperty("Value")
   private String value;
   @JsonProperty("RetStatus")
   private Integer retStatus;
   @JsonProperty("StrRetStatus")
   private String strRetStatus;
   private List<MessageInfo> Data;

    public SmsServiceResponse() {
        super();
    }

   /* public SmsServiceResponse(String Value, Integer RetStatus, String StrRetStatus) {
        this.Value = Value;
        this.RetStatus = RetStatus;
        this.StrRetStatus = StrRetStatus;
    }
    public SmsServiceResponse(String Value, Integer RetStatus, String StrRetStatus, List<MessageInfo> data) {
        this.Value = Value;
        this.RetStatus = RetStatus;
        this.StrRetStatus = StrRetStatus;
        this.Data = data;
    }*/

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getRetStatus() {
        return retStatus;
    }

    public void setRetStatus(Integer retStatus) {
        this.retStatus = retStatus;
    }

    public String getStrRetStatus() {
        return strRetStatus;
    }

    public void setStrRetStatus(String strRetStatus) {
        this.strRetStatus = strRetStatus;
    }

    public List<MessageInfo> getData() {
        return this.Data;
    }

    public void setData(List<MessageInfo> data) {
        this.Data = data;
    }
}
