package com.core.topup.model.topupmodel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MciProductResponse  implements Serializable {
    private String errorMsg;
    private String result;
    private String execStatus;
    private String responseType;
    private String responseDesc;
    private List<MciProductDetailResponse> data;
}
