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
public class DepositRequestDto extends BaseDto {

    @NotNullStr(message = "{common.depositRequest.title_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String title;

    @NotNull(message = "{common.depositRequest.active_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Boolean active;

    @NotNull(message = "{common.depositRequest.accountId_required}", groups ={CreateValidationGroup.class})
    private Long accountId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNull(message = "{common.depositRequest.detail_required}", groups ={CreateValidationGroup.class})
    private List<DepositRequestDetailDto> depositRequestDetails;

    @NotNullStr(message = "{common.depositRequest.expireDate_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String expireDate;

}
