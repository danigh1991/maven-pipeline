package com.core.card.model.cardmodel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CardInfoResponse implements Serializable {
    private String transactionId;
    private Long cardId;
    private String maskedPan;
    private Date referenceExpiryDate;
    private String pan;
    private Date panExpiryDate;
    private Integer assuranceLevel;
    private Integer  status;
    private List<ErrorResponse> errors;

}
