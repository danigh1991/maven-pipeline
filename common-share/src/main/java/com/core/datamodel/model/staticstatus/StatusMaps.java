package com.core.datamodel.model.staticstatus;

import com.core.datamodel.model.enums.ERefundStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatusMaps {

    ////////////////////////////////////Refund Valis Status Changes ///////////////////////////////////////////////////
    public static final Map<Integer, Map<Integer, ERefundStatus>> REFUND_VALID_CHANGE_STATUS_MAP;
    static {
        Map<Integer, Map<Integer, ERefundStatus>> refundStatusChangeMap = new HashMap<>();
        Map<Integer, ERefundStatus> newRequest = new HashMap<>();
        newRequest.put(ERefundStatus.WAIT_FOR_DEPOSIT.getId(), ERefundStatus.WAIT_FOR_DEPOSIT);
        /*newRequest.put(ERefundStatus.DEPOSIT.getId(), ERefundStatus.DEPOSIT);*/
        newRequest.put(ERefundStatus.REJECT.getId(), ERefundStatus.REJECT);
        newRequest.put(ERefundStatus.USER_CANCEL.getId(), ERefundStatus.USER_CANCEL);
        refundStatusChangeMap.put(0, newRequest);

        Map<Integer, ERefundStatus> waitForDepositTo = new HashMap<>();
        waitForDepositTo.put(ERefundStatus.DEPOSIT.getId(), ERefundStatus.DEPOSIT);
        waitForDepositTo.put(ERefundStatus.REJECT.getId(), ERefundStatus.REJECT);
        refundStatusChangeMap.put(1, waitForDepositTo);

        Map<Integer, ERefundStatus> depositTo = new HashMap<>();
        refundStatusChangeMap.put(2, depositTo);

        Map<Integer, ERefundStatus>rejectTo = new HashMap<>();
        refundStatusChangeMap.put(3, rejectTo);

        Map<Integer, ERefundStatus> userCancelTo = new HashMap<>();
        refundStatusChangeMap.put(4, userCancelTo );

        REFUND_VALID_CHANGE_STATUS_MAP = Collections.unmodifiableMap(refundStatusChangeMap);
    }
 ////////////////////////////////////   ///////////////////////////////////////////////////
}
