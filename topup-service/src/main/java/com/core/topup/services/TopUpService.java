package com.core.topup.services;

import com.core.accounting.model.wrapper.OperationRequestWrapper;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.model.wrapper.TypeWrapper;
import com.core.model.wrapper.ResultListPageable;
import com.core.topup.model.contextmodel.TopUpReserveRequestDto;
import com.core.topup.model.dbmodel.TopUpRequest;
import com.core.topup.model.enums.EOperator;
import com.core.topup.model.topupmodel.DirectChargeType;
import com.core.topup.model.topupmodel.InternetPackageDetailResponse;
import com.core.topup.model.topupmodel.MciProductDetailResponse;
import com.core.topup.model.wrapper.*;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.web.bind.ServletRequestBindingException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface TopUpService {
    //region External Service
    String getToken(Boolean forceFetch,String processId);
    TopUpSaleResult reserveAndSale(TopUpReserveRequestDto topUpReserveRequestDto, HttpServletRequest request) ;
    BankPaymentResponse compositeSaleAfterOnlinePayment(HttpServletRequest request) throws ServletRequestBindingException ;

    void checkingUnknownTopUpRequest();
    //endregion External Service


    //region Internal Business
    ResultListPageable<TopUpRequestWrapper> getTopUpRequestWrappers(Map<String, Object> requestParams );
    TopUpRequestWrapper getTopUpRequestWrapperInfo(Long topUpRequestId );
    TopUpRequestWrapper getTopUpRequestWrapperInfoByOperationRequestWrapper(Long operationRequestId );
    OperationRequestWrapper getTopUpOperationRequestWrapperInfo(Long operationRequestId);
    //endregion Internal Business


    //region Base Data
    List<TypeWrapper> getOperators();
    List<DirectChargeData> getAllDirectChargeType();
    List<OperatorDirectCharge> getDirectChargeType(EOperator eOperator);
    List<InternetPackageData> getAllInternetPackageData();
    List<SimType> getInternetPackageData(EOperator eOperator);
    List<TypeWrapper> getTopUpRequestStatuses();

    List<MciProductDetailResponse> getMciProductList(Integer productTypeId);
    List<InternetPackageDetailResponse> getInternetPackageList(EOperator eOperator);
    //endregion Base Data

}
