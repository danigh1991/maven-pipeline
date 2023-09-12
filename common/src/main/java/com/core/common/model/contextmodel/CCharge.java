package com.core.common.model.contextmodel;


import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CCharge implements Serializable {

    @NotNull(message = "{common.charge.gatewayId_required}")
    private Integer gatewayId;

    @NotNull(message = "{common.account.id_required}")
    private Long accountId;

    @NotNull(message = "{common.charge.amount_required}")
    @Min(value = 0,message ="{common.charge.amount_min}" )
    private Double amount;

}
