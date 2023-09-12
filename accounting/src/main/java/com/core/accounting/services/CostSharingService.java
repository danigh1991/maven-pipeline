package com.core.accounting.services;

import com.core.accounting.model.contextmodel.*;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.wrapper.*;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.services.Service;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import org.springframework.web.bind.ServletRequestBindingException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface CostSharingService extends Service {

//region CostShareRequest

    CostShareType getCostShareTypeInfo(Long costShareTypeId);
    List<CostShareType> getAllCostShareTypes();
    List<CostShareType> getAllActiveCostShareTypes();


    CostShareRequest getCostShareRequestInfo(Long costShareRequestId);
    CostShareRequestWrapper getCostShareRequestWrapperInfo(Long costShareRequestId);
    List<CostShareRequestWrapper> getCostShareRequestWrappers(Map<String, Object> requestParams);

    String createCostShareRequest(CostShareRequestDto costShareRequestDto);
    String editCostShareRequest(CostShareRequestDto costShareRequestDto);
    String removeCostShareRequest(Long costShareRequestId);

    CostShareRequestDetail getCostShareRequestDetailInfo(Long costShareRequestDetailId);
    CostShareRequestDetail getCostShareRequestDetailInfoForReceiverUser(Long costShareRequestDetailId);
    CostShareRequestDetailWrapper getCostShareRequestDetailWrapperInfo(Long costShareRequestDetailId);

    MessageBoxWrapper getCostShareRequestDetailMessageWrapperInfo(Long costShareRequestDetailId);
    String addCostShareRequestDetail(CostShareRequestDetailNewDto costShareRequestDetailNewDto);
    String editCostShareRequestDetail(CostShareRequestDetailDto costShareRequestDetailDto);
    String removeCostShareRequestDetail(Long costShareRequestDetailId);
    void seenCostShareRequestDetail(Long costShareRequestDetailId);
    void doneCostShareRequestDetail(Long costShareRequestDetailId, Double amount);
    CostShareRequestDetail validatePreDoneCostShareRequestDetail(Long costShareRequestDetailId, Double amount);



    CostDetail getCostDetailInfo(Long costDetailId);
    CostDetailWrapper getCostDetailWrapperInfo(Long costDetailId);
    List<CostDetailWrapper> getCostDetailWrappers(Long costShareRequestId);
    List<CostDetailWrapper> getCostDetailWrappers(Long costShareRequestId, Boolean forceOwner);

    String createCostDetail(CostDetailDto costDetailDto);
    String editCostDetail(CostDetailDto costDetailDto);
    String removeCostDetail(Long costDetailId);

//endregion CostShareRequest




}
