package com.core.topup.model.wrapper;

import com.core.topup.model.enums.EOperator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
public class DirectChargeData implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private EOperator operator;
    private Double minPurchase;
    private Long operatorTargetId;

    private List<OperatorDirectCharge> operatorDirectCharges;

    public DirectChargeData(EOperator operator,Double minPurchase,Long operatorTargetId, List<OperatorDirectCharge> operatorDirectCharges) {
        this.operator = operator;
        this.minPurchase = minPurchase;
        this.operatorTargetId = operatorTargetId;
        this.operatorDirectCharges = operatorDirectCharges;
    }

    public Integer  getOperatorId() {
        return operator.getId();
    }
    public String  getOperatorName() {
        return operator.name();
    }

}
