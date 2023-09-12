package com.core.accounting.services.impl;

import com.core.accounting.model.dbmodel.BankPayment;
import com.core.accounting.services.PaymentService;
import com.core.accounting.services.PaymentUtilsService;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.services.CommonService;
import com.core.common.services.impl.AbstractPaymentService;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import com.core.datamodel.model.enums.EBank;
import com.core.exception.BankPaymentException;
import com.core.exception.BankPaymentNotResponseException;
import com.core.exception.MyException;
import com.core.external.bank.samat.SamatIpgGeneralResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("samatPaymentServiceImpl")
public class SamatPaymentServiceImpl extends AbstractPaymentService implements PaymentService {

    private static Logger logger = LogManager.getLogger(SamatPaymentServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PaymentUtilsService paymentUtilsService;



    private HttpHeaders createHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        //headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Bearer " + commonService.getSamatIpgIdentity());
        return headers;
    }

    private String createSign(String separator,String... signArgs){
        String dataForSign = null;
        for (String arg:signArgs){
            if(Utils.isStringSafeEmpty(dataForSign))
                dataForSign=arg;
            else
                dataForSign=dataForSign + separator + arg;
        }
        return Utils.HMAC_Sign(dataForSign,commonService.getSamatIpgHashAlgorithm(),commonService.getSamatIpgPrivateKey());
    }


    @Override
    public List<GeneralKeyValue> getBankPaymentParams(Long userId, Double amount, String refNumber, String requestNumber, String returnUrl, String paymentMethod, String currency, String notifyUrl, Map<String, String> additional) {
        String samatResultBody="";
        SamatIpgGeneralResponse samatIpgGeneralResponse = null;
        try {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            map.add("amount", amount.longValue());
            map.add("order_id", refNumber);
            map.add("callback", returnUrl);
            map.add("sign", this.createSign("#",String.valueOf(amount.longValue()), refNumber, returnUrl));
            map.add("name", additional.get("firstName"));
            map.add("phone", additional.get("phone"));
            map.add("mail", additional.get("email"));
            map.add("description", "");
            map.add("callback_method", 1);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, this.createHeader());

            try {
                ResponseEntity<String>   samatResult = restTemplate.exchange(commonService.getSamatIpgBaseUrl() + "/create", HttpMethod.POST, entity, String.class);
                samatResultBody=samatResult.getBody();
            } catch (Exception e) {
                if(e instanceof HttpClientErrorException.BadRequest && ((HttpClientErrorException.BadRequest)e).getStatusCode()==HttpStatus.BAD_REQUEST){
                    samatResultBody=((HttpClientErrorException.BadRequest) e).getResponseBodyAsString();
                }else {
                    logger.error(" Error Message =" + e.getMessage());
                    throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس سمات و دریافت توکن.");
                }
            }

            try {
                samatIpgGeneralResponse = jsonMapper.readValue(samatResultBody, SamatIpgGeneralResponse.class);
                if (samatIpgGeneralResponse != null && samatIpgGeneralResponse.getStatus() != null)
                   this.checkStatus(samatIpgGeneralResponse);
                if (samatIpgGeneralResponse == null || samatIpgGeneralResponse.getStatus() == null || !samatIpgGeneralResponse.getStatus().equals(1) ||  samatIpgGeneralResponse.getData()==null)
                    throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس سمات و دریافت توکن.");
            } catch (MyException e) {
                throw e;
            } catch (IOException e) {
                logger.error(" Error Message =" + e.getMessage());
                throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس سمات و دریافت توکن.");
            }

            paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(requestNumber),null, null, samatIpgGeneralResponse.getData().get("ref_num").toString(),null, "getToken:Success",   "getToken:" + (samatIpgGeneralResponse!=null && samatIpgGeneralResponse.getData()!=null? samatIpgGeneralResponse.getData().toString():""), null);
            List<GeneralKeyValue> result=new ArrayList<>();
            result.add(new GeneralKeyValue("payMethod", "POST"));
            result.add(new GeneralKeyValue("payBankURL", commonService.getSamatIpgBaseUrl() + "/payment"));
            result.add(new GeneralKeyValue("token", samatIpgGeneralResponse.getData().get("token").toString()));
            return result;

        }catch (Exception x){
            paymentUtilsService.updateBankResponseByNewTransaction(Long.parseLong(requestNumber), null, -2, null, null, "getToken:UnSuccess", "getToken: "+ x.getMessage() + (samatIpgGeneralResponse!=null && samatIpgGeneralResponse.getData()!=null? samatIpgGeneralResponse.getData().toString():""), null);
            throw x;
        }
    }

    @Override
    public BankPaymentResponse fillBankPaymentFromResponse(HttpServletRequest request) throws ServletRequestBindingException {
        BankPaymentResponse bankPaymentResponse = new BankPaymentResponse();
        String orderReferenceNumber= ServletRequestUtils.getStringParameter(request,"myRefId");
        if (orderReferenceNumber==null)
            orderReferenceNumber=ServletRequestUtils.getStringParameter(request,"myrefid");

        bankPaymentResponse.setBank(EBank.SAMAT);
        bankPaymentResponse.setOrderReferenceNumber(orderReferenceNumber);
        bankPaymentResponse.setBankReferenceNumber(ServletRequestUtils.getStringParameter(request,"ref_num"));
        bankPaymentResponse.setStateCode(ServletRequestUtils.getStringParameter(request,"status"));
        bankPaymentResponse.setBankTransactionRef(ServletRequestUtils.getStringParameter(request,"tracking_code"));
        bankPaymentResponse.getOthers().put("cardNumber",ServletRequestUtils.getStringParameter(request,"card_number")!=null? ServletRequestUtils.getStringParameter(request,"card_number"): "");

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

        paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, null, bankPaymentResponse.getBankReferenceNumber(),bankPaymentResponse.getBankTransactionRef(), "getData:Success" , "getData: "+ (bankPaymentResponse.getOthers()!=null ? bankPaymentResponse.getOthers().toString():""), null);

        if (bankPayment.getStatus()== 0) {
            String samatResultBody="";
            SamatIpgGeneralResponse samatIpgGeneralResponse = null;
            try {
                Integer statusCode=Integer.valueOf(bankPaymentResponse.getStateCode());
                this.checkStatus(statusCode);
                if (!statusCode.equals(1))
                    throw new BankPaymentException("Invalid Result Status", "برگشت وضعیت نا معتبر در پاسخ");

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                map.add("amount", bankPaymentResponse.getAmount().longValue());
                map.add("ref_num", bankPaymentResponse.getBankReferenceNumber());
                map.add("sign", this.createSign("#",String.valueOf(bankPaymentResponse.getAmount().longValue()), bankPaymentResponse.getBankReferenceNumber(),bankPaymentResponse.getOthers().get("cardNumber").toString(), bankPaymentResponse.getBankTransactionRef()));
                HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, this.createHeader());

                try {
                    ResponseEntity<String>   samatResult = restTemplate.exchange(commonService.getSamatIpgBaseUrl() + "/verify", HttpMethod.POST, entity, String.class);
                    samatResultBody=samatResult.getBody();
                } catch (Exception e) {
                    if(e instanceof HttpClientErrorException.BadRequest && ((HttpClientErrorException.BadRequest)e).getStatusCode()==HttpStatus.BAD_REQUEST){
                        samatResultBody=((HttpClientErrorException.BadRequest) e).getResponseBodyAsString();
                    }else {
                        logger.error(" Error Message =" + e.getMessage());
                        throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس سمات و اعتبارسنجی پرداخت.");
                    }
                }

                try {
                    samatIpgGeneralResponse = jsonMapper.readValue(samatResultBody, SamatIpgGeneralResponse.class);
                    if (samatIpgGeneralResponse != null && samatIpgGeneralResponse.getStatus() != null)
                        this.checkStatus(samatIpgGeneralResponse);
                    if (samatIpgGeneralResponse == null || samatIpgGeneralResponse.getStatus() == null || !samatIpgGeneralResponse.getStatus().equals(1) ||  samatIpgGeneralResponse.getData()==null)
                        throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس سمات و اعتبارسنجی پرداخت.");

                    Double price=samatIpgGeneralResponse.getData().get("price")!=null?(Double)samatIpgGeneralResponse.getData().get("price"):0;
                    String verifyRefNum=samatIpgGeneralResponse.getData().get("ref_num")!=null? samatIpgGeneralResponse.getData().get("ref_num").toString():"";
                    if(price!=bankPaymentResponse.getAmount() || verifyRefNum.equals(bankPaymentResponse.getBankReferenceNumber()))
                        throw new BankPaymentException("input data Mismatch", "مغایرت در اطلاعات دریافتی");
                    bankPaymentResponse.setVerified(true);
                    paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, null, bankPaymentResponse.getBankReferenceNumber(),bankPaymentResponse.getBankTransactionRef(), "verifyTransaction:Success" , "verifyTransaction:"+ (samatIpgGeneralResponse!=null && samatIpgGeneralResponse.getData()!=null? samatIpgGeneralResponse.getData().toString():""), null);
                } catch (MyException e) {
                    throw e;
                } catch (IOException e) {
                    logger.error(" Error Message =" + e.getMessage());
                    throw new BankPaymentException("Remote Connection Exception", "اشکال در اتصال به سرویس سمات و اعتبارسنجی پرداخت.");
                }
            }catch (Exception ex){
                ex.printStackTrace();
                paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, null, null, null, "unSuccess"  , "verify:Error " + ex.getMessage(), null);
                this.reverse(bankPaymentResponse);
                throw new BankPaymentNotResponseException("Error In Call VerifySamat", ex.getMessage());
            }
        }
        return bankPaymentResponse;
    }

    @Override
    public BankPaymentResponse reverse(BankPaymentResponse bankPaymentResponse) {
        bankPaymentResponse.setReverse(false);
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


    private void checkStatus(SamatIpgGeneralResponse res) {
        if(res.getStatus()==-1)
            logger.error("Error Code:-1" + "  Error Message " + res.getMessage() + " , Data: " + res.getData().toString());
        this.checkStatus(res.getStatus());
    }

    private void checkStatus(Integer status) {
        switch (status) {
            case -1:        // invalidRequest
                throw new BankPaymentException("invalidRequest", "درخواست نامعتبر (خطا در پارامترهای ورودی)");
           case -2:        //inactiveGateway
                throw new BankPaymentException("inactiveGateway", "درگاه فعال نیست");
            case -3:        //retryToken
                throw new BankPaymentException("retryToken", "توکن تکراری است");
            case -4:        //amountLimitExceed
                throw new BankPaymentException("amountLimitExceed", "مبلغ بیشتر از سقف مجاز درگاه است");
            case -5:        //invalidRefNum
                throw new BankPaymentException("invalidRefNum", "شناسه ref_num معتبر نیست");
            /*case -6:        //retryVerification
                throw new BankPaymentException("retryVerification", "تراکنش قبلا وریفای شده است");*/
            case -7:        //badData
                throw new BankPaymentException("badData", "پارامترهای ارسال شده نامعتبر است");
            case -8:        //trNotVerifiable
                throw new BankPaymentException("trNotVerifiable", "تراکنش را نمیتوان وریفای کرد");
            case -9:        //trNotVerified
                throw new BankPaymentException("trNotVerified", "تراکنش وریفای نشد");
            case -98:        //paymentFailed
                throw new BankPaymentException("paymentFailed", "تراکنش ناموفق");
            case -99:        //error
                throw new BankPaymentException("error", "خطای سامانه");
        }
    }

}
