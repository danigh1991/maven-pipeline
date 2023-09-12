package com.core.topup.model.topupmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TopUpReserveResponse implements Serializable {
   private Long saleId;
   private String resultCode;
   @JsonProperty("errMsg")
   private String errorMessage;
   private String operator;
   private Double amount;
   private Double vat;
   private Double discount;
   private Double billAmount;
   private String telNo;
}
