package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class UserAccountPolicyCreditDetailDto extends BaseDto {
    @NotNull(message = "{common.accountCreditDetail.id_required}", groups ={CreateValidationGroup.class})
    private Long accountCreditDetailId;
    private Long groupId;
    private Long userId;
}
