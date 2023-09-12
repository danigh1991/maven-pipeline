package com.core.topup.services.impl;

import com.core.accounting.model.dbmodel.OperationRequest;
import com.core.accounting.model.dbmodel.TransactionType;
import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.enums.ETransactionType;
import com.core.accounting.model.wrapper.OperationRequestResult;
import com.core.accounting.model.wrapper.OperationRequestWrapper;
import com.core.accounting.services.AccountingService;
import com.core.accounting.services.OperationService;
import com.core.common.config.condition.MainInstanceContition;
import com.core.common.externalapi.ExternalApiHelper;
import com.core.common.externalapi.ExternalApiResponse;
import com.core.common.externalapi.ExternalApiUtil;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.model.enums.EExternalApi;
import com.core.common.services.CommonService;
import com.core.common.services.RetryableService;
import com.core.common.services.impl.AbstractService;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.services.CacheService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.model.wrapper.ResultListPageable;
import com.core.topup.exception.TopUpException;
import com.core.topup.model.contextmodel.TopUpReserveRequestDto;
import com.core.topup.model.dbmodel.TopUpRequest;
import com.core.topup.model.enums.EOperator;
import com.core.topup.model.enums.ETopUpRequestStatus;
import com.core.topup.model.enums.ETopUpType;
import com.core.topup.model.topupmodel.*;
import com.core.topup.model.wrapper.*;
import com.core.topup.services.TopUpService;
import com.core.util.search.SearchCriteria;
import com.core.util.search.SearchCriteriaParser;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.client.HttpClientErrorException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("topUpServiceImpl")
public class TopUpServiceImpl extends AbstractService implements TopUpService {

    private ExternalApiHelper externalApiHelper;
    private ExternalApiUtil externalApiUtil;
    private OperationService operationService;
    private AccountingService accountingService;
    private TopUpUtil topUpUtil;
    private CacheService cacheService;
    private CommonService commonService;
    private RetryableService retryableService;
    private DynamicQueryHelper dynamicQueryHelper;

    @Value("#{${topUpRequest.search.native.private.params}}")
    private HashMap<String, List<String>> TOPUP_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;


    private static Map<Integer, EOperation> LOCAL_OPERATIONS;
    static {
        LOCAL_OPERATIONS = new HashMap<>();
        LOCAL_OPERATIONS.put(1060,EOperation.valueOfCode(1060));
        LOCAL_OPERATIONS.put(1070,EOperation.valueOfCode(1070));
    }



    @Autowired
    public TopUpServiceImpl(ExternalApiHelper externalApiHelper,ExternalApiUtil externalApiUtil, OperationService operationService,
                            TopUpUtil topUpUtil,CacheService cacheService,CommonService commonService,RetryableService retryableService,
                            DynamicQueryHelper dynamicQueryHelper,AccountingService accountingService) {
        this.externalApiHelper = externalApiHelper;
        this.externalApiUtil = externalApiUtil;
        this.operationService = operationService;
        this.accountingService = accountingService;
        this.topUpUtil = topUpUtil;
        this.cacheService = cacheService;
        this.commonService = commonService;
        this.retryableService = retryableService;
        this.dynamicQueryHelper = dynamicQueryHelper;
    }

//region External Service

    private HttpHeaders generateHeaderForAuthorization(Boolean forceFetchToken,String processId){
        HttpHeaders headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization","Bearer " + this.getToken(forceFetchToken,processId));
        return headers;
    }


    @Transactional
    @Override
    public String getToken(Boolean forceFetch,String processId) {
        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.TOPUP_TOKEN.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null,processId);


        LinkedMap<String, String> loginRequest= new LinkedMap<String, String>();
        loginRequest.put("grant_type", "password");
        loginRequest.put("username", externalApi.getKey());
        loginRequest.put("password",externalApi.getSecret());
        loginRequest.put("client_id",externalApi.getClientId());


        HttpHeaders headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        headers.add("content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<LinkedMap<String, String>> entity = new HttpEntity<>(loginRequest, headers);
        //HttpEntity entity = new HttpEntity(headers);
        ExternalApiResponse<TopUpTokenResponse> externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,0,null, entity, new ParameterizedTypeReference<TopUpTokenResponse>(){}, forceFetch);
        //((HttpClientErrorException.Unauthorized) e).getStatusCode().value
        try {
            TopUpTokenResponse topUpTokenResponse =externalApiResponse.getResponseEntity() instanceof TopUpTokenResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),TopUpTokenResponse.class);
            externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,null);
            return topUpTokenResponse.getAccess_token();

        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage(),null);
            throw e;
        }
    }



    @Transactional
    @Override
    public TopUpSaleResult reserveAndSale(TopUpReserveRequestDto topUpReserveRequestDto, HttpServletRequest request) {
        if (LOCAL_OPERATIONS.get(topUpReserveRequestDto.getOperationTypeCode())==null)
            throw new InvalidDataException("Invalid Data", "common.operationType.code_invalid");

        EOperator eOperator= EOperator.valueOf(topUpReserveRequestDto.getOperatorId());
        if((topUpReserveRequestDto.getChargeType()!=null && topUpReserveRequestDto.getPackageType()!=null) || topUpReserveRequestDto.getChargeType()==null && topUpReserveRequestDto.getPackageType()==null ){
            throw new InvalidDataException("Invalid Data", "common.topUp.type_invalid");
        }
        EOperation eOperation=EOperation.valueOfCode(topUpReserveRequestDto.getOperationTypeCode());
        if (eOperation==EOperation.MOBILE_CHARGE && Utils.isStringSafeEmpty(topUpReserveRequestDto.getChargeType()))
            throw new InvalidDataException("Invalid Data", "common.topUp.chargeType_invalid");

        if (eOperation==EOperation.INTERNET_CHARGE && topUpReserveRequestDto.getPackageType()==null)
            throw new InvalidDataException("Invalid Data", "common.topUp.packType_invalid");

        ETopUpType eTopUpType=ETopUpType.CHARGE;
        if (eOperation==EOperation.INTERNET_CHARGE)
            eTopUpType=ETopUpType.INTERNET_PACKAGE;

        if(topUpReserveRequestDto.getToAccounts().size()!=1){
            throw new InvalidDataException("Invalid Data", "common.topUp.targetId_invalid");
        }

        if(!topUpReserveRequestDto.getToAccounts().get(0).getAccountId().equals(commonService.getConfigValue(eOperator.getTargetAccountConfigName()))){
            throw new InvalidDataException("Invalid Data", "common.topUp.targetId_invalid");
        }
        topUpReserveRequestDto.setAmount(topUpReserveRequestDto.getToAccounts().get(0).getAmount());

        String description="";
        if(eTopUpType==ETopUpType.CHARGE) {
           List<OperatorDirectCharge> chargeTypeListMap = this.getDirectChargeType(eOperator).stream().filter(c -> c.getDirectChargeType().getCode().equals(topUpReserveRequestDto.getChargeType())).collect(Collectors.toList());
           if (chargeTypeListMap == null || chargeTypeListMap.size() == 0)
               throw new InvalidDataException("Invalid Data", "common.topUp.chargeType_invalid");

           description= chargeTypeListMap.get(0).getDirectChargeType().getDescription();
            //uncomment after mci new service
           /*if (EOperator.MCI == eOperator) {
               List<MciProductDetailResponse> productDetails = chargeTypeListMap.get(0).getProductDetails();
               if (!productDetails.stream().anyMatch(t -> t.getId().equals(topUpReserveRequestDto.getProductId()) && (t.getAmount() == null || t.getAmount().equals(0d) || t.getAmount().equals(topUpReserveRequestDto.getAmount()))))
                   throw new InvalidDataException("Invalid Data", "common.topUp.productId_error");
           }*/
        }else if(eTopUpType==ETopUpType.INTERNET_PACKAGE) {
            List<InternetPackageDetailResponse> internetPackageDetailResponses= this.getInternetPackageList(eOperator).stream().filter(t -> t.getPackage_type().equals(topUpReserveRequestDto.getPackageType()) &&  t.getPackage_cost().equals(topUpReserveRequestDto.getAmount())).collect(Collectors.toList());
            if (internetPackageDetailResponses == null || internetPackageDetailResponses.size() == 0)
                throw new InvalidDataException("Invalid Data", "common.topUp.packageType_notFound");
            /*if (!this.getInternetPackageList(eOperator).stream().anyMatch(t -> t.getPackage_type().equals(topUpReserveRequestDto.getPackageType()) &&  t.getPackage_cost().equals(topUpReserveRequestDto.getAmount())))
                throw new InvalidDataException("Invalid Data", "common.topUp.packageType_notFound");*/
            description= internetPackageDetailResponses.get(0).getPackage_desc();
        }

        TopUpRequest topUpRequest=topUpUtil.createTopUpRequest(topUpReserveRequestDto,description,eTopUpType,request);
        topUpRequest=this.reserveCharge(topUpRequest);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("referenceNumber",topUpRequest.getTrackingId());
        OperationRequestResult operationRequestResult= operationService.operationRequestCall(topUpReserveRequestDto, Utils.getCurrentUserId(),additionalData);
        TopUpSaleResult topUpSaleResult=new TopUpSaleResult(operationRequestResult);
        if(!Utils.isStringSafeEmpty(topUpSaleResult.getRedirectUrl())) {
            topUpUtil.setOperationRequestIdInTopUpRequest(topUpRequest.getId(),operationRequestResult.getOperationRequestId());
            return topUpSaleResult;
        }

        topUpRequest=this.compositeSale(topUpRequest);
        topUpUtil.setOperationRequestIdInTopUpRequest(topUpRequest.getId(),operationRequestResult.getOperationRequestId());
        topUpSaleResult.setTopUpRequestId(topUpRequest.getId());
        return topUpSaleResult;
    }



    @Override
    public BankPaymentResponse compositeSaleAfterOnlinePayment(HttpServletRequest request) throws ServletRequestBindingException {
          HttpServletRequest finalRequest=request;
          BankPaymentResponse bankPaymentResponse=null;
          try {
              bankPaymentResponse=retryableService.runInRetryable(() -> {
                  try {
                      return this.saleAfterOnlinePayment(request);
                  } catch (ServletRequestBindingException e) {
                      e.printStackTrace();
                  }
                  return null;
              });
          }catch (TopUpException e){
              String orderReferenceNumber = ServletRequestUtils.getStringParameter(request, "myRefId");
              if (orderReferenceNumber == null)
                  orderReferenceNumber = ServletRequestUtils.getStringParameter(request, "myrefid");
              if (orderReferenceNumber == null)
                  throw new ResourceNotFoundException("Empty myRefId", "global.referenceNumber_notReceived");
              TopUpRequest topUpRequest=topUpUtil.getTopUpRequestInfoByReferenceNumber(orderReferenceNumber);
              if(topUpRequest==null || (topUpRequest!=null && (topUpRequest.getStatus()==2 || topUpRequest.getStatus()==0))) {
                  bankPaymentResponse=retryableService.runInRetryable(() -> {
                      try {
                          return  operationService.commitAfterOnlinePayment(request,true);
                      } catch (ServletRequestBindingException ex) {
                          e.printStackTrace();
                      }
                      return null;
                  });
                  //throw e;
              }
          }
          return bankPaymentResponse;
    }

    @Override
    @Transactional
    @Scheduled(fixedRateString = "#{@getCheckingUnknownTopUpRequestSchRateMinute}")
    public void checkingUnknownTopUpRequest() {
        if (!MainInstanceContition.mainInstance)
            return;
        List<TopUpRequest> unknownTopUpRequests=topUpUtil.getUnknownStatusTopUpRequest();
        for (TopUpRequest un:unknownTopUpRequests) {
            TopUpStatusResponse topUpStatusResponse =null;
            String errorMessage=null;
            try {
                topUpStatusResponse = this.saleStatus(EOperator.valueOf(un.getOperator()), un.getReserveId(), un.getTrackingId());
            }catch (Exception e){
                errorMessage=e.getMessage();
                topUpStatusResponse.setResult("-5");
            }

            if (!topUpStatusResponse.getResult().equals("0") && !topUpStatusResponse.getResult().equals("-5")) {
                this.returnUnSuccessToyUpMoney(un);// return Money
                topUpUtil.failResultInStatusTopUpRequestResult(un,ETopUpRequestStatus.UN_SUCCESS_RETURN_AMOUNT.getId(), Utils.isStringSafeEmpty(errorMessage)? Utils.getAsJson(topUpStatusResponse) : errorMessage);
            }else if (topUpStatusResponse.getResult().equals("0")) {
                un = topUpUtil.successStatusTopUpRequestResult(un, topUpStatusResponse);
            }else  if(topUpStatusResponse.getResult().equals("-5") &&  (new Date()).getTime()> Utils.addMinuteToDate(un.getCreateDate(),commonService.getConfigValue("topup_mark_to_manual_control_after_minute")).getTime()){
                topUpUtil.markToManualControl(un, Utils.isStringSafeEmpty(errorMessage)? Utils.getAsJson(topUpStatusResponse) : errorMessage);
            }else  if(topUpStatusResponse.getResult().equals("-5")){
                topUpUtil.failResultInStatusTopUpRequestResult(un,ETopUpRequestStatus.PENDING.getId(), Utils.isStringSafeEmpty(errorMessage)? Utils.getAsJson(topUpStatusResponse) : errorMessage);
            }
        }
    }

    private void returnUnSuccessToyUpMoney(TopUpRequest topUpRequest){
        if(topUpRequest.getOperationRequestId()!=null && topUpRequest.getStatus().equals(ETopUpRequestStatus.PENDING.getId()) &&
             !topUpRequest.getStatus().equals(ETopUpRequestStatus.UN_SUCCESS_RETURN_AMOUNT.getId())) {
            TransactionType transactionType= accountingService.getTransactionTypeInfo(ETransactionType.REJECT_EDIT_FACTOR.getId().longValue());
            EOperator eOperator= EOperator.valueOf(topUpRequest.getOperator());
            OperationRequest opr=operationService.getOperationRequestInfo(topUpRequest.getOperationRequestId(),false);
            accountingService.withdrawAccount(commonService.getConfigValue(eOperator.getTargetAccountConfigName()), topUpRequest.getAmount(),
                    opr.getOperationType().getCode(), transactionType, opr.getReferenceNumber(), Utils.getMessageResource("global.returnMoney",opr.getDescription()) ,
                    null, null, opr.getId(), true, false,null);
            accountingService.depositAccount(accountingService.getMainPersonalAccountIdByUserId(topUpRequest.getUserId()), topUpRequest.getAmount(), opr.getOperationType().getCode(),
                    transactionType, opr.getReferenceNumber(), Utils.getMessageResource("global.returnMoney",opr.getDescription()), null, null, opr.getId());
        }
    };

    @Transactional
    public BankPaymentResponse saleAfterOnlinePayment(HttpServletRequest request) throws ServletRequestBindingException {
        BankPaymentResponse bankPaymentResponse = operationService.commitAfterOnlinePayment(request);
        if(bankPaymentResponse!=null && bankPaymentResponse.getStatus()!=null && bankPaymentResponse.getStatus() &&
                bankPaymentResponse.getOperationRequestId()!=null && bankPaymentResponse.getOperationRequestId()>0) {
            TopUpRequest topUpRequest=null;
            try {
                topUpRequest=topUpUtil.getTopUpRequestInfoByOperationRequestId(bankPaymentResponse.getOperationRequestId());
                topUpRequest=this.compositeSale(topUpRequest);
            }catch (Exception e){
                if(topUpRequest==null || (topUpRequest!=null && (topUpRequest.getStatus()==ETopUpRequestStatus.UN_SUCCESS.getId() || topUpRequest.getStatus()==ETopUpRequestStatus.UNKNOWN.getId()))) {
                    throw new TopUpException("Invalid Data", "common.topUp.reserve_error", e.getMessage());
                }
            }
        }
        return bankPaymentResponse;
    }




    private TopUpRequest reserveCharge(TopUpRequest topUpRequest) {
        ETopUpType eTopUpType=ETopUpType.valueOf(topUpRequest.getTypeId());
        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(eTopUpType==ETopUpType.CHARGE ? EExternalApi.TOPUP_CHARGE_RESERVE.name() : EExternalApi.TOPUP_PACKAGE_RESERVE.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(),  HttpMethod.valueOf(externalApi.getHttpMethod()),null,topUpRequest.getTrackingId());

        LinkedMap<String, Object> reserveRequest= new LinkedMap<String, Object>();
        if(eTopUpType==ETopUpType.CHARGE) {
            //uncomment after mci new service
            //if (EOperator.valueOf(topUpRequest.getOperator()) != EOperator.MCI)
            reserveRequest.put("chargeType", topUpRequest.getChargeType());

            //uncomment after mci new service
            /*if (EOperator.valueOf(topUpRequest.getOperator()) == EOperator.MCI) {
                reserveRequest.put("productId", topUpRequest.getProductId());
                reserveRequest.put("productTypeId", topUpRequest.getProductTypeId());
            }*/
        }else{
            reserveRequest.put("packType", topUpRequest.getPackageType());
        }
        reserveRequest.put("amount", topUpRequest.getAmount().longValue());
        reserveRequest.put("phoneNumber",topUpRequest.getPhoneNumber().trim().substring(topUpRequest.getPhoneNumber().trim().length()-10));
        reserveRequest.put("operator",topUpRequest.getOperator());
        reserveRequest.put("saleDescription",topUpRequest.getSaleDescription());
        reserveRequest.put("extSaleID",topUpRequest.getTrackingId());
        reserveRequest.put("cardType",topUpRequest.getCardType());
        reserveRequest.put("cardNo",topUpRequest.getCardNo());
        reserveRequest.put("channelID",topUpRequest.getChannelId());

        HttpEntity<LinkedMap<String, Object>>  entity = new HttpEntity<>(reserveRequest,this.generateHeaderForAuthorization(false,topUpRequest.getTrackingId()));
        ExternalApiResponse<TopUpReserveResponse> externalApiResponse;
        try {
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,0, null , entity,new ParameterizedTypeReference<TopUpReserveResponse>(){}, false);
        }catch (HttpClientErrorException.Unauthorized e){
            entity = new HttpEntity<>(this.generateHeaderForAuthorization(true,topUpRequest.getTrackingId()));
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,0, null , entity,new ParameterizedTypeReference<TopUpReserveResponse>(){}, false);
        }catch (Exception ex) {
            topUpUtil.failResultInReserveTopUpRequestResult(topUpRequest, ex.getMessage());
            throw ex;
        }

        try {
            TopUpReserveResponse topUpReserveResponse =externalApiResponse.getResponseEntity() instanceof TopUpReserveResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),TopUpReserveResponse.class);
            if (!topUpReserveResponse.getResultCode().equals("0")) {
                topUpUtil.failResultInReserveTopUpRequestResult(topUpRequest,Utils.getAsJson(topUpReserveResponse));
                throw new InvalidDataException("Invalid Data", "common.topUp.reserve_error", topUpReserveResponse.getErrorMessage());
            }
            externalApiCall = externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(), 1, null);
            topUpRequest=topUpUtil.successReserveResultInTopUpRequest(topUpRequest,topUpReserveResponse);
            return topUpRequest;

        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage() + "\n" + Utils.getAsJson(externalApiResponse.getResponseEntity()),null);
            throw e;
        }
    }

    private TopUpRequest compositeSale(TopUpRequest topUpRequest) {
        if(topUpRequest.getStatus()==1)
            return topUpRequest;

        TopUpSaleResponse topUpSaleResponse =null;
        Boolean unknownError=false;
        try {
            topUpSaleResponse =this.sale( topUpRequest); //try to Sale
        }catch (Exception e){
            topUpSaleResponse=new TopUpSaleResponse();
            topUpSaleResponse.setResultCode("-5");
            unknownError=true;
        }

        topUpSaleResponse=this.processSaleResponse(topUpRequest, topUpSaleResponse,unknownError); //process Sale Response And Check Sale Status

        /*if (!topUpSaleResponse.getResultCode().equals("0") && !topUpSaleResponse.getResultCode().equals("-5")) {  //try to ReSale if rise error
            unknownError = false;
            try {
                topUpSaleResponse = this.reSale(topUpRequest);
            } catch (Exception e) {
                topUpSaleResponse=new TopUpSaleResponse();
                unknownError = true;
            }
            topUpSaleResponse=this.processSaleResponse(topUpRequest, topUpSaleResponse,unknownError); //process ReSale Response And Check Sale Status
        }*/

        if (!topUpSaleResponse.getResultCode().equals("0")) {
            topUpUtil.failResultInSaleTopUpRequestResult(topUpRequest,topUpSaleResponse , topUpSaleResponse.getResultCode().equals("-5"));
            if(!topUpSaleResponse.getResultCode().equals("-5"))
               throw new InvalidDataException("Invalid Data", "common.topUp.sale_error", topUpSaleResponse.getErrorMessage());
        }

        if (topUpSaleResponse.getResultCode().equals("0"))
            topUpRequest=topUpUtil.successSaleResultInTopUpRequest(topUpRequest.getId(),topUpSaleResponse);
        return topUpRequest;
    }
    private TopUpSaleResponse processSaleResponse(TopUpRequest topUpRequest,TopUpSaleResponse topUpSaleResponse ,Boolean unknownError){
        if((topUpSaleResponse.getResultCode()!=null && (topUpSaleResponse.getResultCode().equals("-5") || topUpSaleResponse.getResultCode().equals("-93"))) || unknownError) {
            int i=3;
            while (i>0){
                try {
                    TopUpStatusResponse topUpStatusResponse = this.saleStatus(EOperator.valueOf(topUpRequest.getOperator()), topUpRequest.getReserveId(), topUpRequest.getTrackingId());
                    topUpSaleResponse.setResultCode(topUpStatusResponse.getResult());
                    topUpSaleResponse.setErrorMessage(topUpStatusResponse.getErrorMessage());
                    topUpSaleResponse.setTransactionId(topUpStatusResponse.getTransactionId());
                    if (topUpSaleResponse.getResultCode().equals("0"))
                        return topUpSaleResponse;
                }catch (Exception e){
                    topUpSaleResponse.setResultCode("-5");
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
        }
        return  topUpSaleResponse;
    }


    private TopUpSaleResponse sale(TopUpRequest topUpRequest) {
        if(topUpRequest.getReserveId()==null || topUpRequest.getReserveId()==0)
            throw new InvalidDataException("Invalid Data", "common.topUp.charge_notReserved");

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.TOPUP_SALE.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(),  HttpMethod.valueOf(externalApi.getHttpMethod()),null,topUpRequest.getTrackingId());

        String paramRequest="SaleID="+topUpRequest.getReserveId();

        HttpEntity<LinkedMap<String, String>>  entity = new HttpEntity<>(this.generateHeaderForAuthorization(false,topUpRequest.getTrackingId()));
        ExternalApiResponse<TopUpSaleResponse> externalApiResponse;
        try {
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,null, paramRequest, entity,new ParameterizedTypeReference<TopUpSaleResponse>(){}, false);
        }catch (HttpClientErrorException.Unauthorized e){
            entity = new HttpEntity<>(this.generateHeaderForAuthorization(true,externalApiCall.getTrackingId()));
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,null, paramRequest, entity,new ParameterizedTypeReference<TopUpSaleResponse>(){}, false);
        }

        try {
            TopUpSaleResponse topUpSaleResponse =externalApiResponse.getResponseEntity() instanceof TopUpSaleResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),TopUpSaleResponse.class);
            externalApiCall = externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(), 1, null);
            return topUpSaleResponse;
        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage()+ "\n" + Utils.getAsJson(externalApiResponse.getResponseEntity()),null);
            throw e;
        }
    }

    private TopUpSaleResponse reSale(TopUpRequest topUpRequest) {
        if(topUpRequest.getReserveId()==null || topUpRequest.getReserveId()==0)
            throw new InvalidDataException("Invalid Data", "common.topUp.charge_notReserved");

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.TOPUP_RESALE.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(),  HttpMethod.valueOf(externalApi.getHttpMethod()),null,topUpRequest.getTrackingId());

        String paramRequest="PreSaleID="+topUpRequest.getReserveId();

        HttpEntity<LinkedMap<String, String>>  entity = new HttpEntity<>(this.generateHeaderForAuthorization(false,topUpRequest.getTrackingId()));
        ExternalApiResponse<TopUpSaleResponse> externalApiResponse;
        try {
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,null, paramRequest, entity,new ParameterizedTypeReference<TopUpSaleResponse>(){}, false);
        }catch (HttpClientErrorException.Unauthorized e){
            entity = new HttpEntity<>(this.generateHeaderForAuthorization(true,externalApiCall.getTrackingId()));
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,null, paramRequest, entity,new ParameterizedTypeReference<TopUpSaleResponse>(){}, false);
        }

        try {
            TopUpSaleResponse topUpSaleResponse =externalApiResponse.getResponseEntity() instanceof TopUpSaleResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),TopUpSaleResponse.class);
            externalApiCall = externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(), 1, null);
            return topUpSaleResponse;

        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage()+ "\n" + Utils.getAsJson(externalApiResponse.getResponseEntity()),null);
            throw e;
        }
    }

    private TopUpStatusResponse saleStatus(EOperator eOperator,Long saleId,String trackingId) {
        if(saleId==null || saleId==0)
            throw new InvalidDataException("Invalid Data", "common.topUp.charge_notReserved");

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.TOPUP_SALE_STATUS.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(),  HttpMethod.valueOf(externalApi.getHttpMethod()),null,trackingId);

        String paramRequest="SaleID="+saleId + "&Operator="+eOperator.name();

        HttpEntity<LinkedMap<String, String>>  entity = new HttpEntity<>(this.generateHeaderForAuthorization(false,trackingId));
        ExternalApiResponse<TopUpStatusResponse> externalApiResponse;
        try {
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,null, paramRequest, entity,new ParameterizedTypeReference<TopUpStatusResponse>(){}, false);
        }catch (HttpClientErrorException.Unauthorized e){
            entity = new HttpEntity<>(this.generateHeaderForAuthorization(true,externalApiCall.getTrackingId()));
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,null, paramRequest, entity,new ParameterizedTypeReference<TopUpStatusResponse>(){}, false);
        }
        try {
            TopUpStatusResponse topUpStatusResponse =externalApiResponse.getResponseEntity() instanceof TopUpStatusResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),TopUpStatusResponse.class);
            externalApiCall = externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(), 1, null);
            return topUpStatusResponse;
        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage()+ "\n" + Utils.getAsJson(externalApiResponse.getResponseEntity()),null);
            throw e;
        }
    }

//endregion External Service



//region Internal Business

    @Override
    public ResultListPageable<TopUpRequestWrapper> getTopUpRequestWrappers(Map<String, Object> requestParams ) {

        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(TOPUP_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "topUpRequestWrapperMapping");
        ResultListPageable<TopUpRequestWrapper> topUpRequestWrapperResultListPageable=this.getTopUpRequestGeneral(requestParams);
        return topUpRequestWrapperResultListPageable;
    }

    @Override
    public TopUpRequestWrapper getTopUpRequestWrapperInfo(Long topUpRequestId) {
        if(topUpRequestId==null)
            throw new InvalidDataException("Invalid Data", "common.topUp.id_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("id",topUpRequestId);
        requestParams.put("resultSetMapping", "topUpRequestWrapperMapping");

        ResultListPageable<TopUpRequestWrapper> result=this.getTopUpRequestGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);

        throw new ResourceNotFoundException(topUpRequestId.toString(), "common.topUp.id_notFound" );
    }

    @Override
    public TopUpRequestWrapper getTopUpRequestWrapperInfoByOperationRequestWrapper(Long operationRequestId) {
        if(operationRequestId==null)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.id_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("operationRequestId",operationRequestId);
        requestParams.put("resultSetMapping", "topUpRequestWrapperMapping");

        ResultListPageable<TopUpRequestWrapper> result=this.getTopUpRequestGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);

        throw new ResourceNotFoundException(operationRequestId.toString(), "common.topUp.id_notFound" );
    }

    @Override
    public OperationRequestWrapper getTopUpOperationRequestWrapperInfo(Long operationRequestId) {
        TopUpRequestWrapper topUpRequestWrapper=this.getTopUpRequestWrapperInfoByOperationRequestWrapper(operationRequestId);
        OperationRequestWrapper operationRequestWrapper;
        if(topUpRequestWrapper.getOperationRequestId()!=null)
            operationRequestWrapper=operationService.getOperationRequestWrapperInfo(topUpRequestWrapper.getOperationRequestId());
        else
            operationRequestWrapper=operationService.getOperationRequestWrapperInfo(topUpRequestWrapper.getTrackingId());

        List<GeneralKeyValue> result=this.generateTopUpDetailsRow(topUpRequestWrapper);
        if(result!=null && result.size()>0)
            operationRequestWrapper.getAdditionalDetails().add(result);
        return operationRequestWrapper;

    }

    private  List<GeneralKeyValue> generateTopUpDetailsRow(TopUpRequestWrapper topUpRequestWrapper){
        List<GeneralKeyValue> result=new ArrayList<>();
        if(topUpRequestWrapper!=null) {
            result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("global.operator")).value(EOperator.valueOf(topUpRequestWrapper.getOperator()).getCaption()).build());
            result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.topUp.phoneNumber_caption")).value(topUpRequestWrapper.getPhoneNumber()).build());
            result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("global.status")).value(topUpRequestWrapper.getStatusDesc()).build());
            result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("global.description")).value(topUpRequestWrapper.getDescription()).build());
            result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("global.trackingCode")).value(topUpRequestWrapper.getTrackingCode()).build());

        }
        return result;
    }

    private String getBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return "select t.tpr_id as id, t.tpr_type_id as typeId,t.tpr_usr_id as userId, u.usr_username as userName, t.tpr_opr_id operationRequestId,t.tpr_charge_type as chargeType ,\n" +
                " t.tpr_package_type as packageType, t.tpr_product_type_id as productTypeId , t.tpr_product_id as productId,t.tpr_description as description, t.tpr_amount as amount,\n" +
                " t.tpr_phone_number as phoneNumber,  t.tpr_operator as operator, t.tpr_sale_description as saleDescription, t.tpr_card_type as cardType ,\n" +
                " t.tpr_card_no as cardNo, t.tpr_channel_id as channelId, t.tpr_request_ip as requestIp, t.tpr_status as status, t.tpr_reserve_id as reserveId ,\n" +
                " t.tpr_sale_id as saleId, t.tpr_operator_transaction_id as operatorTransactionId, " + (loadDetails ? "t.tpr_response" : "''") +" as response, t.tpr_tracking_id as trackingId,\n" +
                " t.tpr_create_by as createBy, t.tpr_create_date as createDate, t.tpr_modify_by as modifyBy, t.tpr_modify_date as modifyDate \n" ;
    }
    private String getBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }


    private String getBaseQueryBody(Map<String, Object> requestParams) {
        String queryString=" from sc_topup_request t \n" +
                           " inner join sc_user u on (t.tpr_usr_id=u.usr_id)\n";
        return  queryString;
    }

    private ResultListPageable<TopUpRequestWrapper> getTopUpRequestGeneral(Map<String, Object> requestParams) {

        String trackingCode = Utils.getAsStringFromMap(requestParams, "trackingCode", false,null);
        if(!Utils.isStringSafeEmpty(trackingCode)) {
            requestParams.put("id", Utils.getIdentityFromTrackingCode(trackingCode));
        }
        String queryString = this.getBaseQueryHead(requestParams)+ this.getBaseQueryBody(requestParams);
                //Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getBaseCountQueryHead(requestParams)+this.getBaseQueryBody(requestParams);
                             //Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));
        return dynamicQueryHelper.getDataGeneral(requestParams,TOPUP_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,TopUpRequestWrapper.class);
    }

//endregion Internal Business



//region Base Data

    @Override
    public List<TypeWrapper> getOperators() {
        return EOperator.getAllAsObjectWrapper();
    }

    private static Map<Integer,String> MCI_CHARGE_TYPE;
    static {
        MCI_CHARGE_TYPE = new HashMap<>();
        MCI_CHARGE_TYPE.put(5,"شارژ مستقیم");
        MCI_CHARGE_TYPE.put(6,"شارژ دلخواه");
        MCI_CHARGE_TYPE.put(7,"شارژ فوق العاده");
        MCI_CHARGE_TYPE.put(8,"شارژ جوانان");
        MCI_CHARGE_TYPE.put(9,"شارژ بانوان");
        MCI_CHARGE_TYPE.put(10,"شارز وفاداری");
    }

    @Transactional
    @Override
    public List<DirectChargeData> getAllDirectChargeType() {
        List<DirectChargeData> result=(List<DirectChargeData>) cacheService.getCacheValue("topUpCache", "allDirectChargeType");
        if (result != null)
            return result;

        result=new ArrayList<>();
        String activeOperator=commonService.getConfigValue("active_operator");
        if(activeOperator.contains(EOperator.MCI.toString().toLowerCase()))
           result.add(new DirectChargeData(EOperator.MCI,((Long)commonService.getConfigValue("mci_min_amount")).doubleValue(),commonService.getConfigValue(EOperator.MCI.getTargetAccountConfigName()),this.getDirectChargeType(EOperator.MCI)));
        if(activeOperator.contains(EOperator.MTN.toString().toLowerCase()))
           result.add(new DirectChargeData(EOperator.MTN,((Long)commonService.getConfigValue("mtn_min_amount")).doubleValue(),commonService.getConfigValue(EOperator.MTN.getTargetAccountConfigName()),this.getDirectChargeType(EOperator.MTN)));
        if(activeOperator.contains(EOperator.RAY.toString().toLowerCase()))
           result.add(new DirectChargeData(EOperator.RAY,((Long)commonService.getConfigValue("ray_min_amount")).doubleValue(),commonService.getConfigValue(EOperator.RAY.getTargetAccountConfigName()),this.getDirectChargeType(EOperator.RAY)));
        cacheService.putCacheValue("topUpCache", "allDirectChargeType",result, 7, TimeUnit.DAYS);
        return result;
    }

    @Transactional
    @Override
    public List<OperatorDirectCharge> getDirectChargeType(EOperator eOperator){
        List<OperatorDirectCharge> result=(List<OperatorDirectCharge>) cacheService.getCacheValue("topUpCache", "directChargeType_"+eOperator.name());
        if (result != null)
            return result;
        result=new ArrayList<>();
        //uncomment after mci new service
        /*if(eOperator==EOperator.MCI) {
            Map<Integer,List<MciProductDetailResponse>> mciList=this.getMciProductList(1).stream().collect(Collectors.groupingBy(MciProductDetailResponse::getChargeType));
            List<OperatorDirectCharge>  finalResult=new ArrayList<>();
            mciList.forEach((k, v)->{
                finalResult.add(new OperatorDirectCharge(DirectChargeType.builder().code(k.toString()).description(MCI_CHARGE_TYPE.get(k)).build(),v));
            });
            result=finalResult;*/
        if(eOperator==EOperator.MCI) {
            String mciActiveTopUpPackage=commonService.getConfigValue("mci_active_topup_package");
            if(mciActiveTopUpPackage.contains("1002"))
               result.add(new OperatorDirectCharge(DirectChargeType.builder().code("1002").description("دلخواه").build(),this.getDefaultChargeProduct(true, EOperator.MCI)));
            if(mciActiveTopUpPackage.contains("1003"))
              result.add(new OperatorDirectCharge(DirectChargeType.builder().code("1003").description("بانوان").build(),this.getDefaultChargeProduct(true, EOperator.MCI)));
            if(mciActiveTopUpPackage.contains("1005"))
              result.add(new OperatorDirectCharge(DirectChargeType.builder().code("1005").description("جوانان").build(),this.getDefaultChargeProduct(true, EOperator.MCI)));
        }else if(eOperator==EOperator.MTN) {
            String mtnActiveTopUpPackage=commonService.getConfigValue("mtn_active_topup_package");
            if(mtnActiveTopUpPackage.contains("normal"))
               result.add(new OperatorDirectCharge(DirectChargeType.builder().code("normal").description("معمولی").build(),this.getDefaultChargeProduct(true, EOperator.MTN)));
            if(mtnActiveTopUpPackage.contains("amaizing"))
               result.add(new OperatorDirectCharge(DirectChargeType.builder().code("amaizing").description("شگفت انگیز").build(),this.getDefaultChargeProduct(true, EOperator.MTN)));
            if(mtnActiveTopUpPackage.contains("postpay"))
               result.add(new OperatorDirectCharge(DirectChargeType.builder().code("postpay").description("پرداخت قبض سیم کارت دائمی").build(),this.getDefaultChargeProduct(true, EOperator.MTN)));
        }else if(eOperator==EOperator.RAY){
            String rayActiveTopUpPackage=commonService.getConfigValue("ray_active_topup_package");
            if(rayActiveTopUpPackage.contains("normal"))
               result.add(new OperatorDirectCharge(DirectChargeType.builder().code("normal").description("معمولی").build(),this.getDefaultChargeProduct(true, EOperator.RAY)));
            if(rayActiveTopUpPackage.contains("amaizing"))
               result.add(new OperatorDirectCharge(DirectChargeType.builder().code("amaizing").description("شگفت انگیز").build(),this.getDefaultChargeProduct(true, EOperator.RAY)));
        }
        cacheService.putCacheValue("topUpCache", "directChargeType_"+eOperator.name(),result, 7, TimeUnit.DAYS);
        return result;
    }
    private List<MciProductDetailResponse> getDefaultChargeProduct(Boolean addOptional,EOperator eOperator){
        List<MciProductDetailResponse> result=new ArrayList<>();
        String offerAmount="";
        if(eOperator==EOperator.MCI)
            offerAmount=commonService.getConfigValue("mci_active_offer_amount");
        else if(eOperator==EOperator.MTN)
            offerAmount=commonService.getConfigValue("mtn_active_offer_amount");
        else if(eOperator==EOperator.RAY)
            offerAmount=commonService.getConfigValue("ray_active_offer_amount");

        if(offerAmount.contains("1"))
           result.add(MciProductDetailResponse.builder().id(1).title("مبلغ دلخواه").amount(0d).vat(0.09d).build());
        if(offerAmount.contains("2"))
           result.add(MciProductDetailResponse.builder().id(2).title("شارژ 10 هزار ریالی").amount(10000d).vat(0.09d).build());
        if(offerAmount.contains("3"))
           result.add(MciProductDetailResponse.builder().id(3).title("شارژ 20 هزار ریالی").amount(20000d).vat(0.09d).build());
        if(offerAmount.contains("4"))
           result.add(MciProductDetailResponse.builder().id(4).title("شارژ 50 هزار ریالی").amount(50000d).vat(0.09d).build());
        if(offerAmount.contains("5"))
           result.add(MciProductDetailResponse.builder().id(5).title("شارژ 100 هزار ریالی").amount(100000d).vat(0.09d).build());
        if(offerAmount.contains("6"))
           result.add(MciProductDetailResponse.builder().id(6).title("شارژ 200 هزر ریالی").amount(200000d).vat(0.09d).build());

/*      if(addOptional)
            result.add(MciProductDetailResponse.builder().id(1).title("مبلغ دلخواه").amount(0d).vat(0.09f).build());
        if(eOperator==EOperator.MTN) {
            result.add(MciProductDetailResponse.builder().id(2).title("شارژ 10 هزار ریالی").amount(10000d).vat(0.09f).build());
            result.add(MciProductDetailResponse.builder().id(3).title("شارژ 20 هزار ریالی").amount(20000d).vat(0.09f).build());
        }
        result.add(MciProductDetailResponse.builder().id(4).title("شارژ 50 هزار ریالی").amount(50000d).vat(0.09f).build());
        result.add(MciProductDetailResponse.builder().id(5).title("شارژ 100 هزار ریالی").amount(100000d).vat(0.09f).build());
        result.add(MciProductDetailResponse.builder().id(6).title("شارژ 200 هزر ریالی").amount(200000d).vat(0.09f).build());*/
        return result;
    }



    @Transactional
    @Override
    public List<InternetPackageData> getAllInternetPackageData() {
        List<InternetPackageData> result=(List<InternetPackageData>) cacheService.getCacheValue("topUpCache", "allInternetPackageData");
        if (result != null)
            return result;
        result=new ArrayList<>();
        result.add(new InternetPackageData(EOperator.MCI,0d,commonService.getConfigValue(EOperator.MCI.getTargetAccountConfigName()),this.getInternetPackageData(EOperator.MCI)));
        result.add(new InternetPackageData(EOperator.MTN,0d,commonService.getConfigValue(EOperator.MTN.getTargetAccountConfigName()),this.getInternetPackageData(EOperator.MTN)));
        result.add(new InternetPackageData(EOperator.RAY,0d,commonService.getConfigValue(EOperator.RAY.getTargetAccountConfigName()),this.getInternetPackageData(EOperator.RAY)));
        cacheService.putCacheValue("topUpCache", "allInternetPackageData",result, 7, TimeUnit.DAYS);
        return result;
    }

    @Transactional
    @Override
    public List<SimType> getInternetPackageData(EOperator eOperator) {
        List<SimType> result=(List<SimType>) cacheService.getCacheValue("topUpCache", "internetPackageData_"+eOperator.name());
        if (result != null)
            return result;

        List<InternetPackageDetailResponse> internetPackageDetailResponses= this.getInternetPackageList(eOperator);
        result=new ArrayList<>();

        Map<Integer,List<InternetPackageDetailResponse>> mapBySimType=this.getInternetPackageList(eOperator).stream().collect(Collectors.groupingBy(InternetPackageDetailResponse::getSystems));
        List<SimType>  finalResult=new ArrayList<>();
        mapBySimType.forEach((k, v)->{
            finalResult.add(new SimType(k,v.get(0).getSystemName(), this.getPackagePeriods(v)));
        });
        result=finalResult.stream().sorted(Comparator.comparing(SimType::getId)).collect(Collectors.toList());;

        cacheService.putCacheValue("topUpCache", "internetPackageData_"+eOperator.name(),result, 7, TimeUnit.DAYS);
        return result;

    }

    @Override
    public List<TypeWrapper> getTopUpRequestStatuses() {
        return ETopUpRequestStatus.getAllAsObjectWrapper();
    }

    private List<PackagePeriod> getPackagePeriods(List<InternetPackageDetailResponse> internetPackageDetailResponses){
        List<PackagePeriod> result=new ArrayList<>();
        Map<Integer,List<InternetPackageDetailResponse>> mapByPackagePeriod=internetPackageDetailResponses.stream().collect(Collectors.groupingBy(InternetPackageDetailResponse::getPeriod));
        List<PackagePeriod>  finalResult=new ArrayList<>();
        mapByPackagePeriod.forEach((k, v)->{
            finalResult.add(new PackagePeriod(k,v.stream().sorted(Comparator.comparing(InternetPackageDetailResponse::getPackage_cost)).collect(Collectors.toList())));
        });
        result=finalResult;
        return result.stream().sorted(Comparator.comparing(PackagePeriod::getDay)).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public List<MciProductDetailResponse> getMciProductList(Integer productTypeId) {
        if(productTypeId==null)
           throw new InvalidDataException("Invalid Data", "common.topUp.mciProductTypeId_error");

        List<MciProductDetailResponse> result=null;
        if ( commonService.getMciProductList_CacheTime()>0)
            result=(List<MciProductDetailResponse>) cacheService.getCacheValue("topUpCache", "mciProductList_"+productTypeId);
        if (result != null)
            return result;

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.MCI_PRODUCT_LIST.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null);

        String paramRequest="productTypeId="+productTypeId.toString();

        HttpEntity<LinkedMap<String, String>>  entity = new HttpEntity<>(this.generateHeaderForAuthorization(false,externalApiCall.getTrackingId()));
        ExternalApiResponse<MciProductResponse> externalApiResponse;
        try {
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,productTypeId.hashCode(), paramRequest, entity,new ParameterizedTypeReference<MciProductResponse>(){}, false);
        }catch (HttpClientErrorException.Unauthorized e){
            entity = new HttpEntity<>(this.generateHeaderForAuthorization(true,externalApiCall.getTrackingId()));
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,productTypeId.hashCode(), paramRequest, entity,new ParameterizedTypeReference<MciProductResponse>(){}, false);
        }

        try {
            MciProductResponse mciProductResponse =externalApiResponse.getResponseEntity() instanceof MciProductResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),MciProductResponse.class);
            if (!mciProductResponse.getResult().equals("0"))
                throw new InvalidDataException("Invalid Data", "common.topUp.mciProductList_error", mciProductResponse.getErrorMsg());

            externalApiCall = externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(), 1, null);
            if (commonService.getMciProductList_CacheTime() > 0)
                cacheService.putCacheValue("topUpCache", "mciProductList_"+ productTypeId, mciProductResponse.getData(), commonService.getMciProductList_CacheTime(), TimeUnit.MINUTES);
            return mciProductResponse.getData();
        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage()+ "\n" + Utils.getAsJson(externalApiResponse.getResponseEntity()),null);
            throw e;
        }

    }


    @Transactional
    @Override
    public List<InternetPackageDetailResponse> getInternetPackageList(EOperator eOperator) {

        List<InternetPackageDetailResponse> result=null;
        if ( commonService.getInternetPackageList_CacheTime()>0)
            result=(List<InternetPackageDetailResponse>) cacheService.getCacheValue("topUpCache", "internetPackageList_"+ eOperator.name());
        if (result != null)
            return result;

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.INTERNET_PACKAGE_LIST.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null);

        String paramRequest="Operator="+eOperator.name();

        HttpEntity<LinkedMap<String, String>>  entity = new HttpEntity<>(this.generateHeaderForAuthorization(false,externalApiCall.getTrackingId()));
        ExternalApiResponse<InternetPackageResponse> externalApiResponse;
        try {
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,eOperator.name().hashCode(), paramRequest, entity,new ParameterizedTypeReference<InternetPackageResponse>(){}, false);
        }catch (HttpClientErrorException.Unauthorized e){
            entity = new HttpEntity<>(this.generateHeaderForAuthorization(true,externalApiCall.getTrackingId()));
            externalApiResponse = externalApiHelper.callRestApiExchange(externalApiCall,eOperator.name().hashCode(), paramRequest, entity,new ParameterizedTypeReference<InternetPackageResponse>(){}, false);
        }

        try {
            InternetPackageResponse internetPackageResponse =externalApiResponse.getResponseEntity() instanceof InternetPackageResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),InternetPackageResponse.class);
            if (!internetPackageResponse.getResult().equals("0"))
                throw new InvalidDataException("Invalid Data", "common.topUp.internetPackageList_error", internetPackageResponse.getErrorMsg());

            externalApiCall = externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(), 1, null);
            if (commonService.getMciProductList_CacheTime() > 0)
                cacheService.putCacheValue("topUpCache", "internetPackageList_"+ eOperator.name(), internetPackageResponse.getPackageList(), commonService.getInternetPackageList_CacheTime(), TimeUnit.MINUTES);
            return internetPackageResponse.getPackageList();
        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall.getId(),2,e.getMessage()+ "\n" + Utils.getAsJson(externalApiResponse.getResponseEntity()),null);
            throw e;
        }
    }

    //endregion Base Data
}
