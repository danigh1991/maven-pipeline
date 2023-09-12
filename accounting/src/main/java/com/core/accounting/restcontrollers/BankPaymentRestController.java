package com.core.accounting.restcontrollers;


import com.core.accounting.services.impl.PaymentUtilsServiceImpl;
import com.core.common.util.Utils;
import com.core.datamodel.model.view.MyJsonView;
import com.core.responsemodel.ResponseMessage;
import com.core.restcontrollers.AbstractRestController;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/bankPayment", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankPaymentRestController extends AbstractRestController {

    private PaymentUtilsServiceImpl paymentUtilsService;

    @Autowired
    public BankPaymentRestController(PaymentUtilsServiceImpl paymentUtilsService) {
        this.paymentUtilsService = paymentUtilsService;
    }


    //region Bank
    @JsonView(MyJsonView.BankList.class)
    @RequestMapping(value = "/getAllBanks", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank')")
    public ResponseMessage getAllBanks() {
        return Utils.returnSucsess(paymentUtilsService.getAllBanks());
    }
    //endregion Bank

    //region BankPayment

    @JsonView(MyJsonView.BankPaymentList.class)
    @RequestMapping(value = "/getBankPaymentWrappers", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_payment')")
    public ResponseMessage getBankPaymentWrappers(@RequestParam(required = false) Map<String, Object> requestParams) {
        return Utils.returnSucsess(paymentUtilsService.getBankPaymentWrappers(requestParams));
    }

    @JsonView(MyJsonView.BankPaymentDetails.class)
    @RequestMapping(value = "/getBankPaymentWrapperInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_payment')")
    public ResponseMessage getBankPaymentWrapperInfo(@RequestParam Long bankPaymentId) {
        return Utils.returnSucsess(paymentUtilsService.getBankPaymentWrapperInfo(bankPaymentId));
    }

    @JsonView(MyJsonView.TypeWrapperList.class)
    @RequestMapping(value = "/getBankPaymentStatuses", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_payment')")
    public ResponseMessage getBankPaymentStatuses() {
        return Utils.returnSucsess(paymentUtilsService.getBankPaymentStatuses());
    }

    //endregion BankPayment
}
