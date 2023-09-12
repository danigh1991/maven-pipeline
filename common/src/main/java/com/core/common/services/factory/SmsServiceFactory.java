package com.core.common.services.factory;

import com.core.common.model.enums.ESmsProvider;
import com.core.common.services.SmsService;
import com.core.common.services.impl.sms.MelliPayamakServiceImpl;
import com.core.datamodel.model.enums.EBank;
import com.core.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("smsServiceFactory ")
public  class SmsServiceFactory {

    @Autowired
    private MelliPayamakServiceImpl melliPayamakService;


    public SmsService getSmsService(){
        //todo read from config
        ESmsProvider eSmsProvider= ESmsProvider.MELLI_PAYAMAK;

        if (eSmsProvider==ESmsProvider.MELLI_PAYAMAK)
            return melliPayamakService;
        else if(eSmsProvider==ESmsProvider.MELLI_PAYAMAK)
            return melliPayamakService;;

        throw new InvalidDataException("Bank Payment Error", "Sms Service not fount for " + eSmsProvider.toString());
    }
}
