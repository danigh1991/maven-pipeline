package com.core.card.model.cardmodel;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorResponse implements Serializable {

      private Integer errorCode;
      private String errorDescription;
      private String referenceName;
      private String originalValue;
      private String extraData;

      public String getShortError() {
            return  "errorCode=" + errorCode + ", errorDescription=" + errorDescription ;
      }
}
