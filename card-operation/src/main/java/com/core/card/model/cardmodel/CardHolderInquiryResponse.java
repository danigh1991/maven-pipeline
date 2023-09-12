package com.core.card.model.cardmodel;

import com.core.card.model.dbmodel.BankCardOperation;
import com.core.card.model.view.CardJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CardHolderInquiryResponse implements Serializable {
    private String trackingNumber;
    private String transactionId;
    private Date registrationDate;
    private Date transactionDate;
    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    private Integer stan;
    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    private String rrn;
    private String additionalResponseData;
    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    private String cardHolderName;
    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    private String approvalCode;
    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    private Long amount;
    private Integer status;
    private List<ErrorResponse> errors;
    private String securityFactor;
    private Integer transactionType;
    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    private Long bankCardOperationId;

}
