package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CChangeShipmentPlanStatus implements Serializable {

    @NotNull(message = "{common.shipmentPlan.id_required}")
    private Long shipmentPlanId;

    @NotNull(message = "{common.shipmentPlan.status_required}")
    private Integer status;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String comment;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String trackingCode;

    public Long getShipmentPlanId() {
        return shipmentPlanId;
    }

    public void setShipmentPlanId(Long shipmentPlanId) {
        this.shipmentPlanId = shipmentPlanId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }
}
