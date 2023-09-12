package com.core.accounting.model.contextmodel;


import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CProcessRequestRefundMoney implements Serializable {

    @NotNull(message = "{common.requestRefundMoney.id_required}")
    private Long id;
    @NotNull(message = "{common.requestRefundMoney.status_required}")
    private Integer status;
    private Long payBankId;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String payBankRef;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String payDate;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String payDsc;
    private Long toBankId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String financeDestValue;

}
