package com.core.topup.model.topupmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TopUpSaleResponse implements Serializable {
   private String commandStatus;
   private String origResponseMessage;
   private String resultCode;
   @JsonProperty("transactionID")
   private String transactionId;
   @JsonProperty("errorMsg")
   private String errorMessage;
   @JsonProperty("saleID")
   private Long saleId;
   private Double amount;
   private Double vat;
   private Double discount;
   private Double billAmount;
   private String termResponseMessage;
   private String mobileNumber;

}
