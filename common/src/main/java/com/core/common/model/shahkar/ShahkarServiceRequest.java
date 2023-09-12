package com.core.common.model.shahkar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ShahkarServiceRequest implements Serializable {

    private String serviceNumber;
    private Integer serviceType;
    private Integer identificationType;
    private String identificationNo;


    public Integer getKeyAsHash() {
        return (serviceNumber + "," + serviceType +"," + identificationType + "," + identificationNo).hashCode() ;
    }
}
