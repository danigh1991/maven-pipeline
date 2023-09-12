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
public class CostShareRequestDto extends BaseDto {

    @NotNullStr(message = "{common.costShareRequest.title_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String title;

    @NotNull(message = "{common.costShareType.id_required}", groups ={CreateValidationGroup.class})
    private Long typeId;

    @NotNull(message = "{common.costShareRequest.active_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Boolean active;

    @NotNull(message = "{common.costShareRequest.accountId_required}", groups ={CreateValidationGroup.class})
    private Long accountId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNull(message = "{common.costShareRequest.detail_required}", groups ={CreateValidationGroup.class})
    private List<CostShareRequestDetailDto> costShareRequestDetails;

    @NotNullStr(message = "{common.costShareRequest.expireDate_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String expireDate;

}
