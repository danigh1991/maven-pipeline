package com.core.topup.model.topupmodel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InternetPackageResponse  implements Serializable {
    private String errorMsg;
    private String result;
    private List<InternetPackageDetailResponse> packageList;

}
