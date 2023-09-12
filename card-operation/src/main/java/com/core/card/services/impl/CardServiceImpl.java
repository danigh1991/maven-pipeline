package com.core.card.services.impl;

import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.wrapper.OperationRequestWrapper;
import com.core.card.model.cardmodel.*;
import com.core.card.model.cardmodel.enums.EPinCvvSecurityType;
import com.core.card.model.cardmodel.enums.ESecurityType;
import com.core.card.model.cardmodel.enums.ETerminalType;
import com.core.card.model.cardmodel.enums.ETransactionType;
import com.core.card.model.contextmodel.BankCardHolderInquiryDto;
import com.core.card.model.contextmodel.BankCardTransferDto;
import com.core.card.model.contextmodel.OtpDeliveryDto;
import com.core.card.model.dbmodel.BankCardOperation;
import com.core.card.model.enums.ECardOperationStatus;
import com.core.card.model.wrapper.BankCardOperationWrapper;
import com.core.card.repository.BankCardRepository;
import com.core.card.services.CardService;
import com.core.common.externalapi.ExternalApiHelper;
import com.core.common.externalapi.ExternalApiResponse;
import com.core.common.externalapi.ExternalApiUtil;
import com.core.common.model.enums.EExternalApi;
import com.core.common.services.CommonService;
import com.core.common.services.impl.AbstractService;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.services.CacheService;
import com.core.model.wrapper.TypeWrapper;
import com.core.services.CalendarService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.card.model.contextmodel.BankCardDto;
import com.core.card.model.dbmodel.BankCard;
import com.core.card.model.wrapper.BankCardWrapper;
import com.core.card.util.CardUtils;
import com.core.model.repository.IDynamicQueryRepository;
import com.core.model.wrapper.ResultListPageable;
import com.core.util.search.SearchCriteria;
import com.core.util.search.SearchCriteriaParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("cardServiceImpl")
public class CardServiceImpl extends AbstractService implements CardService {

    private BankCardRepository bankCardRepository;
    private ExternalApiHelper externalApiHelper;
    private ExternalApiUtil externalApiUtil;
    private CommonService commonService;
    private CardOperationUtil cardOperationUtil;
    private CacheService cacheService;
    private CalendarService calendarService;
    private DynamicQueryHelper dynamicQueryHelper;


    @Value("#{${bankCardOperation.search.native.private.params}}")
    private HashMap<String, List<String>> BANK_CARD_OPERATION_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;
    //private static Integer REFERENCE_NUMBER_PREFIX=1000;

  /*  private static Map<Integer, EOperation> LOCAL_OPERATIONS;
    static {
        LOCAL_OPERATIONS = new HashMap<>();
        LOCAL_OPERATIONS.put(2010,EOperation.valueOfCode(2010));
    }*/


    //#region  Create Cart
    @Autowired
    public CardServiceImpl(ExternalApiHelper externalApiHelper, ExternalApiUtil externalApiUtil, BankCardRepository bankCardRepository, CommonService commonService,
                           CardOperationUtil cardOperationUtil, CacheService cacheService, CalendarService calendarService,DynamicQueryHelper dynamicQueryHelper) {
        this.externalApiHelper = externalApiHelper;
        this.externalApiUtil = externalApiUtil;
        this.bankCardRepository = bankCardRepository;
        this.commonService = commonService;
        this.cardOperationUtil = cardOperationUtil;
        this.cacheService = cacheService;
        this.calendarService = calendarService;
        this.dynamicQueryHelper= dynamicQueryHelper;
    }

    @Override
    public BankCard getBankCardInfo(Long bankCardId) {
        if (bankCardId == null)
            throw new InvalidDataException("Invalid Data", "common.bankCard.id_required");

        Optional<BankCard> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = bankCardRepository.findByEntityId(bankCardId);
        else
            result = bankCardRepository.findByEntityId(bankCardId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.bankCard.id_notFound");
        return result.get();
    }

    @Override
    public BankCard getBankShpCardInfo(Long shpCardId) {
        if (shpCardId==null)
            throw new InvalidDataException("Invalid Data", "common.bankCard.cardId_required");

        Optional<BankCard> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = bankCardRepository.findByShpCardId(shpCardId);
        else
            result = bankCardRepository.findByShpCardId(shpCardId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.bankCard.cardId_notFound");
        return result.get();
    }

    @Override
    public BankCard getBankCardInfo(String cardNumber) {
        if (Utils.isStringSafeEmpty(cardNumber))
            throw new InvalidDataException("Invalid Data", "common.bankCard.cardNumber_required");

        Optional<BankCard> result;
        result = bankCardRepository.findByCardNumber(cardNumber, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.bankCard.cardNumber_notFound");
        return result.get();

    }

    @Override
    public List<BankCard> getMyBankCards(Integer type) {
        return this.getBankCards(Utils.getCurrentUserId(),type) ;
    }

    @Override
    public List<BankCard> getBankCards(Long userId,Integer type) {
        if (!hasRoleType(ERoleType.ADMIN))
            userId=Utils.getCurrentUserId();

        if(type==null)
            type=1;
        if(type==0)
            return bankCardRepository.findAllByUserId(userId);
        else if(type==1)
           return bankCardRepository.findMyByUserId(userId);
        else
           return bankCardRepository.findOtherByUserId(userId);

    }

    @Override
    public BankCardWrapper getBankCardWrapperInfo(Long bankCardId) {
        if (hasRoleType(ERoleType.ADMIN))
            return bankCardRepository.findBankCardWrapperById(bankCardId);
        else
            return bankCardRepository.findBankCardWrapperByIdAndUsrId(bankCardId,Utils.getCurrentUserId());
    }

    @Override
    public List<BankCardWrapper> getMyBankCardWrappers(Integer type) {
        return this.getBankCardWrappers(Utils.getCurrentUserId(),type);
    }

    @Override
    public List<BankCardWrapper> getBankCardWrappers(Long userId,Integer type) {
        if (!hasRoleType(ERoleType.ADMIN))
            userId=Utils.getCurrentUserId();

        if(type==null)
            type=1;
        if(type==0)
           return bankCardRepository.findAllBankCardWrappersByUsrId(userId);
        else if(type==1)
           return bankCardRepository.findMyBankCardWrappersByUsrId(userId);
        else
           return bankCardRepository.findOtherBankCardWrappersByUsrId(userId);
    }

    @Transactional
    @Override
    public BankCardWrapper createBankCard(BankCardDto bankCardDto) {
        if(!CardUtils.checkBankCardName(bankCardDto.getCardNumber()))
            throw new InvalidDataException("Invalid Data", "common.bankCard.cardNumber_invalid");

        BankCard bankCard=new BankCard();
        if (hasRoleType(ERoleType.ADMIN))
            bankCard.setUserId(bankCardDto.getUserId());
        else
            bankCard.setUserId(Utils.getCurrentUserId());
        if(bankCard.getUserId()==null)
            bankCard.setUserId(Utils.getCurrentUserId());

        bankCard.setCardNumber(bankCardDto.getCardNumber());
        bankCard=this.mapBankCardToDb(bankCard,bankCardDto);
        bankCard=bankCardRepository.save(bankCard);
        return this.getBankCardWrapperInfo(bankCard.getId());
    }

    @Transactional
    @Override
    public String editBankCard(BankCardDto bankCardDto) {
        BankCard bankCard=this.getBankCardInfo(bankCardDto.getId());
        bankCard=this.mapBankCardToDb(bankCard,bankCardDto);
        bankCard=bankCardRepository.save(bankCard);
        return Utils.getMessageResource("global.operation.success_info");
    }

    private BankCard mapBankCardToDb(BankCard bankCard,BankCardDto bankCardDto){
        if(bankCard.getId()==null){
            if(bankCardRepository.countByName(bankCardDto.getName(),bankCard.getUserId())>0)
               throw new InvalidDataException("Invalid Data", "common.bankCard.name_exist");;
            if(bankCardRepository.countByCardNumber(bankCardDto.getCardNumber(),bankCard.getUserId())>0)
               throw new InvalidDataException("Invalid Data", "common.bankCard.cardNumber_exist");;
        }else{
            if(bankCardRepository.countByName(bankCard.getId(),bankCardDto.getName(),bankCard.getUserId())>0)
                throw new InvalidDataException("Invalid Data", "common.bankCard.name_exist");;
        }
        /*if(!Utils.isStringSafeEmpty(bankCardDto.getExpire())) {
            if (!CardUtils.validateBankCardExpire(bankCardDto.getExpire()))
                throw new InvalidDataException("Invalid Data", "common.bankCard.expire_invalid");
            bankCard.setExpire(bankCardDto.getExpire());
        }*/
        bankCard.setName(bankCardDto.getName());
        bankCard.setActive(bankCardDto.getActive());
        bankCard.setDescription(bankCardDto.getDescription());
        return bankCard;
    }

    @Transactional
    @Override
    public String deleteBankCard(Long bankCardId) {
        BankCard bankCard=this.getBankCardInfo(bankCardId);
        if(bankCard.getBankCardOperations()!=null && bankCard.getBankCardOperations().size()>0)
            throw new InvalidDataException("Invalid Data", "common.bankCard.hasCardOperation");
        bankCardRepository.delete(bankCard);
        return Utils.getMessageResource("global.delete_info");
    }
    //#endregion


    //#region Card Operation

    @Override
    public ResultListPageable<BankCardOperationWrapper> getBankCardOperationWrappers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(BANK_CARD_OPERATION_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);
        if (!hasRoleType(ERoleType.ADMIN))
            requestParams.put("specialParams","status>1");
        requestParams.put("resultSetMapping", "bankCardOperationWrapperMapping");
        ResultListPageable<BankCardOperationWrapper> bankCardOperationWrapperResultListPageable=this.getBankCardOperationGeneral(requestParams);
        return bankCardOperationWrapperResultListPageable;
    }

    @Override
    public BankCardOperationWrapper getBankCardOperationWrapperInfo(Long bankCardOperationId) {
        if(bankCardOperationId==null)
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.id_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("id",bankCardOperationId);
        requestParams.put("resultSetMapping", "bankCardOperationWrapperMapping");
        requestParams.put("loadDetails", true);

        ResultListPageable<BankCardOperationWrapper> result=this.getBankCardOperationGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);
        throw new ResourceNotFoundException(bankCardOperationId.toString(), "common.bankCardOperation.id_notFound" );
    }

    @Override
    public List<TypeWrapper> getCardOperatorStatuses() {
        return ECardOperationStatus.getAllAsObjectWrapper();
    }

    //#endregion Card Operation


    //#region query generator
    private String getBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return "select o.bco_id as id, o.bco_opt_id as operationTypeId, o.bco_bcr_id as bankCardId, c.bcr_card_number as sourceCard,c.bcr_name as sourceCardName, \n" +
                "       o.bco_target_card as targetCard, o.bco_shp_card_holder_name as targetCardOwner, o.bco_status as  status, o.bco_last_status_date as lastStatusDate,\n" +
                "       o.bco_bill_id as billId, o.bco_bill_pay as billPay, o.bco_amount as amount, o.bco_description as description, o.bco_tracking_id as trackingId, \n" +
                "       o.bco_shp_registration_date as shpRegistrationDate, o.bco_shp_transaction_id as shpTransactionId, \n" +
                "       o.bco_shp_transaction_date as shpTransactionDate, o.bco_shp_stan as shpStan, o.bco_shp_rrn as shpRrn, \n" +
                "       o.bco_shp_approval_code as shpApprovalCode, " + (loadDetails ? "o.bco_error_description" : "''") + " as errorDescription, o.bco_usr_id as userId " ;

    }
    private String getBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getBaseQueryBody(Map<String, Object> requestParams) {
        String queryString=" from sc_bank_card_operation o \n" +
                " inner join sc_bank_card c on (c.bcr_id=o.bco_bcr_id) \n";
        return  queryString;
    }

    private ResultListPageable<BankCardOperationWrapper> getBankCardOperationGeneral(Map<String, Object> requestParams) {
        String trackingCode = Utils.getAsStringFromMap(requestParams, "trackingCode", false,null);
        if(!Utils.isStringSafeEmpty(trackingCode)) {
            requestParams.put("id", Utils.getIdentityFromTrackingCode(trackingCode));
        }

        String queryString = this.getBaseQueryHead(requestParams)+
                Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getBaseCountQueryHead(requestParams)+
                    Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        return dynamicQueryHelper.getDataGeneral(requestParams,BANK_CARD_OPERATION_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString, BankCardOperationWrapper.class);
    }

    //#endregion query generator

    //#region SHP Card Operation
    private HttpHeaders createHeader(ExternalApi externalApi){
        String authData =  Utils.deCrypt(externalApi.getKey())+":" + Utils.deCrypt(externalApi.getSecret());
        String authDataBase64=Utils.base64(authData);
        HttpHeaders headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Basic " + authDataBase64);
        return headers;
    }

    @Override
    public String getTransactionIdByTrackingId(String trackingId) {
        ExternalApiCall externalApiCall=externalApiUtil.getExternalApiCallInfo(trackingId);
        if(externalApiCall.getStatus()!=1)
            throw new InvalidDataException("Invalid Data", "common.bankCard.status_notValid");

        if(Utils.isStringSafeEmpty(externalApiCall.getTrackingId()))
            throw new InvalidDataException("Invalid Data", "common.bankCard.trackingId_notValid");

        return externalApiCall.getTrackingId();
    }

    @Transactional
    @Override
    public CardEnrollmentResponse cardEnrollment(String platform) {

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_CARD_ENROLLMENT.name());

        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null);

        CardEnrollmentRequest cardEnrollmentRequest=CardEnrollmentRequest.builder()
                .trackingNumber(externalApiCall.getTrackingId())
                //.cellphoneNumber(Utils.getCurrentUser().getMobileNumber())
                .cellphoneNumber("09124727739")
                .appUrlPattern(!Utils.isStringSafeEmpty(platform) && platform.equalsIgnoreCase("WEB") ?
                                             "https://webjat.samatco.ir/#/bankcardlist/1?tkId=" + externalApiCall.getTrackingId() + "&keyId={}"
                                             //"samatapp://open/bankcardlist/1?tkId=" + externalApiCall.getTrackingId() + "&keyId={}"
                                           : "samatapp://open/bankcardlist/1?tkId=" + externalApiCall.getTrackingId() + "&keyId={}"
                                 ).build();
        HttpEntity<CardEnrollmentRequest> entity = new HttpEntity<>(cardEnrollmentRequest, this.createHeader(externalApiCall.getExternalApi()));
        ExternalApiResponse<CardEnrollmentResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity, new ParameterizedTypeReference<CardEnrollmentResponse>(){} ,true);

        CardEnrollmentResponse cardEnrollmentResponse =externalApiResponse.getResponseEntity() instanceof CardEnrollmentResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),CardEnrollmentResponse.class);
        this.resultStatusInterpreter(externalApiCall.getId(), cardEnrollmentResponse.getStatus(),cardEnrollmentResponse.getErrors());
        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,cardEnrollmentResponse.getTransactionId());
        return cardEnrollmentResponse;
    }

    private void resultStatusInterpreter(Long externalApiCallId,Integer status,List<ErrorResponse> errors ){
        if(status!=1){
            externalApiUtil.externalApiCallChangeStatus(externalApiCallId,2,null);
            if(status==0)
                throw new InvalidDataException("Invalid Data", "common.bankCard.process_notFinished",this.parsError(errors));
            if(status==3)
                throw new InvalidDataException("Invalid Data", "common.bankCard.transaction_notFound",this.parsError(errors));
            if(status==2){
                throw new InvalidDataException("Invalid Data",errors.stream().map(e -> e.getShortError()).collect(Collectors.toList()), "common.bankCard.process_error",this.parsError(errors));
            }
        }
    }

    @Transactional
    @Override
    public BankCardWrapper approvedCardEnrollment(String trackingId) {
        if(Utils.isStringSafeEmpty(trackingId))
            throw new InvalidDataException("Invalid Data", "common.bankCard.trackingId_required");

        ExternalApiCall cardEnrollmentResponse=externalApiUtil.getExternalApiCallInfo(trackingId);
        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_CARD_INFO.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(),  HttpMethod.valueOf(externalApi.getHttpMethod()),null);
        CardInfoRequest cardInfoRequest=CardInfoRequest.builder()
                .transactionId(cardEnrollmentResponse.getResponseTransactionId()).build();

        HttpEntity<CardInfoRequest> entity = new HttpEntity<>(cardInfoRequest, this.createHeader(externalApiCall.getExternalApi()));
        ExternalApiResponse<CardInfoResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity,new ParameterizedTypeReference<CardInfoResponse>(){},true);

        CardInfoResponse cardInfoResponse =externalApiResponse.getResponseEntity() instanceof CardInfoResponse ? externalApiResponse.getResponseEntity() : Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),CardInfoResponse.class);
        this.resultStatusInterpreter(externalApiCall.getId(), cardInfoResponse.getStatus(),cardInfoResponse.getErrors());
        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,cardInfoResponse.getTransactionId());
        return  this.createBankCardFromSHP(cardInfoResponse);
    }
    private BankCardWrapper createBankCardFromSHP(CardInfoResponse cardInfoResponse){
        BankCard bankCard=null;
        try {
            bankCard=this.getBankCardInfo(cardInfoResponse.getMaskedPan());
        }catch (Exception e){}
        if(bankCard==null)
            bankCard=new BankCard();

        bankCard.setUserId(Utils.getCurrentUserId());
        bankCard.setCardNumber(cardInfoResponse.getMaskedPan());
        /*if(Utils.isStringSafeEmpty(bankCard.getName()))
           bankCard.setName(cardInfoResponse.getMaskedPan());*/
        bankCard.setActive(true);
        bankCard.setExpire(Utils.getNormalDateWithoutSlash(cardInfoResponse.getReferenceExpiryDate()).substring(0,6));

        bankCard.setShpTransactionId(cardInfoResponse.getTransactionId());
        bankCard.setShpCardId(cardInfoResponse.getCardId());
        bankCard.setShpMaskedPan(cardInfoResponse.getMaskedPan());
        bankCard.setShpReferenceExpiryDate(cardInfoResponse.getReferenceExpiryDate());
        bankCard.setShpPan(cardInfoResponse.getPan());
        bankCard.setShpPanExpiryDate(cardInfoResponse.getPanExpiryDate());
        bankCard.setShpAssuranceLevel(cardInfoResponse.getAssuranceLevel());
        bankCard=bankCardRepository.save(bankCard);
        return this.getBankCardWrapperInfo(bankCard.getId());

    }

    @Transactional
    @Override
    public BankCard renewCardId(Long bankCardId) {
       BankCard bankCard=this.getBankCardInfo(bankCardId);
       return this.renewCardId(bankCard);
    }

    private BankCard renewCardId(BankCard bankCard) {

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_RENEW_CARD_ID.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(),  HttpMethod.valueOf(externalApi.getHttpMethod()),null);

        CardIdRenewalRequest cardEnrollmentRequest=CardIdRenewalRequest.builder()
                .trackingNumber(externalApiCall.getTrackingId())
                //.cellphoneNumber(Utils.getCurrentUser().getMobileNumber())
                .cellphoneNumber("09124727739")
                .cardId(bankCard.getShpCardId())
                .referenceExpiryDate(bankCard.getShpReferenceExpiryDate()).build();

        HttpEntity<CardIdRenewalRequest> entity = new HttpEntity<>(cardEnrollmentRequest, this.createHeader(externalApiCall.getExternalApi()));


        ExternalApiResponse<CardIdRenewalResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity,new ParameterizedTypeReference<CardIdRenewalResponse>(){},true);
        CardIdRenewalResponse cardIdRenewalResponse =externalApiResponse.getResponseEntity()  instanceof CardIdRenewalResponse ? externalApiResponse.getResponseEntity() : Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),CardIdRenewalResponse.class);
        this.resultStatusInterpreter(externalApiCall.getId(), cardIdRenewalResponse.getStatus(),cardIdRenewalResponse.getErrors());
        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,cardIdRenewalResponse.getTransactionId());

        bankCard.setShpTransactionId(cardIdRenewalResponse.getTransactionId());
        bankCard.setShpCardId(cardIdRenewalResponse.getCardId());
        bankCard.setShpReferenceExpiryDate(cardIdRenewalResponse.getReferenceExpiryDate());
        bankCard=bankCardRepository.save(bankCard);
        return bankCard;
    }

    @Transactional
    @Override
    public AppReactivationResponse appReactivation(String platform) {
        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_APP_REACTIVATION.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null);

        AppReactivationRequest appReactivationRequest=AppReactivationRequest.builder()
                .trackingNumber(externalApiCall.getTrackingId())
                .cellphoneNumber("09124727739")
                //.cellphoneNumber(Utils.getCurrentUser().getMobileNumber())
                .appUrlPattern(!Utils.isStringSafeEmpty(platform) && platform.equalsIgnoreCase("WEB") ?
                                //"https://webjat.samatco.ir/#/bankcardlist/2?tkId=" + externalApiCall.getTrackingId() + "&keyId={}"
                                  "samatapp://open/bankcardlist/2?tkId=" + externalApiCall.getTrackingId() + "&keyId={}"
                                : "samatapp://open/bankcardlist/2?tkId=" + externalApiCall.getTrackingId() + "&keyId={}"
                               ).build();

        HttpEntity<AppReactivationRequest> entity = new HttpEntity<>(appReactivationRequest, this.createHeader(externalApiCall.getExternalApi()));
        ExternalApiResponse<AppReactivationResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity,new ParameterizedTypeReference<AppReactivationResponse>(){},true);
        AppReactivationResponse appReactivationResponse =externalApiResponse.getResponseEntity() instanceof AppReactivationResponse ? externalApiResponse.getResponseEntity() : Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),AppReactivationResponse.class);
        this.resultStatusInterpreter(externalApiCall.getId(), appReactivationResponse.getStatus(),appReactivationResponse.getErrors());
        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,appReactivationResponse.getTransactionId());
        return appReactivationResponse;
    }

    @Transactional
    @Override
    public CardHolderInquiryResponse cardHolderInquiry(BankCardHolderInquiryDto bankCardHolderInquiryDto, HttpServletRequest request) {
        BankCard bankCard= this.getBankCardInfo(bankCardHolderInquiryDto.getId());
        BankCardOperation bankCardOperation=cardOperationUtil.createBankCardOperation(bankCard,bankCardHolderInquiryDto.getDestinationCardNumber(),bankCardHolderInquiryDto.getDescription(),EOperation.CARD_TRANSFER,null,null,bankCardHolderInquiryDto.getAmount().doubleValue());
//        Long referenceNumber=REFERENCE_NUMBER_PREFIX +  bankCardOperation.getId();


        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_CARD_HOLDER_INQUIRY.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null,bankCardOperation.getTrackingId());

        if (bankCardHolderInquiryDto.getAmount() <= 0)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.amount_invalid");
        ETerminalType eTerminalType= !Utils.isStringSafeEmpty(bankCardHolderInquiryDto.getPlatform()) && bankCardHolderInquiryDto.getPlatform().equalsIgnoreCase("WEB") ?
                                      ETerminalType.INTERNET_PAYMENT_GATEWAY : ETerminalType.MOBILE_APP;
        CardHolderInquiryRequest cardHolderInquiryRequest=CardHolderInquiryRequest.builder()
                .trackingNumber(externalApiCall.getTrackingId())
                .sourcePAN(bankCard.getShpCardId().toString())
                .destinationPAN(bankCardHolderInquiryDto.getDestinationCardNumber())
                .amount(bankCardHolderInquiryDto.getAmount())
                .sourceAddress(Utils.getClientFirstIp(request))
                .localization(0)
                .referenceNumber(bankCardOperation.getTrackingCode())
                .acceptorCode(commonService.getConfigValue("shpAcceptorCode"))
                .terminalNumber(commonService.getConfigValue("shpTerminalNumber"))
                .terminalType(eTerminalType.getId())
                .build();


        HttpEntity<CardHolderInquiryRequest> entity = new HttpEntity<>(cardHolderInquiryRequest, this.createHeader(externalApiCall.getExternalApi()));
        ExternalApiResponse<CardHolderInquiryResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity,new ParameterizedTypeReference<CardHolderInquiryResponse>(){},true);
        CardHolderInquiryResponse cardHolderInquiryResponse =externalApiResponse.getResponseEntity() instanceof CardHolderInquiryResponse ? externalApiResponse.getResponseEntity() : Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),CardHolderInquiryResponse.class);
        this.resultStatusInterpreter(externalApiCall.getId(), cardHolderInquiryResponse.getStatus(),cardHolderInquiryResponse.getErrors());

        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,cardHolderInquiryResponse.getTransactionId());
        cardOperationUtil.editBankCardOperation( bankCardOperation,0,cardHolderInquiryResponse.getCardHolderName(),cardHolderInquiryResponse.getTransactionId(), cardHolderInquiryResponse.getTransactionDate(),
                                                 cardHolderInquiryResponse.getApprovalCode(),cardHolderInquiryResponse.getRegistrationDate(),cardHolderInquiryResponse.getRrn(),cardHolderInquiryResponse.getStan(),"");
        cardHolderInquiryResponse.setBankCardOperationId(bankCardOperation.getId());

        return cardHolderInquiryResponse;
    }

    @Transactional
    @Override
    public CardTransferResponse cardTransfer(BankCardTransferDto bankCardTransferDto, HttpServletRequest request) {
        BankCardOperation bankCardOperation=cardOperationUtil.getBankCardOperation(bankCardTransferDto.getBankCardOperationId());
        //Long  referenceNumber=REFERENCE_NUMBER_PREFIX +  bankCardOperation.getId();

        if(bankCardOperation.getStatus()>=2){
            CardTransferResponse result=new CardTransferResponse();
            result.setBankCardOperationId(bankCardOperation.getId());
            return result;
        }

        /*if(bankCardOperation.getStatus()==2)
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.duplicate_success_request");
        if(bankCardOperation.getStatus()>2)
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.duplicate_unSuccess_request");*/

        if(bankCardOperation.getBankCard().getShpCardId()!=this.getBankCardInfo(bankCardTransferDto.getId()).getShpCardId())
            throw new InvalidDataException("Invalid Data", "common.bankCard.id_invalid");
        if(!bankCardOperation.getTargetCard().equalsIgnoreCase(bankCardTransferDto.getDestinationCardNumber()))
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.targetCard_invalid");
        if(bankCardTransferDto.getAmount() <= 0 || bankCardOperation.getAmount().longValue()!=bankCardTransferDto.getAmount().longValue())
            throw new InvalidDataException("Invalid Data", "common.operationRequest.amount_invalid");
        if(!bankCardOperation.getShpApprovalCode().equalsIgnoreCase(bankCardTransferDto.getApprovalCode()))
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.shpApprovalCode_invalid");

        EPinCvvSecurityType ePinCvvSecurityType= EPinCvvSecurityType.valueOf(bankCardTransferDto.getSecureType());
        if(EPinCvvSecurityType.NO_SECURE==ePinCvvSecurityType && Utils.isStringSafeEmpty(bankCardTransferDto.getPin()))
            throw new InvalidDataException("Invalid Data", "common.bankCard.pin_required");


        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_CARD_TRANSFER_SYNC.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null,bankCardOperation.getTrackingId());
        ETerminalType eTerminalType= !Utils.isStringSafeEmpty(bankCardTransferDto.getPlatform()) && bankCardTransferDto.getPlatform().equalsIgnoreCase("WEB") ?
                                      ETerminalType.INTERNET_PAYMENT_GATEWAY : ETerminalType.MOBILE_APP;

        CardTransferRequest cardTransferRequest=CardTransferRequest.builder()
                .trackingNumber(externalApiCall.getTrackingId())
                .sourcePAN(bankCardOperation.getBankCard().getShpCardId().toString())
                .destinationPAN(bankCardOperation.getTargetCard() )
                .amount(bankCardOperation.getAmount().longValue())
                .pin(bankCardTransferDto.getPin())
                .cvv2(bankCardTransferDto.getCvv2())
                .expiryDate(bankCardOperation.getBankCard().getExpire().substring(2))
                .approvalCode(bankCardOperation.getShpApprovalCode())
                .sourceAddress(Utils.getClientFirstIp(request))
                .localization(0)
                .referenceNumber(bankCardOperation.getTrackingCode())
                .acceptorCode(commonService.getConfigValue("shpAcceptorCode"))
                .terminalNumber(commonService.getConfigValue("shpTerminalNumber"))
                .terminalType(eTerminalType.getId())
                .securityControl(EPinCvvSecurityType.valueOf(bankCardTransferDto.getSecureType()).getId())
                .build();

        HttpEntity<CardTransferRequest> entity = new HttpEntity<>(cardTransferRequest, this.createHeader(externalApiCall.getExternalApi()));
        ExternalApiResponse<CardTransferResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity,new ParameterizedTypeReference<CardTransferResponse>(){},true);
        CardTransferResponse cardTransferResponse =externalApiResponse.getResponseEntity() instanceof CardTransferResponse ? externalApiResponse.getResponseEntity() : Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),CardTransferResponse.class);
        try {
            this.resultStatusInterpreter(externalApiCall.getId(), cardTransferResponse.getStatus(),cardTransferResponse.getErrors());
        }catch (Exception e){

            cardOperationUtil.editBankCardOperationStatus( bankCardOperation.getId(),3,this.parsError(cardTransferResponse.getErrors()));
            CardTransferResponse result=new CardTransferResponse();
            result.setBankCardOperationId(bankCardOperation.getId());
            return result;
        }
        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,cardTransferResponse.getTransactionId());
        cardOperationUtil.editBankCardOperationStatus( bankCardOperation,2);
        cardTransferResponse.setBankCardOperationId(bankCardOperation.getId());
        return cardTransferResponse;
    }

    public Integer getShpOtpRemainTime(){
        Integer remainTime=0;
        Date oldShpOtpSendDate=(Date)cacheService.getCacheValue("shpOtpRequest", Utils.getCurrentUserId());
        if(oldShpOtpSendDate!=null)
            remainTime =((Integer)commonService.getConfigValue("shpOtpLiveTimeSecond"))  - calendarService.dateDiff(oldShpOtpSendDate,new Date(), com.core.model.enums.Calendar.SECOND);
        return remainTime;
    }

    @Transactional
    @Override
    public OtpDeliveryResponse requestOtp(OtpDeliveryDto otpDeliveryDto, HttpServletRequest request) {
        BankCardOperation bankCardOperation=cardOperationUtil.getBankCardOperation(otpDeliveryDto.getBankCardOperationId());

        Integer remainTime =this.getShpOtpRemainTime();
        if(remainTime>0) {
           OtpDeliveryResponse result=new OtpDeliveryResponse();
           result.setRemainTimeSecond(remainTime);
           return result;
        }

        if(bankCardOperation.getStatus()>1)
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.requestOtp_invalidState");


        if(bankCardOperation.getBankCard().getShpCardId()!=this.getBankCardInfo(otpDeliveryDto.getId()).getShpCardId())
            throw new InvalidDataException("Invalid Data", "common.bankCard.id_invalid");
        if(!bankCardOperation.getTargetCard().equalsIgnoreCase(otpDeliveryDto.getDestinationCardNumber()))
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.targetCard_invalid");
        if(otpDeliveryDto.getAmount() <= 0 || bankCardOperation.getAmount().longValue()!=otpDeliveryDto.getAmount().longValue())
            throw new InvalidDataException("Invalid Data", "common.operationRequest.amount_invalid");
        if(!bankCardOperation.getShpApprovalCode().equalsIgnoreCase(otpDeliveryDto.getApprovalCode()))
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.shpApprovalCode_invalid");
        if(!otpDeliveryDto.getRrn().equalsIgnoreCase(otpDeliveryDto.getRrn()))
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.shpRrn_invalid");
        if(otpDeliveryDto.getStan()!=otpDeliveryDto.getStan())
            throw new InvalidDataException("Invalid Data", "common.bankCardOperation.shpStan_invalid");

        ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHP_REQUEST_OTP.name());
        ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null,bankCardOperation.getTrackingId());

        ETerminalType eTerminalType= !Utils.isStringSafeEmpty(otpDeliveryDto.getPlatform()) && otpDeliveryDto.getPlatform().equalsIgnoreCase("WEB") ?
                ETerminalType.INTERNET_PAYMENT_GATEWAY : ETerminalType.MOBILE_APP;

        OtpDeliveryRequest otpDeliveryRequest=OtpDeliveryRequest.builder()
                .trackingNumber(externalApiCall.getTrackingId())
                .sourcePAN(bankCardOperation.getBankCard().getShpCardId().toString())
                .destinationPAN(bankCardOperation.getTargetCard())
                .amount(bankCardOperation.getAmount().longValue())
                .transactionType( ETransactionType.TRANSFER.getId())
                .acceptorCode(commonService.getConfigValue("shpAcceptorCode"))
                .acceptorName(commonService.getMyBrandName())
                .terminalNumber(commonService.getConfigValue("shpTerminalNumber"))
                .terminalType(eTerminalType.getId())
                .approvalCode(bankCardOperation.getShpApprovalCode())
                .rrn(bankCardOperation.getShpRrn())
                .stan(bankCardOperation.getShpStan())
                .accessAddress(Utils.getClientFirstIp(request))
                .build();

        otpDeliveryRequest=this.addDigitalSignature(otpDeliveryRequest);

        HttpEntity<OtpDeliveryRequest> entity = new HttpEntity<>(otpDeliveryRequest, this.createHeader(externalApiCall.getExternalApi()));
        ExternalApiResponse<OtpDeliveryResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,0,null,entity,new ParameterizedTypeReference<OtpDeliveryResponse>(){},true);
        OtpDeliveryResponse otpDeliveryResponse =externalApiResponse.getResponseEntity() instanceof OtpDeliveryResponse ? externalApiResponse.getResponseEntity() : Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),OtpDeliveryResponse.class);
        this.resultStatusInterpreter(externalApiCall.getId(), otpDeliveryResponse.getStatus(),otpDeliveryResponse.getErrors());
        externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,otpDeliveryResponse.getRequestId());
        cardOperationUtil.editBankCardOperationStatus( bankCardOperation,1);
        cacheService.putCacheValue("shpOtpRequest", Utils.getCurrentUserId() , otpDeliveryResponse.getRegistrationDate(), commonService.getConfigValue("shpOtpLiveTimeSecond") , TimeUnit.SECONDS);
        otpDeliveryResponse.setRemainTimeSecond(((Integer)commonService.getConfigValue("shpOtpLiveTimeSecond"))  - calendarService.dateDiff(otpDeliveryResponse.getRegistrationDate(),new Date(), com.core.model.enums.Calendar.SECOND));
        return otpDeliveryResponse;
    }

    private  OtpDeliveryRequest addDigitalSignature(OtpDeliveryRequest otpDeliveryRequest){
        String sigString =  (otpDeliveryRequest.getTrackingNumber()!=null ? otpDeliveryRequest.getTrackingNumber():"null")+"|"+
                            (otpDeliveryRequest.getSourcePAN()!=null ? otpDeliveryRequest.getSourcePAN():"null")+"|"+
                            (otpDeliveryRequest.getDestinationPAN()!=null ? otpDeliveryRequest.getDestinationPAN() :"null")+"|" +
                            (otpDeliveryRequest.getAmount()!=null ? otpDeliveryRequest.getAmount().toString() :"null")+"|"+
                            (otpDeliveryRequest.getTransactionType()!=null ? otpDeliveryRequest.getTransactionType().toString() :"null")+"|"+
                            (otpDeliveryRequest.getAcceptorCode()!=null ? otpDeliveryRequest.getAcceptorCode():"null")+"|"+
                            (otpDeliveryRequest.getTerminalNumber()!=null ?otpDeliveryRequest.getTerminalNumber() : "null")+"|"+
                            (otpDeliveryRequest.getTerminalType()!=null ? otpDeliveryRequest.getTerminalType().toString() : "null")+"|"+
                            (otpDeliveryRequest.getAccessAddress()!=null ? otpDeliveryRequest.getAccessAddress() : "null")+"|"+
                            (otpDeliveryRequest.getRrn()!=null ? otpDeliveryRequest.getRrn() : "null")+"|"+
                            (otpDeliveryRequest.getStan()!=null ? otpDeliveryRequest.getStan().toString() : "null")+"|";
        String signature=Utils.sign(sigString,commonService.getConfigValue("shpOtpHashAlgorithm"),commonService.getConfigValue("cardPrivateKey"));



        otpDeliveryRequest.setSecurityType(ESecurityType.DIGITAL_SIGNATURE.getId());
        otpDeliveryRequest.setSecurityFactor(signature);
        return otpDeliveryRequest;
    }

    private  String parsError(List<ErrorResponse> errors){
        StringBuffer errorDesc=new StringBuffer();
        Boolean flag=false;
        for (ErrorResponse e: errors) {
            switch (e.getErrorCode()) {
                case 0:
                    errorDesc.append("خطای عمومی - کد خطا: 0");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                case 1:
                    errorDesc.append("اطلاعات ارسالی ناقص است - کد خطا: 1");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 2:
                    errorDesc.append("اطلاعات ارسالی صحیح نمی باشد - کد خطا: 2");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 3:
                    errorDesc.append("اطلاعات ارسالی تکرار است - کد خطا: 3");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                case 4:
                    errorDesc.append("عدم همخوانی اطلاعات ارسالی - کد خطا: 4");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                case 5:
                    errorDesc.append("عدم همخوانی اطلاعات ارسالی - کد خطا: 5");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 6:
                    errorDesc.append("اطلاعات ارسالی معتبر نمی باشد - کد خطا: 6");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 7:
                    errorDesc.append("موجودی کافی نمی باشد - کد خطا: 7");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 8:
                    errorDesc.append("عدم دسترسی به اطلاعات - کد خطا: 8");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 9:
                    errorDesc.append("عدم امکان انجام عملیات - کد خطا: 9");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 10:
                    errorDesc.append("عدم امکان انجام عملیات - کد خطا: 10");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 11:
                    errorDesc.append("عدم پاسخگویی سرویس دهنده - کد خطا: 11");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 12:
                    errorDesc.append("عدم دریافت پاسخ - کد خطا: 12");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 13:
                    errorDesc.append("عدم دریافت پاسخ - کد خطا: 13");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 14:
                    errorDesc.append("عدم دریافت پاسخ - کد خطا: 14");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 15:
                    errorDesc.append("عدم امکان ارائه سرویس درخواستی - کد خطا: 15");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 16:
                    errorDesc.append("خطای در اطلاعات امنیتی - کد خطا: 16");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 17:
                    errorDesc.append("اطلاعات ارسالی صحیح نمی باشد - کد خطا: 17");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 18:
                    errorDesc.append("مرجع غیر فعال است - کد خطا: 18");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                case 19:
                    errorDesc.append("عدم اعتبار اطلاعات - کد خطا: 19");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
                default:
                    errorDesc.append("خطای نامشخص - کد خطا: 20");
                    if(flag)
                        errorDesc.append("\r\n");
                    flag=true;
                    break;
            }
        }


        return errorDesc.toString();
    }



    //#endregion
}
