package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetAccountDto extends BaseDto {

    //@NotNull(message = "{common.accountType.id_required}", groups ={CreateValidationGroup.class})
    //private Long accountType;
    private Long accountId;
    @NotNull(message = "{common.amount_required}", groups ={CreateValidationGroup.class})
    private Double amount;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String userName;
    private Long userId;
    private Long userCreditId;
}
