package com.core.common.externalapi;

import com.core.common.util.SSLUtilities;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.exception.ExternalApiCallException;
import com.core.exception.MyException;
import com.core.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Component("externalApiHelper")
public class ExternalApiHelper{

    private RestTemplate restTemplate;
    private ObjectMapper jsonMapper;
    private ExternalApiUtil externalApiUtil;
    private ObjectMapper mapper;


    @Autowired
    public ExternalApiHelper(RestTemplate restTemplate,ObjectMapper jsonMapper,ExternalApiUtil externalApiUtil,ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.jsonMapper=jsonMapper;
        this.externalApiUtil=externalApiUtil;
        this.mapper=mapper;

    }

    @Transactional
    public <T> ExternalApiResponse<T> callRestApiExchange(ExternalApiCall externalApiCall,Integer requestKey, /*EExternalApi eExternalApi,*/ String additionalAddress, @Nullable HttpEntity<?> requestEntity,ParameterizedTypeReference<T> responseType, Boolean forceFresh ) {
        return this.callRestApiExchange(externalApiCall,requestKey, additionalAddress, requestEntity,null,responseType,forceFresh );
    }

    @Transactional
    public <T>  ExternalApiResponse<T> callRestApiExchange(ExternalApiCall externalApiCall,Integer requestKey, /*EExternalApi eExternalApi,*/ String additionalAddress, @Nullable HttpEntity<?> requestEntity, @Nullable Map<String,?> uriVariable, ParameterizedTypeReference<T> responseType,Boolean forceFresh ){
        ExternalApiResponse<T> result=new ExternalApiResponse();

        ExternalApi externalApi=externalApiCall.getExternalApi();
        if (!externalApi.getActive())
            throw new ResourceNotFoundException(externalApi.getName(), "common.externalApiName.inActive_hint" , externalApi.getName());

        externalApiCall=externalApiUtil.updateExternalApiCallRequest(externalApiCall,(!Utils.isStringSafeEmpty(additionalAddress)?"Additional Address ="+additionalAddress + " , ":"") + Utils.getAsJson(requestEntity),requestKey);

        result.setExternalApi(externalApi);
        if(!forceFresh && externalApi.getCacheable() && externalApi.getValidityMinutes()>0 ){
            List<ExternalApiCall> cacheResult=externalApiUtil.findByNewestByExternalApiId(externalApi.getId(),requestKey,externalApi.getValidityMinutes());
            if(cacheResult!=null && cacheResult.size()>0 && cacheResult.get(0)!=null) {
                T responseEntity;

                TypeReference typeReference=new TypeReference<T>(){
                    public Type getType() {
                        return responseType.getType();
                    }
                };
                responseEntity =Utils.readFromJson(cacheResult.get(0).getResponse(),typeReference);
                //responseEntity = jsonMapper.readValue(cacheResult.get(0).getResponse(), responseValueType);

                externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall,cacheResult.get(0).getStatus(),cacheResult.get(0).getResponse(),cacheResult.get(0).getId());
                result.setExternalApiCallId(externalApiCall.getId());
                result.setResponseEntity(responseEntity);
                result.setFromCache(true);
                return result;
            }
        }

        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        //String address=externalApi.getAddress()+(!Utils.isStringSafeEmpty(additionalAddress)?additionalAddress:"");
        HttpMethod httpMethod=HttpMethod.valueOf(externalApi.getHttpMethod());
        ResponseEntity<T>  responseEntity ;

        if (requestEntity.getHeaders().get("content-type")!=null && requestEntity.getHeaders().get("content-type").contains("application/x-www-form-urlencoded"))
           restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));

        try {
            if(httpMethod==HttpMethod.POST){
                if(uriVariable!=null)
                   responseEntity = restTemplate.exchange(externalApi.getAddress()+(!Utils.isStringSafeEmpty(additionalAddress)?"?"+additionalAddress:""),httpMethod, requestEntity, new ParameterizedTypeReference<T>() {},uriVariable);
                else
                   responseEntity = restTemplate.exchange(externalApi.getAddress()+(!Utils.isStringSafeEmpty(additionalAddress)?"?"+additionalAddress:""),httpMethod, requestEntity, new ParameterizedTypeReference<T>() {});

            }else if (httpMethod==HttpMethod.GET) {
                if (uriVariable != null)
                    responseEntity = restTemplate.exchange(externalApi.getAddress() + (!Utils.isStringSafeEmpty(additionalAddress) ? "?" + additionalAddress : ""), httpMethod, requestEntity, new ParameterizedTypeReference<T>() {}, uriVariable);
                else
                    responseEntity = restTemplate.exchange(externalApi.getAddress() + (!Utils.isStringSafeEmpty(additionalAddress) ? "?" + additionalAddress : ""), httpMethod, requestEntity, new ParameterizedTypeReference<T>() {});
            }else
                responseEntity = restTemplate.exchange(externalApi.getAddress()+(!Utils.isStringSafeEmpty(additionalAddress)?"?"+additionalAddress:""),httpMethod, requestEntity, new ParameterizedTypeReference<T>() {});
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall,1,Utils.getAsJson(responseEntity.getBody()),null);
        }catch (Exception e){
            externalApiCall=externalApiUtil.updateExternalApiCallResponse(externalApiCall,2,e.getMessage(),null);
            e.printStackTrace();
            throw new ExternalApiCallException("External Service Call Error", "global.externalServiceCall_error");
        }
        externalApiCall.setExternalApi(externalApi);
        result.setExternalApiCallId(externalApiCall.getId());
        result.setExternalApiCall(externalApiCall);
        result.setResponseEntity(responseEntity.getBody());
        result.setFromCache(false);
        return result;
    }



}
