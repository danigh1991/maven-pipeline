package com.core.common.services.impl.sms;

import com.core.common.services.factory.AbstractSmsService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.datamodel.model.dbmodel.SmsBodyCode;
import com.core.common.model.sms.SmsServiceResponse;
import com.core.datamodel.repository.SmsBodyCodeRepository;
import com.core.datamodel.services.CacheService;
import com.core.common.services.SmsService;
import com.core.common.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Service("MelliPayamakServiceImpl")
public class MelliPayamakServiceImpl extends AbstractSmsService implements SmsService {

    @Value("${app.sms.send_url}")
    private String smsSendUrl;

    @Value("${app.sms.send_base_url}")
    private String smsSendBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private SmsBodyCodeRepository smsBodyCodeRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    public SmsServiceResponse send( String[] toNumber, String message, Boolean isFlash) {
        return send(Utils.getAppPropery("app.sms.send_from_number","50001060657972"),
                    toNumber,message,isFlash);
    }

    @Override
    public SmsServiceResponse send(String fromNumber,String[] toNumber, String message, Boolean isFlash) {
        try {
            String numbers="";
            for (String s:toNumber ) {
                if (numbers == "")
                    numbers = s;
                else
                    numbers = numbers + "," + s;
            }
            MultiValueMap<String, String> map=createModel(numbers,message);
            map.add("from",fromNumber);
            map.add("isflash",isFlash.toString());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, createHeader());
           // HttpEntity<String> requestBody = new HttpEntity<>(sendSmsRequest.toStringForSend(), headers);

           ResponseEntity<String> result=restTemplate.exchange(smsSendUrl, HttpMethod.POST,entity,String.class);
          //  SmsServiceResponse result=restTemplate.postForObject(smsSendUrl, entity,SmsServiceResponse.class);
           SmsServiceResponse smsServiceResponse =jsonMapper.readValue(result.getBody(), SmsServiceResponse.class);
           if (smsServiceResponse.getRetStatus()!=1)
                throw new InvalidDataException("Send Sms Error", "sms.send_notOk");

            return smsServiceResponse;
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Send Sms Error", e.getMessage());
        } catch (IOException e) {
           e.printStackTrace();
           throw new InvalidDataException("Invalid Data", "sms.send_invalidResponse");
        }
    }

    @Override
    public SmsServiceResponse baseSend(String toNumber, String message, Integer bodyId) {
        try {
            MultiValueMap<String, String> map=createModel(toNumber,message);
            map.add("bodyId",bodyId.toString());
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, createHeader());

            ResponseEntity<String> result=restTemplate.exchange(smsSendBaseUrl,HttpMethod.POST, entity,String.class);
            SmsServiceResponse smsServiceResponse =jsonMapper.readValue(result.getBody(), SmsServiceResponse.class);
            if (smsServiceResponse.getRetStatus()!=1)
                throw new InvalidDataException("Send Sms Error", "sms.send_notOk");
            return smsServiceResponse;
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Send Sms Error", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidDataException("Invalid Data", "sms.send_invalidResponse");
        }
    }

    private MultiValueMap<String, String> createModel(String toNumber, String message){
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", Utils.getAppPropery("app.sms.username",""));
        map.add("password", Utils.getAppPropery("app.sms.password", ""));
        map.add("to",toNumber);
        map.add("text",message);
        return map;
    }

    private HttpHeaders createHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        //headers.add("content-type",MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    @Override
    public String getDeliverStatus() {
        return null;
    }

    @Override
    public String getMessages() {
        return null;
    }

    @Override
    public String getCredit() {
        return null;
    }

    @Override
    public String getBasePrice() {
        return null;
    }

    @Override
    public String getUserNumber() {
        return null;
    }


    @Override
    public SmsBodyCode getBodyCode(Long smsBodyCodeId) {
        SmsBodyCode result= (SmsBodyCode ) cacheService.getCacheValue("smsBodyCode", Utils.addGlobalCacheKey(smsBodyCodeId));
        if (result != null)
            return result;
        result=smsBodyCodeRepository.findByEntityId(smsBodyCodeId);
        if (result == null)
            throw new ResourceNotFoundException(smsBodyCodeId.toString(), "sms.body.code_notFound" , smsBodyCodeId );

        cacheService.putCacheValue("smsBodyCode", Utils.addGlobalCacheKey(smsBodyCodeId), result,30, TimeUnit.DAYS);
        return result;
    }
}



