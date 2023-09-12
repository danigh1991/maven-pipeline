package com.core.topup.model.wrapper;

import com.core.topup.model.enums.EOperator;
import com.core.topup.model.topupmodel.InternetPackageDetailResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class InternetPackageData implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private EOperator operator;
    private Long operatorTargetId;
    private Double minPurchase;
    private List<SimType> simTypes;

    public InternetPackageData(EOperator operator,Double minPurchase, Long operatorTargetId, List<SimType> simTypes) {
        this.operator = operator;
        this.operatorTargetId = operatorTargetId;
        this.minPurchase = minPurchase;
        this.simTypes=simTypes;
    }

    public Integer  getOperatorId() {
        return operator.getId();
    }
    public String  getOperatorName() {
        return operator.name();
    }

}
