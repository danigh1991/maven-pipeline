package com.core.topup.model.topupmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TopUpTokenResponse implements Serializable {
   private String access_token;
   private String token_type;
   private Integer expires_in;
   private String refresh_token;
   @JsonProperty("as:client_id")
   private String client_id;
   private String userName;
   @JsonProperty(".issued")
   private Date issued;
   @JsonProperty(".expires")
   private Date expires;

}
