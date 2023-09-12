package com.core.accounting.model.contextmodel;


import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CRequestRefundMoney implements Serializable {

    @NotNull(message = "{common.account.id_required}")
    private Long accountId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String reqDesc;
    @NotNull(message = "{common.requestRefundMoney.reqAmount_required}")
    private Double reqAmount;

    //@JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private Long bankAccountId;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String financeDestName;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String financeDestValue;

}
