package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EBankPaymentStatus;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankPaymentWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(MyJsonView.BankPaymentList.class)
    private Long id;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long bankId;
    @JsonView(MyJsonView.BankPaymentList.class)
    private String bankName;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long accountId;
    @JsonView(MyJsonView.BankPaymentList.class)
    private String myReferenceNumber;
    @JsonView(MyJsonView.BankPaymentList.class)
    private Double amount;
    @JsonView(MyJsonView.BankPaymentList.class)
    private String bankReferenceNumber;
    @JsonView(MyJsonView.BankPaymentList.class)
    private String bankResponseLastStatus;
    @JsonView(MyJsonView.BankPaymentList.class)
    private String bankResponseStatusHist;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankResponseTransactionRef;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankResponse;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long orderId;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String shpIds;
    private Integer status;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long  transactionId;
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long  operationRequestId;

    @JsonSerialize(using= JsonDateTimeSerializer.class)
    @JsonView(MyJsonView.BankPaymentList.class)
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;

    @JsonView(MyJsonView.BankPaymentList.class)
    public String statusDesc(){
        return EBankPaymentStatus.valueOf(this.getStatus()).getCaption();
    }
}
