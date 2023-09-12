package com.core.common.services.impl.sms;

import com.core.common.model.sms.SmsServiceResponse;
import com.core.common.services.SmsService;
import com.core.common.services.factory.AbstractSmsService;
import com.core.datamodel.model.dbmodel.SmsBodyCode;

public class LinePayamakServiceImpl extends AbstractSmsService implements SmsService {
    @Override
    public SmsServiceResponse send(String[] toNumber, String message, Boolean isFlash) {
        return null;
    }

    @Override
    public SmsServiceResponse send(String fromNumber, String[] toNumber, String message, Boolean isFlash) {
        return null;
    }

    @Override
    public SmsServiceResponse baseSend(String toNumber, String message, Integer bodyId) {
        return null;
    }

    @Override
    public String getDeliverStatus() {
        return null;
    }

    @Override
    public String getMessages() {
        return null;
    }

    @Override
    public String getCredit() {
        return null;
    }

    @Override
    public String getBasePrice() {
        return null;
    }

    @Override
    public String getUserNumber() {
        return null;
    }

    @Override
    public SmsBodyCode getBodyCode(Long smsBodyCodeId) {
        return null;
    }
}
