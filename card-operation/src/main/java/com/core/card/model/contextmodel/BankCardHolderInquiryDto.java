package com.core.card.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.BaseValidationGroup;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class BankCardHolderInquiryDto extends BaseDto {

    @NotNullStr(message = "{common.bankCard.cardNumber_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String   destinationCardNumber;
    @NotNull(message = "common.amount_required", groups = CreateValidationGroup.class)
    private Long  amount;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String  description;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String   platform;

}
