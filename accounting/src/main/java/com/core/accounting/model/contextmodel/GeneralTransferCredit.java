package com.core.accounting.model.contextmodel;

import com.core.common.util.json.JsonCleanLinkAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GeneralTransferCredit implements Serializable {
   @NotNull(message = "{common.user.username_required}")
   @JsonDeserialize(using = JsonCleanLinkAndXssDeserializer.class)
   private String toUserName;

    @NotNull(message = "{bankPayment.amount_invalid}")
    private Double amount;
}
