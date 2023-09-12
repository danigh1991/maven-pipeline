package com.core.card.model.cardmodel;

import com.core.card.model.view.CardJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AppReactivationResponse implements Serializable {
    @JsonView(CardJsonView.AppReactivationResponseView.class)
    private String trackingNumber;
    @JsonView(CardJsonView.AppReactivationResponseView.class)
    private String transactionId;
    private Date registrationDate;
    @JsonView(CardJsonView.AppReactivationResponseView.class)
    private String reactivationAddress;
    private Integer  status;
    private List<ErrorResponse> errors;

    //Todo temprory
    @Deprecated
    public String getReactivationAddress() {
        return reactivationAddress.replace("tsm.shaparak.ir","185.167.72.9");
    }
}
