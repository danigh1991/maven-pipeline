package com.core.card.model.cardmodel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CardIdRenewalResponse implements Serializable {
    private String trackingNumber;
    private String transactionId;
    private Date registrationDate;
    private Long cardId;
    private Date referenceExpiryDate;
    private Integer  status;
    private List<ErrorResponse> errors;
}
