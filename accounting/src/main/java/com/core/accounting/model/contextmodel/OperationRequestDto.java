package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class OperationRequestDto extends OperationRequestTargetDto {

    public OperationRequestDto(@NotNull(message = "{common.operationType.code_required}", groups = {CreateValidationGroup.class}) Integer operationTypeCode, List<TargetAccountDto> fromAccounts, List<TargetAccountDto> toAccounts , String description, String platform,Long transactionTypeId,Integer referenceOperationTypeCode, Long referenceId) {
        super(operationTypeCode, toAccounts,referenceOperationTypeCode, referenceId);
        this.fromAccounts = fromAccounts;
        this.description = description;
        this.platform = platform;
        this.transactionTypeId=transactionTypeId;
    }

    private List<TargetAccountDto> fromAccounts=new ArrayList<>();
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String platform;

    private Long transactionTypeId;

}
