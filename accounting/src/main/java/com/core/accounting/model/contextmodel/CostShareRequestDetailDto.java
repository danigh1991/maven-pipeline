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
public class CostShareRequestDetailDto extends BaseDto {
    @NotNullStr(message = "{common.costShareRequestDetail.targetUser_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String targetUser;

    @NotNull(message = "{common.costShareRequestDetail.amount_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Double amount;

}
