package com.core.topup.model.topupmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class DirectChargeType implements Serializable {
   private String code;
   private String description;
}
