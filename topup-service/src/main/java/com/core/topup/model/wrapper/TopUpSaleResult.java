package com.core.topup.model.wrapper;

import com.core.accounting.model.wrapper.OperationRequestResult;
import com.core.topup.model.view.TopUpJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;

@Data
public class TopUpSaleResult extends OperationRequestResult implements Serializable {
    private static final long serialVersionUID = 1L;


    public TopUpSaleResult(OperationRequestResult op) {
       super(op.getUserId(),op.getEBank(),op.getBankRequestNumber(),op.getBankInfo(),op.getOrderId(),op.getOperationRequestId(),op.getMessage(),op.getReferenceNumber(),op.getStatus(),op.getRedirectUrl());
    }

    @JsonView(TopUpJsonView.TopUpSaleResultView.class)
    private Long topUpRequestId;

}
