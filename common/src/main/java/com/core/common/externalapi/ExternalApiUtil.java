package com.core.common.externalapi;

import com.core.common.model.enums.EExternalApi;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.datamodel.model.enums.EExternalApiCallStatus;
import com.core.datamodel.model.wrapper.ExternalApiCallWrapper;
import com.core.datamodel.repository.ExternalApiCallRepository;
import com.core.datamodel.repository.ExternalApiRepository;
import com.core.datamodel.services.CacheService;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;
import com.core.services.CalendarService;
import com.core.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component("externalApiUtil")
public class ExternalApiUtil {

    @Value("#{${externalApiCall.search.native.private.params}}")
    private HashMap<String, List<String>> EXTERNAL_API_CALL_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;


    private CalendarService calendarService;
    private CacheService cacheService;
    private ExternalApiRepository externalApiRepository;
    private ExternalApiCallRepository externalApiCallRepository;
    private DynamicQueryHelper dynamicQueryHelper;


    @Autowired
    public ExternalApiUtil(CalendarService calendarService,CacheService cacheService, ExternalApiRepository externalApiRepository,
                           ExternalApiCallRepository externalApiCallRepository, DynamicQueryHelper dynamicQueryHelper) {
        this.calendarService=calendarService;
        this.cacheService=cacheService;
        this.externalApiRepository=externalApiRepository;
        this.externalApiCallRepository=externalApiCallRepository;
        this.dynamicQueryHelper=dynamicQueryHelper;
    }



    public List<ExternalApiCall> findByNewestByExternalApiId(Long externalApiId,Integer requestKey,Integer validityMinute){
        Date validDate=calendarService.addMinuteToDate(new Date(),-validityMinute);
        return  externalApiCallRepository.findByNewestByExternalApiId(externalApiId,requestKey,validDate,Utils.gotoPage(0,1));
    }

    public List<ExternalApi> getAllExternalApis() {
        return externalApiRepository.findAllExternalApis();
    }

    public ExternalApi getExternalApiInfo(String externalApiName){
        ExternalApi externalApi=null;

        externalApi=(ExternalApi) cacheService.getCacheValue("externalApi", externalApiName);
        if (externalApi != null)
            return externalApi;

        Optional<ExternalApi> result= externalApiRepository.findByEntityName(externalApiName);
        if (!result.isPresent())
            throw new ResourceNotFoundException(externalApiName, "common.externalApiName.name_notFound" , externalApiName );

        cacheService.putCacheValue("externalApi", externalApiName, result.get(),30,  TimeUnit.DAYS);
        return result.get();
    }

    public ExternalApi getExternalApiInfo(Long externalApiId){
        ExternalApi externalApi=null;

        externalApi=(ExternalApi) cacheService.getCacheValue("externalApi", externalApiId);
        if (externalApi != null)
            return externalApi;

        Optional<ExternalApi> result= externalApiRepository.findByEntityId(externalApiId);
        if (!result.isPresent())
            throw new ResourceNotFoundException(externalApiId.toString(), "common.externalApiName.id_notFound" , externalApiId );

        cacheService.putCacheValue("externalApi", externalApiId, result.get(),30,  TimeUnit.DAYS);
        return result.get();
    }


    /*@Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall createExternalApiCall(EExternalApi eExternalApi, String address,HttpMethod httpMethod, String requestEntity){
        ExternalApi externalApi=this.getExternalApiInfo(eExternalApi.name());
        return this.createExternalApiCall(externalApi,address, httpMethod, requestEntity);
    }*/

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall createExternalApiCall(ExternalApi externalApi, String address,HttpMethod httpMethod, String requestEntity){
        return this.createExternalApiCall(externalApi,address, httpMethod, requestEntity,null);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall createExternalApiCall(ExternalApi externalApi, String address,HttpMethod httpMethod, String requestEntity,String processId){
        ExternalApiCall externalApiCall=new ExternalApiCall();
        externalApiCall.setTrackingId(Utils.createUniqueRandom());
        if(!Utils.isStringSafeEmpty(processId))
           externalApiCall.setProcessId(processId);
        else
           externalApiCall.setProcessId(externalApiCall.getTrackingId());
        externalApiCall.setAddress(address);
        externalApiCall.setHttpMethod(httpMethod.name());
        externalApiCall.setExternalApi(externalApi);
        externalApiCall.setRequest(requestEntity);
        externalApiCall=externalApiCallRepository.saveAndFlush(externalApiCall);
        return externalApiCall;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall updateExternalApiCallRequest(ExternalApiCall externalApiCall,String requestEntity,Integer requestKey){
        externalApiCall.setRequest(requestEntity);
        externalApiCall.setRequestKey(requestKey);
        externalApiCall=externalApiCallRepository.saveAndFlush(externalApiCall);
        return externalApiCall;
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall updateExternalApiCallResponse(Long externalApiCallId,Integer status,String  responseEntity,Long responseReferenceId){
        return this.updateExternalApiCallResponse(this.getExternalApiCallInfo(externalApiCallId), status,  responseEntity, responseReferenceId);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall updateExternalApiCallResponse(ExternalApiCall externalApiCall,Integer status,String  responseEntity,Long responseReferenceId){
        externalApiCall.setStatus(status);
        externalApiCall.setResponse(responseEntity);
        externalApiCall.setResponseDate(Utils.getCurrentDateTime());
        externalApiCall.setResponseReferenceId(responseReferenceId);
        externalApiCall=externalApiCallRepository.saveAndFlush(externalApiCall);
        return externalApiCall;
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall externalApiCallChangeStatus(Long externalApiCallId, Integer status,String transactionId) {
        ExternalApiCall externalApiCall=this.getExternalApiCallInfo(externalApiCallId);
        return this.externalApiCallChangeStatus(externalApiCall,status,transactionId);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall externalApiCallChangeStatus(ExternalApiCall externalApiCall, Integer status,String transactionId) {
        if(status<0 && status>2)
            throw new ResourceNotFoundException("Invalid Data", "common.externalApiCall.status_invalid");

        if(!Utils.isStringSafeEmpty(transactionId))
            externalApiCall.setResponseTransactionId(transactionId);
        externalApiCall.setStatus(status);
        externalApiCall=externalApiCallRepository.saveAndFlush(externalApiCall);
        return externalApiCall;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ExternalApiCall getExternalApiCallInfo(Long externalApiCallId){
        if (externalApiCallId==null)
            throw new ResourceNotFoundException("Invalid Data", "common.externalApiCall.id_required");

        Optional<ExternalApiCall> result= externalApiCallRepository.findByEntityId(externalApiCallId);
        if (!result.isPresent())
            throw new ResourceNotFoundException(externalApiCallId.toString(), "common.externalApiCall.id_notFound");

        return result.get();
    }
    public ExternalApiCall getExternalApiCallInfo(String trackingId){
        if (Utils.isStringSafeEmpty(trackingId))
            throw new ResourceNotFoundException("Invalid Data", "common.externalApiCall.trackingId_required");

        Optional<ExternalApiCall> result= externalApiCallRepository.findByTrackingId(trackingId);
        if (!result.isPresent())
            throw new ResourceNotFoundException(trackingId, "common.externalApiCall.trackingId_notFound");

        return result.get();
    }



    //region External Api Call Report

    public ResultListPageable<ExternalApiCallWrapper> getExternalApiCallWrappers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(EXTERNAL_API_CALL_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "externalApiCallWrapperMapping");
        ResultListPageable<ExternalApiCallWrapper> externalApiCallGeneral=this.getExternalApiCallGeneral(requestParams);
        return externalApiCallGeneral;
    }


    public ExternalApiCallWrapper getExternalApiCallWrapperInfo(Long externalApiCallId) {
        if(externalApiCallId==null)
            throw new InvalidDataException("Invalid Data", "common.externalApiCall.id_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("id",externalApiCallId);
        requestParams.put("start",0);
        requestParams.put("count",1);
        requestParams.put("resultSetMapping", "externalApiCallWrapperMapping");

        ResultListPageable<ExternalApiCallWrapper> result=this.getExternalApiCallGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);

        throw new ResourceNotFoundException(externalApiCallId.toString(), "common.externalApiCall.id_notFound" );
    }


    private String getBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return "select c.eac_id as id, c.eac_eap_id apiId, a.eap_name as apiName,a.eap_description as apiDescription,\n" +
                "        c.eac_status as status,  c.eac_address as address,  c.eac_http_method as httpMethod,\n" +
                "        "+ (loadDetails ? "c.eac_request" : "''") +" as requestBody,  c.eac_request_key as requestKey,  " +
                "        "+ (loadDetails ? "c.eac_response" : "''") +" as responseBody,c.eac_response_date as responseDate, \n" +
                "        c.eac_reference_id as referenceId, c.eac_tracking_id as trackingId,\n" +
                "        c.eac_process_id as processId, c.eac_transaction_id as transactionId, c.eac_create_date as createDate,\n" +
                "        c.eac_create_by as createBy, c.eac_modify_date as modifyDate, c.eac_modify_by as modifyBy " ;

    }
    private String getBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }


    private String getBaseQueryBody(Map<String, Object> requestParams) {
        String queryString="  from sc_external_api_call c\n" +
                " inner join sc_external_api a on (a.eap_id=c.eac_eap_id) \n";
        return  queryString;
    }

    private ResultListPageable<ExternalApiCallWrapper> getExternalApiCallGeneral(Map<String, Object> requestParams) {

        String queryString = this.getBaseQueryHead(requestParams)+ this.getBaseQueryBody(requestParams);
        //Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getBaseCountQueryHead(requestParams)+this.getBaseQueryBody(requestParams);

        return dynamicQueryHelper.getDataGeneral(requestParams,EXTERNAL_API_CALL_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,ExternalApiCallWrapper.class);
    }


    public List<TypeWrapper> getExternalApiCallStatuses() {
        return EExternalApiCallStatus.getAllAsObjectWrapper();
    }

    //endregion  External Api Call Report



}
