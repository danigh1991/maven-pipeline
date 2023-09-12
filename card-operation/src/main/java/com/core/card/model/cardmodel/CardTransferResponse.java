package com.core.card.model.cardmodel;

import com.core.card.model.view.CardJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CardTransferResponse implements Serializable {
    private String trackingNumber;
    private String transactionId;
    private Date registrationDate;
    private Date transactionDate;
    private Integer stan;
    private String rrn;
    private String additionalResponseData;
    private Long amount;
    private Integer status;
    private List<ErrorResponse> errors;
    private String securityFactor;
    private Integer transactionType;
    @JsonView(CardJsonView.CardTransferResponseView.class)
    private Long bankCardOperationId;
}
