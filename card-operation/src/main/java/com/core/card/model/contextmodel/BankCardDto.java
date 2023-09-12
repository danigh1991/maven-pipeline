package com.core.card.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class BankCardDto extends BaseDto {

    @NotNullStr(message = "{common.bankCard.name_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;
    @NotNullStr(message = "{common.bankCard.cardNumber_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String cardNumber;
    @NotNull(message = "{common.bankCard.active_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Boolean active=true;
    //@NotNullStr(message = "{common.bankCard.expire_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    //private String expire;
    private Long userId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
}
