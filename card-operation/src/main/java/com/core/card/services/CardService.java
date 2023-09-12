package com.core.card.services;

import com.core.card.model.cardmodel.*;
import com.core.card.model.contextmodel.BankCardDto;
import com.core.card.model.contextmodel.BankCardHolderInquiryDto;
import com.core.card.model.contextmodel.BankCardTransferDto;
import com.core.card.model.contextmodel.OtpDeliveryDto;
import com.core.card.model.dbmodel.BankCard;
import com.core.card.model.wrapper.BankCardOperationWrapper;
import com.core.card.model.wrapper.BankCardWrapper;
import com.core.common.services.Service;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface CardService extends Service {

    //#region  Create Bank  Cart
    BankCard getBankCardInfo(Long bankCardId);
    BankCard getBankShpCardInfo(Long shpCardId);
    BankCard getBankCardInfo(String cardNumber);
    List<BankCard> getMyBankCards(Integer type);
    List<BankCard> getBankCards(Long userId,Integer type);

    BankCardWrapper getBankCardWrapperInfo(Long bankCardId);
    List<BankCardWrapper> getMyBankCardWrappers(Integer type);
    List<BankCardWrapper> getBankCardWrappers(Long userId,Integer type);

    BankCardWrapper createBankCard(BankCardDto bankCardDto);
    String editBankCard(BankCardDto bankCardDto);
    String deleteBankCard(Long bankCardId);

    //#endregion Create Bank Cart

    //#region Card Operation

    ResultListPageable<BankCardOperationWrapper> getBankCardOperationWrappers(Map<String, Object> requestParams);
    BankCardOperationWrapper getBankCardOperationWrapperInfo(Long bankCardOperationId );
    List<TypeWrapper> getCardOperatorStatuses();

    //#endregion Card Operation

    //#region SHP Card Operation

    String getTransactionIdByTrackingId(String trackingId);
    CardEnrollmentResponse cardEnrollment(String platform);
    BankCardWrapper approvedCardEnrollment(String trackingId);
    BankCard renewCardId(Long bankCardId);
    AppReactivationResponse appReactivation(String platform);

    CardHolderInquiryResponse cardHolderInquiry(BankCardHolderInquiryDto bankCardHolderInquiryDto, HttpServletRequest request);
    CardTransferResponse cardTransfer(BankCardTransferDto bankCardTransferDto, HttpServletRequest request);
    Integer getShpOtpRemainTime();
    OtpDeliveryResponse requestOtp(OtpDeliveryDto otpDeliveryDto, HttpServletRequest request);

    //#endregion SHP Card Operation



}
