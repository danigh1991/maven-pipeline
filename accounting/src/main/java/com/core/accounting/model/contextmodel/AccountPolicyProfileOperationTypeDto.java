package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import lombok.Data;
import java.util.List;

@Data
public class AccountPolicyProfileOperationTypeDto extends BaseDto {

    private Long operationTypeId;
    private Double minAmount;
    private Double maxAmount;
    private Double globalMaxDailyAmount;
    private Integer order;
    private Boolean notify;
    private List<Double> defaultAmounts;

}
