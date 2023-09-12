package com.core.card.model.cardmodel;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CardHolderInquiryRequest implements Serializable {
    private String trackingNumber;
    private String sourcePAN;
    private String destinationPAN;
    private Long amount;
    private String referenceNumber;
    private String sourceAddress;
    private Integer localization;
    private String acceptorCode;
    private String terminalNumber;
    private Integer terminalType;

}
