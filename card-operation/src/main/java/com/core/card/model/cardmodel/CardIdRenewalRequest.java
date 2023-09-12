package com.core.card.model.cardmodel;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class CardIdRenewalRequest implements Serializable {
    private String trackingNumber;
    private String cellphoneNumber;
    private Long cardId;
    private Date referenceExpiryDate;
}
