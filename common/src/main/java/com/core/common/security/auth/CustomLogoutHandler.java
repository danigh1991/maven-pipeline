package com.core.common.security.auth;

import com.core.common.security.CipherHelper;
import com.core.common.security.TokenHelper;
import com.core.common.services.CommonService;
import com.core.common.services.impl.DeviceService;
import com.core.common.util.Utils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@Component("CustomLogoutHandler")
public class CustomLogoutHandler implements LogoutHandler {


    private TokenHelper tokenHelper;
    private DeviceService deviceService;
    private CommonService commonService;
    private CipherHelper cipherHelper;

    @Autowired
    public CustomLogoutHandler(TokenHelper tokenHelper,DeviceService deviceService,CommonService commonService,CipherHelper cipherHelper) {
        this.tokenHelper = tokenHelper;
        this.deviceService = deviceService;
        this.commonService = commonService;
        this.cipherHelper = cipherHelper;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(Utils.isAuthUser()) {
            String authToken = tokenHelper.getToken(request);
            if(commonService.getJwtTokenCryptStatus()) {
                try {
                    authToken = cipherHelper.decipherString(authToken, commonService.getJwtTokenKey());
                }catch (Exception e){
                    SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
                    return;
                }
            }
            Claims claims=tokenHelper.getClaimsFromToken(authToken);
            Long deviceMetadataId=Utils.parsLong(Utils.getAsStringFromMap(claims, "deviceMetadataId", false, "0"));
            if (deviceMetadataId>0)
                deviceService.removeDeviceById(deviceMetadataId);
            //deviceService.removeDeviceByUserId(Utils.getCurrentUserId(), request);
        }
        //System.out.println("logout ");
    }
}
