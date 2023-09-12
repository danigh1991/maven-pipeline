package com.core.accounting.model.contextmodel;

import com.core.common.util.json.JsonCleanLinkAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CTransferCredit extends GeneralTransferCredit{

    @JsonDeserialize(using = JsonCleanLinkAndXssDeserializer.class)
    private String description;

    @NotNull(message = "{common.user.activationCode_invalid}")
    private Integer userCode;
}
