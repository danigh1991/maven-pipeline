package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EChangeShipmentPlanTrackingCode implements Serializable {

    @NotNull(message = "{common.shipmentPlan.id_required}")
    private Long shipmentPlanId;

    @NotNullStr(message = "{common.shipmentPlan.trackingCode_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String trackingCode;

    public Long getShipmentPlanId() {
        return shipmentPlanId;
    }

    public void setShipmentPlanId(Long shipmentPlanId) {
        this.shipmentPlanId = shipmentPlanId;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }
}
