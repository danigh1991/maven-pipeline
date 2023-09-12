package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.TransactionRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.ColumnResult;
import java.io.Serializable;
import java.util.Date;

@Data
public class TransactionWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(AccountJsonView.TransactionList.class)
    private Long id;
    private Long accountId;
    @JsonView(AccountJsonView.TransactionList.class)
    private Long operationTypeId;
    @JsonView(AccountJsonView.TransactionList.class)
    private String operationTypeName;
    @JsonView(AccountJsonView.TransactionList.class)
    private Integer operationTypeCode;
    @JsonView(AccountJsonView.TransactionList.class)
    private Character operationType;
    private String description;
    @JsonView(AccountJsonView.TransactionList.class)
    private Integer transactionTypeId;
    private String transactionTypeCaption;
    @JsonView(AccountJsonView.TransactionList.class)
    private Double debit;
    @JsonView(AccountJsonView.TransactionList.class)
    private Double credit;
    @JsonView(AccountJsonView.TransactionDetails.class)
    private String referenceId;
    private Date createDate;
    @JsonView(AccountJsonView.TransactionDetails.class)
    private Long createBy;
    private Date modifyDate;
    @JsonView(AccountJsonView.TransactionList.class)
    private Integer status;
    @JsonView(AccountJsonView.TransactionList.class)
    private Long crossTransactionId;
    @JsonView(AccountJsonView.TransactionList.class)
    private Long orderId;
    private Long userCreditId;

    private TransactionWrapper destination;
    //@JsonView(AccountJsonView.TransactionList.class)
    private TransactionWrapper source;

    //private BankPaymentWrapper bankPaymentWrapper;


    public TransactionWrapper(Long id, Long accountId, Long operationTypeId, String operationTypeName, Integer operationTypeCode, Character operationType,
                              String description,Integer transactionTypeId,String transactionTypeCaption, Double debit, Double credit, String referenceId,
                              Date createDate, Long createBy, Date modifyDate, Integer status, Long crossTransactionId, Long orderId,Long userCreditId) {
        this.id = id;
        this.accountId = accountId;
        this.operationTypeId = operationTypeId;
        this.operationTypeName = operationTypeName;
        this.operationTypeCode = operationTypeCode;
        this.operationType = operationType;
        this.description = description;
        this.transactionTypeId = transactionTypeId;
        this.transactionTypeCaption = transactionTypeCaption;
        this.debit = debit;
        this.credit = credit;
        this.referenceId = referenceId;
        this.createDate = createDate;
        this.createBy = createBy;
        this.modifyDate = modifyDate;
        this.status = status;
        this.crossTransactionId = crossTransactionId;
        this.orderId=orderId;
        this.userCreditId = userCreditId;
    }


    @JsonView(AccountJsonView.TransactionDetails.class)
    public String getDescription() {
        return BaseUtils.getMessageResourceByKeyArgs(description);
    }

    @JsonView(AccountJsonView.TransactionDetails.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.TransactionDetails.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }



    @JsonView(AccountJsonView.TransactionList.class)
    public TransactionWrapper getDestination() {
        if (destination==null && this.getCrossTransactionId()!=null)
            destination=((TransactionRepository) AccountingRepositoryFactory.getRepository(ERepository.TRANSACTION)).getTransactionWrapperById(this.getCrossTransactionId());
        return destination;
    }

    @JsonView(AccountJsonView.TransactionList.class)
    public String getAmountDesc() {
        if (this.getCredit() > 0)
            return BaseUtils.getMessageResource("global.deposit");
        else if (this.getDebit() > 0)
            return BaseUtils.getMessageResource("global.withdraw");
        else
            return "";
    }

    @JsonView(AccountJsonView.TransactionList.class)
    public String getOperationTypeCaption() {
        return EOperation.valueOfCode(operationTypeCode).getCaption();
    }



}
