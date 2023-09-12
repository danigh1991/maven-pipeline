package com.core.card.model.cardmodel;

import com.core.card.model.view.CardJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CardEnrollmentResponse implements Serializable {
    @JsonView(CardJsonView.CardEnrollmentResponseView.class)
    private String trackingNumber;
    @JsonView(CardJsonView.CardEnrollmentResponseView.class)
    private String transactionId;
    private Date registrationDate;
    @JsonView(CardJsonView.CardEnrollmentResponseView.class)
    private String registrationAddress;
    private Integer  status;
    private List<ErrorResponse> errors;


    //Todo temprory
    @Deprecated
    public String getRegistrationAddress() {
    //    return registrationAddress.replace("tsm.shaparak.ir","185.167.72.9");
        return registrationAddress;
    }
}
