package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class OperationRequestTargetDto extends BaseDto {

    @NotNull(message = "{common.operationType.code_required}", groups ={CreateValidationGroup.class})
    private Integer operationTypeCode;
    private List<TargetAccountDto> toAccounts=new ArrayList<>();;

    private Integer referenceOperationTypeCode;
    private Long referenceId;
}
