package com.core.card.model.cardmodel;

import com.core.card.model.view.CardJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class OtpDeliveryResponse implements Serializable {
    private String trackingNumber;
    @JsonView(CardJsonView.OtpDeliveryResponseView.class)
    private Integer status;
    @JsonView(CardJsonView.OtpDeliveryResponseView.class)
    private String requestId;
    private Date registrationDate;
    private List<DeliveryChannel> deliveryChannels;
    private List<ErrorResponse> errors;

    @JsonView(CardJsonView.OtpDeliveryResponseView.class)
    private Integer remainTimeSecond;


}
