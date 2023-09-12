package com.core.accounting.services;

import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.datamodel.model.contextmodel.GeneralBankObject;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestBindingException;
import javax.servlet.http.HttpServletRequest;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface PaymentService {

     // String getBankUrl();
      List<GeneralKeyValue> getBankPaymentParams(Long userId, Double amount, String refNumber, String requestNumber, String returnUrl, String paymentMethod, String currency, String notifyUrl, Map<String,String> additional);
      BankPaymentResponse fillBankPaymentFromResponse(HttpServletRequest request) throws ServletRequestBindingException;

      BankPaymentResponse verify(BankPaymentResponse bankPaymentResponse);
      BankPaymentResponse reverse(BankPaymentResponse bankPaymentResponse);
      BankPaymentResponse settlement(BankPaymentResponse bankPaymentResponse);
      BankPaymentResponse refund(BankPaymentResponse bankPaymentResponse);
      BankPaymentResponse isRollbacked(BankPaymentResponse bankPaymentResponse);

}
