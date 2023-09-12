package com.core.common.services;

import com.core.datamodel.model.dbmodel.SmsBodyCode;
import com.core.common.model.sms.SmsServiceResponse;

public interface SmsService {

    SmsServiceResponse send( String[] toNumber, String message, Boolean isFlash);
    SmsServiceResponse send(String fromNumber, String[] toNumber, String message, Boolean isFlash);
    SmsServiceResponse baseSend( String toNumber, String message, Integer bodyId);
    String getDeliverStatus();
    String getMessages();
    String getCredit();
    String getBasePrice();
    String getUserNumber();


    SmsBodyCode getBodyCode(Long smsBodyCodeId);

}
