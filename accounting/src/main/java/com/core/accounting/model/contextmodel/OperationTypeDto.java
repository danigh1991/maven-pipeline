package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.EditValidationGroup;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OperationTypeDto extends BaseDto {

    @NotNull(message = "{common.operationType.name_required}", groups ={ EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
    @NotNull(message = "{common.operationType.active_required}", groups ={ EditValidationGroup.class})
    private Boolean active;
    @NotNull(message = "{common.operationType.minAmount_required}", groups ={ EditValidationGroup.class})
    private Double minAmount;
    private Double maxAmount;
    private Double globalMaxDailyAmount;
    /*@NotNull(message = "{common.operationType.maxAmountDurationType_required}", groups ={ EditValidationGroup.class})
    private Integer maxAmountDurationType;
    private Integer maxAmountDuration;*/
    @NotNull(message = "{common.operationType.wageType_required}", groups ={ EditValidationGroup.class})
    private Integer wageType;
    private Double wageRate;
    private Double wageAmount;
    @NotNull(message = "{common.operationType.order_required}", groups ={ EditValidationGroup.class})
    private Integer order;
    private List<Double> defaultAmounts;

    @NotNull(message = "{common.operationType.notify_required}", groups ={ EditValidationGroup.class})
    private Boolean notify;

}
