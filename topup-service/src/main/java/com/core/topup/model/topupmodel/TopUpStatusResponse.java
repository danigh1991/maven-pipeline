package com.core.topup.model.topupmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TopUpStatusResponse implements Serializable {
   @JsonProperty("errorMsg")
   private String errorMessage;
   private String result;
   @JsonProperty("transactionID")
   private String transactionId;

}
