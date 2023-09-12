package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.model.contextmodel.GeneralBankObject;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import com.core.datamodel.model.enums.EBank;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/*@Getter
@Setter*/
@Data
@NoArgsConstructor
public class OperationRequestResult extends GeneralBankObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public OperationRequestResult(Long userId, EBank eBank, String bankRequestNumber, List<GeneralKeyValue> bankInfo, Long orderId, Long operationRequestId, String message, String referenceNumber, Boolean status, String redirectUrl) {
        super(userId, eBank, bankRequestNumber, bankInfo, orderId, operationRequestId, message);
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.redirectUrl = redirectUrl;
    }

    private String referenceNumber;
    @JsonView(AccountJsonView.OperationRequestResultView.class)
    private Boolean status;
    @JsonView(AccountJsonView.OperationRequestResultView.class)
    private String redirectUrl;

    @JsonView(AccountJsonView.OperationRequestResultView.class)
    public Long getRequestId() {
        return this.getOperationRequestId();
    }
}
