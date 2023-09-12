package com.core.accounting.services.factory;

import com.core.accounting.services.impl.*;
import com.core.exception.InvalidDataException;
import com.core.datamodel.model.enums.EBank;
import com.core.accounting.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("PaymentServiceFactory")
public class PaymentServiceFactory {

    @Autowired
    private Saman2PaymentServiceImpl saman2PaymentService;

    @Autowired
    private SamatPaymentServiceImpl samatPaymentService;


    public PaymentService getPaymentService(EBank eBank){
        if (eBank==EBank.SAMAN)
            return saman2PaymentService;
        else if(eBank==EBank.SAMAT)
            return samatPaymentService;
        else
            throw new InvalidDataException("Bank Payment Error", "Payment Service not fount for this Bank");

    }
}
