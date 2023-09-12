package com.core.topup.model.wrapper;

import com.core.topup.model.topupmodel.DirectChargeType;
import com.core.topup.model.topupmodel.InternetPackageDetailResponse;
import com.core.topup.model.topupmodel.MciProductDetailResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PackagePeriod implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer  day;
    private List<InternetPackageDetailResponse> internetPackageDetailResponses;

    public PackagePeriod(Integer day, List<InternetPackageDetailResponse> internetPackageDetailResponses) {
        this.day = day;
        this.internetPackageDetailResponses = internetPackageDetailResponses;
    }
}
