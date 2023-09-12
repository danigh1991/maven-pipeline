package com.core.card.model.cardmodel;

import com.core.common.util.validator.annotation.NotUrlInvalidChar;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Builder
public class CardTransferRequest implements Serializable {
    private String trackingNumber;
    private String sourcePAN;
    private String destinationPAN;
    private Long amount;
    private String pin;
    private String cvv2;
    private String expiryDate;
    private Integer securityControl;
    private String approvalCode;
    private String referenceNumber;
    private String sourceAddress;
    private Integer localization;
    private String acceptorCode;
    private String terminalNumber;
    private Integer terminalType;
}
