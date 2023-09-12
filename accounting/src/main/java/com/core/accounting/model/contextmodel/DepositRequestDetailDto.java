package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DepositRequestDetailDto extends BaseDto {
    @NotNullStr(message = "{common.depositRequestDetail.targetUser_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String targetUser;

    @NotNull(message = "{common.depositRequestDetail.amount_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Double amount;

}
