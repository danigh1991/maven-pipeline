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
public class CostDetailDto extends BaseDto {

    @NotNull(message = "{common.costShareRequest.id_required}", groups ={CreateValidationGroup.class})
    private Long costShareRequestId;

    @NotNullStr(message = "{common.costDetail.description_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNull(message = "{common.costDetail.amount_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Double amount;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String effectiveDate;

}
