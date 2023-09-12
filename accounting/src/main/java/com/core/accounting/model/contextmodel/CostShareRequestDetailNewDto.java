package com.core.accounting.model.contextmodel;

import com.core.common.util.validator.CreateValidationGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CostShareRequestDetailNewDto extends CostShareRequestDetailDto {
    @NotNull(message = "{common.costShareRequest.id_required}", groups ={CreateValidationGroup.class})
    private Long costShareRequestId;

}
