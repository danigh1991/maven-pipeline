package com.core.card.model.cardmodel;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CardEnrollmentRequest implements Serializable {
    private String trackingNumber;
    private String cellphoneNumber;
    private String appUrlPattern;
}
