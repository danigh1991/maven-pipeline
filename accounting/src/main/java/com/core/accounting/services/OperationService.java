package com.core.accounting.services;

import com.core.accounting.model.contextmodel.*;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.wrapper.*;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.services.Service;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.core.datamodel.model.enums.ENotifyTargetType;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.model.wrapper.QRCodeDataWrapper;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.web.bind.ServletRequestBindingException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface OperationService extends Service {

    OperationType getOperationTypeInfo(Long operationId);
    OperationType getOperationTypeInfoByCode(Integer code);
    OperationTypeWrapper getActiveOperationTypeWrappersByName(String operationTypeName);
    OperationTypeWrapper getActiveOperationTypeWrappersByCode(Integer code);
    OperationType editOperationType(OperationTypeDto operationTypeDto);
    List<OperationTypeWrapper> getAllOperationTypeWrappers();
    List<OperationTypeWrapper> getAllAccountOperationTypeWrappers();

    Boolean getOperationTypeSendNotifyStatusByCode(Integer code);
    //Boolean getOperationTypeSendNotifyStatusForAccountByCode(Long accountId,Integer code);


    BankPaymentResponse onlineCharge(BankPaymentResponse bankPaymentResponse);
    EOperation getOperationTypeByOperationRequestId(Long operationRequestId);
    OperationRequest getOperationRequestInfo(Long operationRequestId,String referenceNumber);
    OperationRequest getOperationRequestInfo(Long operationRequestId,String referenceNumber, Boolean forceOwner);
    OperationRequest getOperationRequestInfo(String referenceNumber);
    OperationRequest getOperationRequestInfo(String referenceNumber, Boolean forceOwner);
    OperationRequest getOperationRequestInfo(Long operationRequestId);
    OperationRequest getOperationRequestInfo(Long operationRequestId, Boolean forceOwner);
    OperationRequestWrapper getOperationRequestWrapperInfo(Long operationRequestId);
    OperationRequestWrapper getOperationRequestWrapperInfo(String referenceNumber);
    //List<OperationRequestWrapper> getMyOperationRequestWrappers(Map<String,Object> requestParams);
    //List<OperationRequestWrapper> getOperationRequestWrappers1(Map<String,Object> requestParams);
    ResultListPageable<OperationRequestWrapper> getOperationRequestWrappers(Map<String, Object> requestParams);
    List<TypeWrapper> getOperationRequestStatuses();


    QRCodeDataWrapper getUserQRCodeData(String qrCodeText);
    OperationTypeWrapper checkAllowOperationTo(Integer operationTypeCode,String userName,Boolean systemic);
    Boolean checkAllowOperationTo(Integer operationTypeCode,Long accountId,Boolean systemic);
    Map<String,Object> getAvailableAccountWrappersByOperationTypeCode(OperationRequestTargetDto operationRequestTargetDto);
    Map<String,Object> getAvailableAccountWrappersByOperationTypeCode(OperationRequestTargetDto operationRequestTargetDto,Long userId);


    OperationRequestResult prepareOperationRequestOnlinePayment(Long operationRequestId,String referenceNumber);
    OperationRequestResult operationRequestCall(OperationRequestDto operationRequestDto);
    OperationRequestResult operationRequestCall(OperationRequestDto operationRequestDto, Long userId, Map<String, Object> additionalData);
    OperationRequestResult operationRequestCall(String platform,Integer operationTypeCode,String description,Long transactionTypeId,Long fromUserId ,Long toUserId , Double amount, Long userId, Map<String, Object> additionalData);



    void calculateAndGetWageFromAccount(OperationType operationType,Long userId, Long accountId ,Double amount,String description,String referenceNumber, Long orderId, Long operationRequestId, Boolean negativeAllow);
    void getWageFromAccount(OperationType operationType,Long userId, Long accountId ,Double wageAmount,String description,String referenceNumber, Long orderId, Long operationRequestId, Boolean negativeAllow);
    Double calculateWage(Integer operationTypeCode,Double amount);

    BankPaymentResponse commitAfterOnlinePayment(HttpServletRequest request) throws ServletRequestBindingException;
    BankPaymentResponse commitAfterOnlinePayment(HttpServletRequest request,Boolean chargeOnly) throws ServletRequestBindingException;
    BankPaymentResponse commitAfterOnlineStripePayment(HttpServletRequest request) throws ServletRequestBindingException;
    BankPaymentResponse commitAfterOnlineStripePayment(HttpServletRequest request,Boolean chargeOnly) throws ServletRequestBindingException;
    BankPaymentResponse chargeAccountAndCommitOperationRequest(BankPaymentResponse bankPaymentResponse);
    BankPaymentResponse chargeAccount(BankPaymentResponse bankPaymentResponse);





//region DepositRequest

    DepositRequest getDepositRequestInfo(Long depositRequestId);
    DepositRequestWrapper getDepositRequestWrapperInfo(Long depositRequestId);
    List<DepositRequestWrapper> getDepositRequestWrappers(Map<String,Object> requestParams);

    String createDepositRequest(DepositRequestDto depositRequestDto);
    String editDepositRequest(DepositRequestDto depositRequestDto);
    String removeDepositRequest(Long depositRequestId);

    DepositRequestDetail getDepositRequestDetailInfo(Long depositRequestDetailId);
    DepositRequestDetail getDepositRequestDetailInfoForReceiverUser(Long depositRequestDetailId);
    DepositRequestDetailWrapper getDepositRequestDetailWrapperInfo(Long depositRequestDetailId);



    Map<String,Object> allowDepositRequest( String targetUserName);

    MessageBoxWrapper getDetailMessageWrapperInfo(Integer notifyTargetTypeId, Long targetId);
    MessageBoxWrapper getDepositRequestDetailMessageWrapperInfo(Long depositRequestDetailId);
    String addDepositRequestDetail(DepositRequestDetailNewDto depositRequestDetailNewDto);
    String editDepositRequestDetail(DepositRequestDetailDto depositRequestDetailDto);
    String removeDepositRequestDetail(Long depositRequestDetailId);
    void seenDepositRequestDetail(Long depositRequestDetailId);
    void doneDepositRequestDetail(Long depositRequestDetailId,Double amount);
    DepositRequestDetail validatePreDoneDepositRequestDetail(Long depositRequestDetailId, Double amount) ;

    Integer linkDepositRequest(String userName,Long usrId);
//endregion DepositRequest






}
