package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MerchantLimitDto extends BaseDto {

    @NotNull(message = "{common.merchantLimit.sourceId_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Long sourceId;
    private Long groupId;
    private Long userId;

}