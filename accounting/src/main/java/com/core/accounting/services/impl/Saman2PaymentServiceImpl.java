package com.core.accounting.services.impl;

import com.core.accounting.services.AccountingService;
import com.core.accounting.services.PaymentService;
import com.core.accounting.services.PaymentUtilsService;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.services.*;
import com.core.common.services.impl.AbstractPaymentService;
import com.core.common.util.SSLUtilities;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import com.core.accounting.model.dbmodel.BankPayment;
import com.core.datamodel.model.enums.EBank;
import com.core.exception.BankPaymentException;
import com.core.exception.BankPaymentNotResponseException;
import com.core.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("saman2PaymentServiceImpl")
public class Saman2PaymentServiceImpl extends AbstractPaymentService implements PaymentService {

    //private final String bankUrl = "https://sep.shaparak.ir/Payment.aspx";
    //private final String MID = "12393060";

 //saman ip: //   web service 91.240.182.20, 91.240.180.103   - web: 91.240.180.40



    @Autowired
    private com.core.external.bank.saman2.payment.PaymentIFBinding PaymentIFBinding;

    @Autowired
    private com.core.external.bank.saman2.token.PaymentIFBinding tokenIFBinding;

    @Autowired
    AccountingService accountingService;

    @Autowired
    private PaymentUtilsService paymentUtilsService;

    private com.core.external.bank.saman2.payment.PaymentIFBindingSoap paymentIFBindingSoap ;
    private com.core.external.bank.saman2.token.PaymentIFBindingSoap tokenIFBindingSoap ;

    @Autowired
    private CommonService commonService;


/*    @Override
    public String getBankUrl() {
        return bankUrl;
    }*/

    @Override
    public List<GeneralKeyValue> getBankPaymentParams(Long userId,Double amount, String refNumber, String requestNumber, String returnUrl, String paymentMethod, String currency,String notifyUrl, Map<String,String> additional) {
        String res= null;
        try {
            //"پرداخت توسط شناسه" + userId
            res = this.getSamanTokenIPaymentGateway().requestToken(commonService.getSamanBankTerminalId(), requestNumber, amount.longValue(), 0l, 0l, 0l, 0l,
                    0l, 0l, "pay by user Id:" + userId,"", 0l, returnUrl);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس بانک سامان.");
        }
        Integer errorCode=null;
        try {
            errorCode=Integer.valueOf(res);
        }catch (Exception e){
        }

        try {
            if (errorCode!=null)
                checkingTransaction(errorCode);
        }catch (MyException e){
            paymentUtilsService.updateBankResponseByNewTransaction(Long.parseLong(requestNumber), null, -2, null, null, "getToken:"+res, "getToken:"+ e.getMessage(), null);
            throw  e;
        }
        if(res==null)
            throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس بانک سامان و دریافت توکن.");

        paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(requestNumber),null, 0, res,null, "getToken:"+res,   "getToken:success", null);
        List<GeneralKeyValue> result=new ArrayList<>();
        result.add(new GeneralKeyValue("payMethod", "POST"));
        result.add(new GeneralKeyValue("payBankURL", "https://sep.shaparak.ir/Payment.aspx"));
        result.add(new GeneralKeyValue("Token", res));
        result.add(new GeneralKeyValue("RedirectURL", returnUrl));
        return result;


//-------------
/*        List<GeneralKeyValue> result = new ArrayList<>();
        result.add(new GeneralKeyValue("Amount", String.format("%.0f", amount)));//String.format("%.0f",tmpOrder.getSumAmount().toString()));
        result.add(new GeneralKeyValue("MID", MID));
        result.add(new GeneralKeyValue("ResNum", refNumber));
        result.add(new GeneralKeyValue("RedirectURL", returnUrl));
        return result;*/
    }

    @Override
    public BankPaymentResponse fillBankPaymentFromResponse(HttpServletRequest request) throws ServletRequestBindingException {

        /*@RequestParam("CID") String cid,
        @RequestParam("TRACENO") String traceno,
        @RequestParam("RRN") String rrn,
        @RequestParam("SecurePan") String securePan,*/

        BankPaymentResponse bankPaymentResponse = new BankPaymentResponse();
        String orderReferenceNumber=ServletRequestUtils.getStringParameter(request,"myRefId");
        if (orderReferenceNumber==null)
            orderReferenceNumber=ServletRequestUtils.getStringParameter(request,"myrefid");
        bankPaymentResponse.setOrderReferenceNumber(orderReferenceNumber);
        //bankPaymentResponse.setOrderReferenceNumber(ServletRequestUtils.getStringParameter(request,"myRefId"));
        bankPaymentResponse.setBankRequestNumber(ServletRequestUtils.getStringParameter(request,"ResNum"));
        bankPaymentResponse.setBank(EBank.SAMAN);
        bankPaymentResponse.setState(ServletRequestUtils.getStringParameter(request,"State"));
        bankPaymentResponse.setStateCode(ServletRequestUtils.getStringParameter(request,"StateCode"));
        bankPaymentResponse.setMid(ServletRequestUtils.getStringParameter(request,"MID"));
        bankPaymentResponse.setBankReferenceNumber(ServletRequestUtils.getStringParameter(request,"RefNum"));
        bankPaymentResponse.setBankTraceNo(ServletRequestUtils.getStringParameter(request,"TraceNo"));
        return bankPaymentResponse;
    }

    @Override
    public BankPaymentResponse verify(BankPaymentResponse bankPaymentResponse) {
        bankPaymentResponse.setVerified(false);

        BankPayment bankPayment = paymentUtilsService.getBankPaymentInfoByMyRefNum(bankPaymentResponse.getOrderReferenceNumber());
        if (bankPayment.getStatus()==-2) {
            bankPaymentResponse.setStatus(false);
            bankPaymentResponse.setStatusDesc(Utils.getMessageResource("global.payment.unSuccess"));
            return bankPaymentResponse;
        }else if (bankPayment.getStatus()==1) {
            bankPaymentResponse.setSettlement(true);
            return bankPaymentResponse;
        }else if (bankPayment.getStatus()==2){
            bankPaymentResponse.setReverse(true);
            return bankPaymentResponse;
        }else if (bankPayment.getStatus()==3){
            bankPaymentResponse.setRollback(true);
            return bankPaymentResponse;
        }

        //Boolean result=false;
        if (paymentUtilsService.countSuccessPaymentByBankReferenceNumber(bankPaymentResponse.getBankReferenceNumber()) <= 0) {
            if (bankPaymentResponse.getStateCode().equals("0")) {
                SSLUtilities.trustAllHostnames();
                SSLUtilities.trustAllHttpsCertificates();

                Double verifyResult = null;
                try {
                    verifyResult = this.getSamanIPaymentGateway().verifyTransaction(bankPaymentResponse.getBankReferenceNumber(), commonService.getSamanBankTerminalId());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()),null, null, bankPaymentResponse.getBankReferenceNumber(),null,"verifyTransaction:"+verifyResult, "verifyTransaction:"+ e.getMessage(), null);
                    //this.reverse(bankPaymentResponse);
                    throw new BankPaymentNotResponseException("Error In Call bpVerifyRequest",e.getMessage());
                }

                if (verifyResult > 0) {
                    try {
                        if (verifyResult.equals(bankPaymentResponse.getAmount())) //Total Amount of Basket
                        {
                            bankPaymentResponse.setVerified(true);
                            paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, null, bankPaymentResponse.getBankReferenceNumber(),null, "verifyTransaction:" + verifyResult, "verifyTransaction:success", null);
                        } else {
                            bankPaymentResponse.setVerified(false);
                            paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()),null, null, bankPaymentResponse.getBankReferenceNumber(), null,"verifyTransaction:"+verifyResult, "verifyTransaction:مغایرت در مبلغ پرداختی" , null);
                            throw new BankPaymentException("Amount Not Match", "عملیات ناموفق");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()),null, null, bankPaymentResponse.getBankReferenceNumber(),null,"verifyTransaction:"+verifyResult, "Error In Commit chargeTransaction After  verifyTransaction :"+ e.getMessage(), null);
                        this.reverse(bankPaymentResponse);
                        throw e;
                    }
                } else {
                    bankPaymentResponse.setVerified(false);
                    try {
                        checkingTransaction(verifyResult.intValue());
                    }catch (Exception e){
                        paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()),null, null, bankPaymentResponse.getBankReferenceNumber(),null, "verifyTransaction:"+bankPaymentResponse.getState(),   "verifyTransaction:"+ e.getMessage(), null);
                        throw e;
                    }
                }
            } else {
                bankPaymentResponse.setVerified(false);
                try {
                    checkState(bankPaymentResponse.getStateCode());
                }catch (Exception e){
                    paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()),null, null, null,null, "verifyTransaction:"+bankPaymentResponse.getState(),  "verifyTransaction:"+ e.getMessage(), null);
                    throw e;
                }
            }
        }

        return bankPaymentResponse;
    }

    @Override
    public BankPaymentResponse reverse(BankPaymentResponse bankPaymentResponse) {
        Long requestNumber = Utils.parsLong(bankPaymentResponse.getBankRequestNumber());
        //Long saleReferenceId= Utils.parsLong(bankPaymentResponse.getBankTransactionRef());
        Double reverseResult;
        BankPayment bankPayment= paymentUtilsService.getBankPaymentInfo(requestNumber);
        if (bankPayment.getStatus()<=1 && !bankPaymentResponse.getReverse()) {
            try {
                reverseResult = this.getSamanIPaymentGateway().reverseTransaction(bankPaymentResponse.getBankReferenceNumber(),commonService.getSamanBankTerminalId(),commonService.getSamanBankTerminalId(),commonService.getSamanBankPassword());
            } catch (Exception e) {
                e.printStackTrace();
                paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, null,  bankPaymentResponse.getBankReferenceNumber(),null, "reverseTransaction:Error In Reverse Call" , "reverseTransaction:" + e.getMessage(), null);
                throw new BankPaymentException("Error In Bank Payment", e.getMessage());
            }
            this.checkingTransaction(reverseResult.intValue());
            bankPaymentResponse.setReverse(true);
            paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, 2, bankPaymentResponse.getBankReferenceNumber(), null, "reverseTransaction:" + reverseResult, "reverseTransaction:success", null);
        }else if (bankPayment.getStatus()==2) {
            bankPaymentResponse.setReverse(true);
        }else  {
            bankPaymentResponse.setReverse(false);
        }
        return bankPaymentResponse;

    }

    @Override
    public BankPaymentResponse settlement(BankPaymentResponse bankPaymentResponse) {
        bankPaymentResponse.setSettlement(true);
        paymentUtilsService.updateBankResponseInCurrentTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, 1, null, null, "Settlement:success", null, null);
        return bankPaymentResponse;
    }

    @Override
    public BankPaymentResponse refund(BankPaymentResponse bankPaymentResponse) {
        return null;
    }

    @Override
    public BankPaymentResponse isRollbacked(BankPaymentResponse bankPaymentResponse) {
        bankPaymentResponse.setRollback(false);
        return bankPaymentResponse;
    }

    private com.core.external.bank.saman2.payment.PaymentIFBindingSoap getSamanIPaymentGateway() {
        if (paymentIFBindingSoap==null) {
            try {
                paymentIFBindingSoap = PaymentIFBinding.getPaymentIFBindingSoap12();
            } catch (ServiceException e) {
                e.printStackTrace();
                throw new BankPaymentException("Error In Bank Payment", e.getMessage());
            }
        }
        return paymentIFBindingSoap;
    }

    private com.core.external.bank.saman2.token.PaymentIFBindingSoap getSamanTokenIPaymentGateway() {
        if (tokenIFBindingSoap==null) {
            try {
                tokenIFBindingSoap = tokenIFBinding.getPaymentIFBindingSoap12();
            } catch (ServiceException e) {
                e.printStackTrace();
                throw new BankPaymentException("Error In Bank Token", e.getMessage());
            }
        }
        return tokenIFBindingSoap;
    }

    private void checkingTransaction(int i) {

        switch (i) {
            case -1:        //TP_ERROR
                throw new BankPaymentException("TP_ERROR", "خطا در پردازش اطلاعات ارسالی<br/>(مشکل در یکی از ورودیها و ناموفق بودن فراخوانی متد برگشت تراکنش).");
/*           case -2:        //ACCOUNTS_DONT_MATCH
                throw new BankPaymentException("ACCOUNTS_DONT_MATCH", "بروز خطا در هنگام تاييد رسيد ديجيتالي در نتيجه خريد شما تاييد نگرييد.");*/

            case -3:        //BAD_INPUT
                throw new BankPaymentException("BAD_INPUT", "ورودی ها حاوی کاراکتر های غیر مجاز می باشند.");

            case -4:        //INVALID_PASSWORD_OR_ACCOUNT
                throw new BankPaymentException("INVALID_PASSWORD_OR_ACCOUNT", "Merchant Authentication Failed (کلمه عبور یا کد فروشنده اشتباه است).");

                /*            case -5:        //DATABASE_EXCEPTION
                throw new BankPaymentException("DATABASE_EXCEPTION", "خطاي دروني سيستم درهنگام بررسي صحت رسيد ديجيتالي در نتيجه خريد شما تاييد نگرييد");*/

            case -6:        //DATABASE_EXCEPTION
                throw new BankPaymentException("DATABASE_EXCEPTION", "سند قبلاً برگشت کامل یافته است. <br/>یا خارج از زمان ۳۰ دقیقه ارسال شده است.");

            case -7:        //ERROR_STR_NULL
                throw new BankPaymentException("ERROR_STR_NULL", "رسید دیجیتالی تهی است.");

            case -8:        //ERROR_STR_TOO_LONG
                throw new BankPaymentException("ERROR_STR_TOO_LONG", "طول ورودی ها بیش از حد مجاز است.");

            case -9:        //ERROR_STR_NOT_AL_NUM
                throw new BankPaymentException("ERROR_STR_NOT_AL_NUM", "وجود کاراکتر های غیر مجاز در مبلغ برگشتی.");

            case -10:    //ERROR_STR_NOT_BASE64
                throw new BankPaymentException("ERROR_STR_NOT_BASE64", "رسید دیجیتالی به صورت Base64 نیست <br/>(حاوی کاراکتر های غیر مجاز است).");

            case -11:    //ERROR_STR_TOO_SHORT
                throw new BankPaymentException("ERROR_STR_TOO_SHORT", "طول ورودی ها کمتر از حد مجاز است.");

            case -12:        //ERROR_STR_NULL
                throw new BankPaymentException("ERROR_STR_NULL", "مقدار برگشتی منفی است.");

            case -13:        //ERROR IN AMOUNT OF REVERS TRANSACTION AMOUNT
                throw new BankPaymentException("ERROR IN AMOUNT OF REVERS TRANSACTION AMOUNT", "مقدار برگشتی برای برگشت جزئی بیش از مبلغ برگشت نخورده ی رسید دیجیتالی است.");

            case -14:    //INVALID TRANSACTION
                throw new BankPaymentException("INVALID TRANSACTION", "چنین تراکنشی تعریف نشده است.");

            case -15:    //RETERNED AMOUNT IS WRONG
                throw new BankPaymentException("RETERNED AMOUNT IS WRONG", "مبلغ برگشتی به صورت اعشاری داده شده است.");

            case -16:    //INTERNAL ERROR
                throw new BankPaymentException("INTERNAL ERROR", "خطای داخلی سیستم.");

            case -17:    // REVERS TRANSACTIN FROM OTHER BANK
                throw new BankPaymentException("REVERS TRANSACTIN FROM OTHER BANK", "برگشت زدن جزئی تراکنش مجاز نمی باشد.");

            case -18:    //INVALID IP
                throw new BankPaymentException("INVALID IP", "IP Address فروشنده نا معتبر است\nو یا رمز تابع بازگشتی ( )reverseTransaction اشتباه است.");

        }
    }


    private void checkState(String stateCode) {
        if (stateCode.equals("-1") || Utils.isStringSafeEmpty(stateCode))
            throw new BankPaymentException("Canceled By User", "عملیات ناموفق");
        else if (stateCode.equals("3"))
            throw new BankPaymentException("Invalid Merchant", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("5"))
            throw new BankPaymentException("Do Not Honour", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("12"))
            throw new BankPaymentException("Invalid Transaction", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("14"))
            throw new BankPaymentException("Invalid Card Number", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("15"))
            throw new BankPaymentException("No Such Issuer", "کارت نامعتبر است.");
        else if (stateCode.equals("19"))
            throw new BankPaymentException("Reenter Transaction", "عملیات ناموفق");
        else if (stateCode.equals("23"))
            throw new BankPaymentException("UnAcceptable  Transaction Fee", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("30"))
            throw new BankPaymentException("Format Error",  "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("31"))
            throw new BankPaymentException("Bank Not Supported By Switch", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("33"))
            throw new BankPaymentException("Expired Card Pick Up", "کارت نامعتبر است (تاریخ انقضای کارت سپری شده است).");
        else if (stateCode.equals("34"))
            throw new BankPaymentException("Suspected Fraud Pick Up", "عملیات ناموفق");
        else if (stateCode.equals("38"))
            throw new BankPaymentException("Allowable PIN Tries Exceeded Pick Up", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("39"))
            throw new BankPaymentException("No Credit Acount", "کارت نامعتبر است.");
        else if (stateCode.equals("40"))
            throw new BankPaymentException("Requested Function", "عملیات ناموفق");
        else if (stateCode.equals("41"))
            throw new BankPaymentException("Lost Card", "کارت نامعتبر است.");
        else if (stateCode.equals("42"))
            throw new BankPaymentException("No Universal Account","کارت نامعتبر است.");
        else if (stateCode.equals("43"))
            throw new BankPaymentException("Stolen Card", "عملیات ناموفق");
        else if (stateCode.equals("44"))
            throw new BankPaymentException("No Investment Acount", "کارت نامعتبر است.");
        else if (stateCode.equals("51"))
            throw new BankPaymentException("No Sufficient Funds", "موجودی کافی نیست");
        else if (stateCode.equals("52"))
            throw new BankPaymentException("No Cheque Account", "کارت نامعتبر است.");
        else if (stateCode.equals("53"))
            throw new BankPaymentException("No Saving Account", "کارت نامعتبر است.");
        else if (stateCode.equals("54"))
            throw new BankPaymentException("Expired Account", "کارت نامعتبر است (تاریخ انقضای کارت سپری شده است).");
        else if (stateCode.equals("55"))
            throw new BankPaymentException("Incorrect PIN", "رمز نامعتبر است");
        else if (stateCode.equals("56"))
            throw new BankPaymentException("No Card Record", "کارت نامعتبر است.");
        else if (stateCode.equals("57"))
            throw new BankPaymentException("Transaction Not Permitted To CardHolder", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("58"))
            throw new BankPaymentException("Transaction Not Permitted To Terminal", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("61"))
            throw new BankPaymentException("Exceeds Withdrawal Amount Limit", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("62"))
            throw new BankPaymentException("Restricted Card-Decline", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("63"))
            throw new BankPaymentException("Security Violationت", "عملیات ناموفق");
        else if (stateCode.equals("65"))
            throw new BankPaymentException("Exceeds Withdrawal Frequency Limit", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("68"))
            throw new BankPaymentException("Response Received Too Late","-");
        else if (stateCode.equals("75"))
            throw new BankPaymentException("PIN Reties Exceeds-Slm", "تراکنش نامعتبر است \"در صورت کسر وجه از حساب شما، مبلغ مذکور طی  72ساعت به حساب شما عودت داده خواهد شد، در غیر این صورت جهت پیگیری لطفاً با شماره تلفن  021-84080 تماس حاصل نمایید\"");
        else if (stateCode.equals("79"))
            throw new BankPaymentException("Invalid Amount", "عملیات ناموفق");
        else if (stateCode.equals("84"))
            throw new BankPaymentException("Issuer Down Slm", "عملیات ناموفق");
        else if (stateCode.equals("90"))
            throw new BankPaymentException("Cut Off Is Inprogress","عملیات ناموفق (سامانه مقصد تراکنش درحال انجام عملیات پایان روز می باشد)");
        else if (stateCode.equals("93"))
            throw new BankPaymentException("Transaction Cannot Be Completed", "عملیات ناموفق");
        else if (stateCode.equals("96"))
            throw new BankPaymentException("TME Error", "عملیات ناموفق");
    }


}
