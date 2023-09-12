package com.core.common.services.impl;

import com.core.common.externalapi.ExternalApiHelper;
import com.core.common.externalapi.ExternalApiResponse;
import com.core.common.externalapi.ExternalApiUtil;
import com.core.common.model.enums.EExternalApi;
import com.core.common.model.shahkar.ShahkarServiceRequest;
import com.core.common.model.shahkar.ShahkarServiceResponse;
import com.core.common.model.sms.SmsServiceResponse;
import com.core.common.services.CommonService;
import com.core.common.util.SSLUtilities;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.datamodel.repository.SmsBodyCodeRepository;
import com.core.datamodel.services.CacheService;
import com.core.exception.InvalidDataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service("shahkarServiceImpl")
public class ShahkarServiceImpl extends AbstractService{


    private ExternalApiHelper externalApiHelper;
    private ExternalApiUtil externalApiUtil;
    private CommonService commonService;

    @Autowired
    public ShahkarServiceImpl(ExternalApiHelper externalApiHelper, ExternalApiUtil externalApiUtil, CommonService commonService) {
        this.externalApiHelper = externalApiHelper;
        this.externalApiUtil = externalApiUtil;
        this.commonService = commonService;
    }

    public ShahkarServiceResponse callService(ShahkarServiceRequest shahkarServiceRequest) {
        try {
            ExternalApi externalApi=externalApiUtil.getExternalApiInfo(EExternalApi.SHAHKAR.name());
            ExternalApiCall externalApiCall=externalApiUtil.createExternalApiCall(externalApi,externalApi.getAddress(), HttpMethod.valueOf(externalApi.getHttpMethod()),null);

            URI addressUri= new URI(externalApi.getAddress());

            LinkedMap<String, String> map = this.createModel(shahkarServiceRequest.getServiceNumber(), shahkarServiceRequest.getServiceType(), shahkarServiceRequest.getIdentificationType(), shahkarServiceRequest.getIdentificationNo());
            String dataForSign = "POST#" + addressUri.getPath() + "#" + Utils.deCrypt(externalApi.getApiKey()) + "#" + Utils.getAsJson(map);
            String signature = Utils.sign(dataForSign, commonService.getShahkarHashAlgorithm(), commonService.getShahkarPrivetKey());
            HttpEntity<LinkedMap<String, String>> entity = new HttpEntity<>(map, this.createHeader(signature, Utils.deCrypt(externalApi.getApiKey()), true, null));

            ExternalApiResponse<ShahkarServiceResponse> externalApiResponse= externalApiHelper.callRestApiExchange(externalApiCall,shahkarServiceRequest.getKeyAsHash(),null,entity, new ParameterizedTypeReference<ShahkarServiceResponse>(){} ,false);

            ShahkarServiceResponse shahkarServiceResponse =externalApiResponse.getResponseEntity() instanceof ShahkarServiceResponse ?  externalApiResponse.getResponseEntity() :Utils.mapToPojo((Map) externalApiResponse.getResponseEntity(),ShahkarServiceResponse.class);
            if (!shahkarServiceResponse.getResult().equalsIgnoreCase("OK."))
                throw new InvalidDataException("Invalid IdentificationNo", "shahkar.matchingId.result_invalid");
            externalApiCall=externalApiUtil.externalApiCallChangeStatus(externalApiCall.getId(),1,shahkarServiceResponse.getRequestId());
            return shahkarServiceResponse;
        }catch (InvalidDataException ex){
            throw ex;
        }catch (Exception e) {
            e.printStackTrace();
            throw new InvalidDataException("Invalid Data", "shahkar.response_invalid");
        }
    }

    private LinkedMap<String, String> createModel(String serviceNumber, Integer serviceType, Integer identificationType, String identificationNo){
        LinkedMap<String, String> map= new LinkedMap<String, String>();
        map.put("ServiceNumber", serviceNumber);
        map.put("ServiceType", serviceType.toString());
        map.put("IdentificationType",identificationType.toString());
        map.put("IdentificationNo",identificationNo);
        return map;
    }

    private HttpHeaders createHeader(String signature,String apiKey,Boolean isPure,String acceptVersion){
        HttpHeaders headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        headers.add("content-type",MediaType.APPLICATION_JSON_VALUE);
        headers.add("Signature", signature);
        headers.add("ApiKey", apiKey);
        if(isPure!=null)
           headers.add("IsPure", isPure.toString().toUpperCase());
        if(!Utils.isStringSafeEmpty(acceptVersion))
           headers.add("Accept-Version", acceptVersion);
        //headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
