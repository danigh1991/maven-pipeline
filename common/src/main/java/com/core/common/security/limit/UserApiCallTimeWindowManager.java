package com.core.common.security.limit;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.AllowedIpPath;
import com.core.datamodel.model.dbmodel.PathLimitation;
import com.core.datamodel.model.securitymodel.RequestPerIpDetails;
import com.core.datamodel.services.CacheService;
import com.core.exception.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class UserApiCallTimeWindowManager {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private CommonService commonService;

    public UserApiCallTimeWindowManager() {

    }

    public synchronized boolean removeUserRequest(HttpServletRequest request, HttpServletResponse response,Long userId) {
        String key=this.getKey(userId,"");
        RequestPerIpDetails requestPerIpDetails=(RequestPerIpDetails) cacheService.getCacheValue("userApiCall",key);
        if (requestPerIpDetails!=null && requestPerIpDetails.getRequestsPerIp().size()>0){
            requestPerIpDetails.getRequestsPerIp().remove(0);
            cacheService.putCacheValue("userApiCall",key,requestPerIpDetails,1, TimeUnit.DAYS);
        }
        return true;
    }

    private String getKey(Long userId,String  pathLimitationName){
        String key=userId.toString();
        if (!Utils.isStringSafeEmpty(pathLimitationName))
            key=key+"_"+pathLimitationName;
        return key;
    }

    public synchronized Boolean checkAndAddUserRequest(HttpServletRequest request, HttpServletResponse response,Long userId) throws IOException {
        if( userId!=null && userId>0) {
            long epochSecond = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            String key=this.getKey(userId,"");
            RequestPerIpDetails requestPerIpDetails=(RequestPerIpDetails) cacheService.getCacheValue("userApiCall",key);
            requestPerIpDetails=Utils.refreshRequests(requestPerIpDetails,epochSecond);
            requestPerIpDetails=Utils.cleanExpiredRequests(requestPerIpDetails,commonService.getApiCallDurationLimit());
            cacheService.putCacheValue("userApiCall",key,requestPerIpDetails,commonService.getApiCallDurationLimit(), TimeUnit.SECONDS);


            if (this.ipAddressReachedLimit(requestPerIpDetails,commonService.getApiCallCountLimit())) {
                return false;
            }

            return true;
        }
        return  true;
    }

    private synchronized boolean ipAddressReachedLimit(RequestPerIpDetails requestPerIpDetails,Integer maxRequestSizePerIp) {
        int amountRequests = requestPerIpDetails.getRequestsPerIp().size();
        return (amountRequests > maxRequestSizePerIp);
    }

}
