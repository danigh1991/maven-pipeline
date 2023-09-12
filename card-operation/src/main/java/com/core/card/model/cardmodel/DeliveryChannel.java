package com.core.card.model.cardmodel;

import lombok.Data;
import java.io.Serializable;

@Data
public class DeliveryChannel implements Serializable {
    private Integer channelType;
    private String channelAdditionalData;
}
