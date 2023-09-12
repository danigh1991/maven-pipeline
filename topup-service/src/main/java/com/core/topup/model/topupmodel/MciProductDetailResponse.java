package com.core.topup.model.topupmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MciProductDetailResponse implements Serializable {
    private Integer id;
    private String title;
    private String detailTitle;
    private Integer chargeType;
    private Double chargeAmount;
    private Double giftAmount;
    private Double amount;
    private Double vat;
    private Integer productTypeId;
    private String productTypeTitle;

}
