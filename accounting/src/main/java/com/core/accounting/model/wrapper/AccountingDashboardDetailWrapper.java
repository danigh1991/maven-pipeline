package com.core.accounting.model.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountingDashboardDetailWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date summeryDate;
    private Integer registerUserCount;
    private Integer registerMerchantCount;
    private Double sumChargeAmount;
    private Double sumGetMoneyAmount;
}
