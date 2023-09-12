package com.core.topup.model.wrapper;

import com.core.topup.model.topupmodel.DirectChargeType;
import com.core.topup.model.topupmodel.MciProductDetailResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OperatorDirectCharge implements Serializable {
    private static final long serialVersionUID = 1L;
    private DirectChargeType directChargeType;
    private List<MciProductDetailResponse> productDetails;

    public OperatorDirectCharge(DirectChargeType directChargeType, List<MciProductDetailResponse> productDetails) {
        this.directChargeType = directChargeType;
        this.productDetails = productDetails;
    }
}
