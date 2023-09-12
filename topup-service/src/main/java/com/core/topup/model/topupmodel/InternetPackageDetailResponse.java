package com.core.topup.model.topupmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class InternetPackageDetailResponse  implements Serializable {
    @JsonProperty("packagE_TYPE")
    private Integer package_type;
    @JsonProperty("packagE_DESC")
    private String package_desc;
    @JsonProperty("packagE_COST")
    private Double package_cost;
    private Integer systems;
    private String systemName;
    @JsonProperty("daily_Capacity")
    private Double daily_capacity;
    @JsonProperty("nightly_Capacity")
    private Double nightly_capacity;
    private Integer period;
    private String other_privilege;
    private Double priority;
    private String name;
    private Boolean isProcessed;
    private Boolean isSpecial;
    @JsonProperty("bestseller")
    private Boolean bestSeller;
    private Boolean isNew;
}
