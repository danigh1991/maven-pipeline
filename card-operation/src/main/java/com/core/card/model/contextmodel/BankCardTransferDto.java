package com.core.card.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class BankCardTransferDto extends BaseDto {

    @NotNullStr(message = "{common.bankCard.cardNumber_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String   destinationCardNumber;
    @NotNull(message = "common.amount_required", groups = CreateValidationGroup.class)
    private Long  amount;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    //@NotNullStr(message = "{common.bankCard.pin_required}", groups ={CreateValidationGroup.class})
    private String pin;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    @NotNullStr(message = "{common.bankCard.cvv2_required}", groups ={CreateValidationGroup.class})
    private String cvv2;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    @NotNullStr(message = "{common.bankCard.approvalCode_required}", groups ={CreateValidationGroup.class})
    private String approvalCode;

    @NotNull(message = "common.bankCardOperation.id_required", groups = CreateValidationGroup.class)
    private Long bankCardOperationId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String   platform;

    @NotNull(message = "common.bankCardOperation.secureType_required", groups = CreateValidationGroup.class)
    private Integer secureType;



}
