package com.core.accounting.services;

import com.core.accounting.model.dbmodel.BankPayment;
import com.core.accounting.model.wrapper.BankPaymentWrapper;
import com.core.datamodel.model.contextmodel.GeneralBankObject;
import com.core.datamodel.model.dbmodel.Bank;
import com.core.datamodel.model.enums.EBank;
import com.core.model.wrapper.ResultListPageable;

import java.util.List;
import java.util.Map;

public interface PaymentUtilsService {


    //#region  Bank
    List<Bank> getAllBanks();
    //#endregion Bank


    //#region  Bank Payment
    BankPayment getBankPaymentInfo(Long bankPaymentId);
    BankPayment getBankPaymentInfoByMyRefNum(String myRefNum);
    Integer countSuccessPaymentByBankReferenceNumber(String bankReferenceNumber);
    Integer countSuccessByMyReferenceNumber(String myReferenceNumber);
    BankPayment createBankResponse(Long userId,Long accountId,Double amount , EBank eBank, Long transactionId, Integer status, String bankReferenceNumber, String bankTransactionRef, String bankStatus, String bankResponse, String myReferenceNumber, Long orderId,String shipmentIds,Long operationRequestId);
    void updateBankResponseByNewTransaction(Long bankPaymentId, Long transactionId, Integer status, String bankReferenceNumber, String bankTransactionRef, String bankStatus, String bankResponse, Long orderId);
    void updateBankResponseInCurrentTransaction(Long bankPaymentId, Long transactionId, Integer status, String bankReferenceNumber, String bankTransactionRef, String bankStatus, String bankResponse, Long orderId);
    List<BankPayment> getBankPayments(Integer status, Integer start, Integer count);
    GeneralBankObject createBankPaymentRequest(GeneralBankObject generalBankObject, Double payAmount, EBank eBank, String referenceNumber, Map<String,Object> additional);

    ResultListPageable<BankPaymentWrapper> getBankPaymentWrappers(Map<String, Object> requestParams);
    BankPaymentWrapper getBankPaymentWrapperInfo(Long bankPaymentId);
    //#endregion


}
