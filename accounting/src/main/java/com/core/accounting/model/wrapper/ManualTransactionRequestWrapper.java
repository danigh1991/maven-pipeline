package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EManualTransactionRequestStatus;
import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.util.json.JsonDateSerializer;
import com.core.datamodel.util.json.JsonDateShortSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class ManualTransactionRequestWrapper implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private Long id;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private Long accountId ;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private String accountName ;
        private Long accountTypeId ;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private String accountTypeDesc ;
        private Long userId;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private String userName;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperDetail.class)
        private Long transactionId;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private Integer status;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private String reference;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        @JsonSerialize(using = JsonDateSerializer.class)
        private Date referenceDate;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperDetail.class)
        private String description;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
        private Double amount;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperDetail.class)
        private Long approvedBy;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperDetail.class)
        @JsonSerialize(using = JsonDateTimeSerializer.class)
        private Date approvedDate;
        @JsonView(AccountJsonView.ManualTransactionRequestWrapperDetail.class)
        private String approvedDescription;
        private Long createBy;
        private Date createDate;
        private Long modifyBy;
        private Date modifyDate;

    public ManualTransactionRequestWrapper(Long id, Long accountId, String accountName, Long accountTypeId, String accountTypeDesc, Long userId, String userName,
                                           Long transactionId, Integer status, String reference, Date referenceDate, String description, Double amount, Long approvedBy,
                                           Date approvedDate, String approvedDescription, Long createBy, Date createDate, Long modifyBy, Date modifyDate) {
        this.id = id;
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountTypeId = accountTypeId;
        this.accountTypeDesc = accountTypeDesc;
        this.userId = userId;
        this.userName = userName;
        this.transactionId = transactionId;
        this.status = status;
        this.reference = reference;
        this.referenceDate = referenceDate;
        this.description = description;
        this.amount = amount;
        this.approvedBy = approvedBy;
        this.approvedDate = approvedDate;
        this.approvedDescription = approvedDescription;
        this.createBy = createBy;
        this.createDate = createDate;
        this.modifyBy = modifyBy;
        this.modifyDate = modifyDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @JsonView(AccountJsonView.ManualTransactionRequestWrapperList.class)
    public String getStatusDesc() {
        return EManualTransactionRequestStatus.valueOf(status).getCaption();
    }
}
