package com.core.common.security.limit;

import com.core.common.services.CommonService;
import com.core.datamodel.model.dbmodel.AllowedIpPath;
import com.core.datamodel.model.dbmodel.PathLimitation;
import com.core.datamodel.model.securitymodel.RequestPerIpDetails;
import com.core.datamodel.services.CacheService;
import com.core.common.util.Utils;
import com.core.exception.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class IpTimeWindowManager {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private CommonService commonService;

    public static final int WINDOW_SIZE_IN_MINUTES = 1440;
    public static final int MAX_REQUEST_PER_IP_IN_WINDOW = 2;


    public IpTimeWindowManager() {
    }

    public synchronized boolean removeIpRequest(HttpServletRequest request, HttpServletResponse response) {
        String key=this.getKey(request,"");
        RequestPerIpDetails requestPerIpDetails=(RequestPerIpDetails) cacheService.getCacheValue("antiCrawler",key);
        if (requestPerIpDetails!=null && requestPerIpDetails.getRequestsPerIp().size()>0){
            requestPerIpDetails.getRequestsPerIp().remove(0);
            cacheService.putCacheValue("antiCrawler",key,requestPerIpDetails,1, TimeUnit.DAYS);
        }
        return true;
    }
    private String getKey(HttpServletRequest request,String  pathLimitationName){
        String key=Utils.getClientIp(request);
        if (!Utils.isStringSafeEmpty(pathLimitationName))
            key=key+"_"+pathLimitationName;

        return key;
    }

    public synchronized Boolean checkAndAddIpRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ip=Utils.getClientFirstIp(request);
        if( ip!=null) {
            if(commonService.getBlockedIps().stream().filter(i-> ip.startsWith(i)).count()>0 )
                return false;

            for (AllowedIpPath activeAllowedIpPath : commonService.getActiveAllowedIpPaths()){
               if(Arrays.stream(activeAllowedIpPath.getPath().split(",")).anyMatch(allowedPath ->  request.getRequestURI().startsWith(allowedPath)) &&
                  !Arrays.stream(activeAllowedIpPath.getIp().split(",")).anyMatch(allowedIp ->  ip.startsWith(allowedIp)) )
                   throw new NoAccessException("no Access",Utils.getMessageResource("global.accessDeniedMessage2"));
                  //return false;
            };

            List<PathLimitation> pathLimitationConfigs = commonService.getLimitationPathConfigs();
            for (PathLimitation pathLimitation : pathLimitationConfigs)
                if ( Arrays.stream(pathLimitation.getPath().split(",")).anyMatch(limitedPath ->  request.getRequestURI().startsWith(limitedPath)))
                    return saveAndCheckRequestData(request,response,pathLimitation);
        }
        return  true;
    }
    private synchronized Boolean saveAndCheckRequestData(HttpServletRequest request, HttpServletResponse response,PathLimitation pathLimitationConfig) throws IOException {
        long epochSecond = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        String key=this.getKey(request, pathLimitationConfig.getName());

        RequestPerIpDetails requestPerIpDetails=(RequestPerIpDetails) cacheService.getCacheValue("antiCrawler",key);
        requestPerIpDetails=Utils.refreshRequests(requestPerIpDetails,epochSecond);

        requestPerIpDetails=Utils.cleanExpiredRequests(requestPerIpDetails,pathLimitationConfig.getTimeSize());
        cacheService.putCacheValue("antiCrawler",key,requestPerIpDetails,pathLimitationConfig.getTimeSize(), TimeUnit.SECONDS);

        if (this.ipAddressReachedLimit(requestPerIpDetails,pathLimitationConfig.getAddToBlockCount())) {
            Boolean blockActive=true;
        }

        if (this.ipAddressReachedLimit(requestPerIpDetails,pathLimitationConfig.getAddToWarningCount())) {
            return false;
        }

        if (this.ipAddressReachedLimit(requestPerIpDetails,pathLimitationConfig.getVisibleCaptchaCount())) {
            Boolean captchaActive=true;
        }
        return true;
    }

    public synchronized boolean ipAddressReachedLimit(RequestPerIpDetails requestPerIpDetails,Integer maxRequestSizePerIp) {
        int amountRequests = requestPerIpDetails.getRequestsPerIp().size();
        return (amountRequests > maxRequestSizePerIp);
    }

}
