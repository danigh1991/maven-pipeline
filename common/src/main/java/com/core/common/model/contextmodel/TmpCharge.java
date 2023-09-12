package com.core.common.model.contextmodel;

import com.core.datamodel.model.contextmodel.GeneralBankObject;

import java.io.Serializable;

public class TmpCharge extends GeneralBankObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String referenceNumber;
    private Boolean status;
    private Double amount;

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

     public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
