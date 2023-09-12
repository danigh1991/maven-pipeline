package com.core.card.model.cardmodel;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class OtpDeliveryRequest implements Serializable {
    private String trackingNumber;
    private String sourcePAN;
    private String destinationPAN;
    private Long amount;
    private Integer transactionType;
    private String acceptorCode;
    private String acceptorName;
    private String terminalNumber;
    private Integer terminalType;
    private String approvalCode;
    private String rrn;
    private Integer stan;
    private String accessAddress;
    private Integer securityType;
    private String securityFactor;
    private String iin;

}
