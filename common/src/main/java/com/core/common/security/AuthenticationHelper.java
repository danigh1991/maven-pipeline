package com.core.common.security;

import com.core.common.model.contextmodel.TokenDto;
import com.core.common.security.auth.PreAuthenticationChecker;
import com.core.common.security.auth.a2fa.CustomWebAuthenticationDetails;
import com.core.common.services.CommonService;
import com.core.common.services.UserService;
import com.core.common.services.impl.DeviceService;
import com.core.datamodel.model.dbmodel.User;
import com.core.datamodel.model.dbmodel.UserDeviceMetadata;
import com.core.datamodel.model.securitymodel.UserPrincipalDetails;
import com.core.common.security.auth.AnonAuthentication;
import com.core.common.security.auth.TokenBasedAuthentication;
import com.core.common.util.Utils;
import com.core.exception.InvalidDataException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("AuthenticationHelper")
public class AuthenticationHelper {
    @Autowired
    TokenHelper tokenHelper;
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;


    @Autowired
    private CommonService commonService;

    @Autowired
    private PreAuthenticationChecker preAuthenticationChecker;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private CipherHelper cipherHelper;


    @Value("${jwt.cookie}")
    private String TOKEN_COOKIE;
    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;



   public void setAuthenticationByToken(HttpServletRequest request,HttpServletResponse response,String token){
       if (Utils.isStringSafeEmpty(token)) {
            SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
            return;
        }
        String decipherToken=token;
        if(commonService.getJwtTokenCryptStatus()) {
            try {
               decipherToken = cipherHelper.decipherString(token, commonService.getJwtTokenKey());
            }catch (Exception e){
                SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
                Utils.removeCookie(response,TOKEN_COOKIE);
                return;
            }
        }

        // get username from token
        Claims claims=tokenHelper.getClaimsFromToken(decipherToken);

        try {
            UserDetails userDetails=this.validateAndGetUserDetailsFromClaims(claims,request,commonService.tokenIpCheck());
            if(userDetails==null){
                SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
                Utils.removeCookie(response,TOKEN_COOKIE);
                return;
            }

            String ip=Utils.getClientFirstIp(request);

            if(((UserPrincipalDetails)userDetails).getUser().getLimitIpList()!=null && ((UserPrincipalDetails)userDetails).getUser().getLimitIpList().size()>0
                    && !((UserPrincipalDetails)userDetails).getUser().getLimitIpList().stream().anyMatch(limitIp ->  ip.startsWith(limitIp))) {
                SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
                Utils.removeCookie(response,TOKEN_COOKIE);
                return;
            }

            try {
                preAuthenticationChecker.check(userDetails);
            }catch (AccountStatusException ex){
                SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
                Utils.removeCookie(response,TOKEN_COOKIE);
                return;
            }



            String audience= tokenHelper.getAudienceFromToken(claims);

            //Referesh Expier Time Token
            if (!tokenHelper.isTokenBeExpired(claims)) {
                Cookie authCookie = Utils.createCookie(TOKEN_COOKIE, token, "/" , EXPIRES_IN ,true);
                response.addCookie( authCookie );
            }

            TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
            authentication.setToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // erase Password For reset password
            if (audience!=null && audience.contains("resetPassword"))
                ((UserPrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().setPassword("");
               //authentication.eraseCredentials();
        }catch (UsernameNotFoundException e){
            //System.out.println("****Error******");
            e.printStackTrace();
            Utils.removeCookie(response,TOKEN_COOKIE);
            SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
        }
    }



    public UserTokenState authenticationByUserNameAndPassword(HttpServletRequest request, HttpServletResponse response, String username, String password) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        authToken.setDetails(new CustomWebAuthenticationDetails(request));
        Authentication authentication =authenticationManagerBuilder.getOrBuild().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserTokenState userTokenState = tokenHelper.generateTokenState( username,request);

        //todo remove after migrate to header cookie
        // Create token auth Cookie and Add cookie to response
        if(userTokenState!=null) {
            Cookie authCookie = Utils.createCookie(TOKEN_COOKIE, userTokenState.getAccess_token(), "/", userTokenState.getExpires_in().intValue(), true);
            response.addCookie(authCookie);
        }
        // JWT is also in the response
        return userTokenState;
    }

    private UserDetails validateAndGetUserDetailsFromClaims(Claims claims,HttpServletRequest request,Boolean ipCheck){
        if(claims==null)
           return null;
        String username = tokenHelper.getUsernameFromToken(claims);
        if (username == null || tokenHelper.isTokenBeExpired(claims) ||  !commonService.getMySiteDomain().equalsIgnoreCase(tokenHelper.getIssuerFromToken(claims)))
            return null;

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null)
            return null;

        String deviceMetadata=Utils.getAsStringFromMap(claims,"deviceMetadata",false,"");
        String clientIp=Utils.getAsStringFromMap(claims,"clientIp",false,"");
        //Long deviceMetadataId=Utils.getAsLongFromMap(claims,"deviceMetadataId",false,0l);
        Long deviceMetadataId=Utils.parsLong(Utils.getAsStringFromMap(claims, "deviceMetadataId", false, "0"));

        if(!deviceService.verifyDevice(((UserPrincipalDetails)userDetails).getId(),request,deviceMetadata, deviceMetadataId,clientIp,ipCheck))
            return null;

        return  userDetails;
    }

    @Deprecated
    public UserTokenState refreshToken_old(HttpServletRequest request, HttpServletResponse response, TokenDto refreshToken) {
        if (Utils.isStringSafeEmpty(refreshToken.getData())) {
            throw new BadCredentialsException(Utils.getMessageResource("global.login_required"));
        }

        String rfToken=refreshToken.getData();
        if(commonService.getJwtTokenCryptStatus()) {
           try {
               rfToken = cipherHelper.decipherString(rfToken, commonService.getJwtTokenKey());
           }catch (Exception e){
               throw new BadCredentialsException(Utils.getMessageResource("global.login_required"));
           }

        }

        Claims claims=tokenHelper.getClaimsFromToken(rfToken);
        String deviceMetadata=Utils.getAsStringFromMap(claims,"deviceMetadata",false,"");

        Long deviceMetadataId= Utils.parsLong(Utils.getAsStringFromMap(claims, "deviceMetadataId", false, "0"));

        UserDetails userDetails=this.validateAndGetUserDetailsFromClaims(claims,request,commonService.refreshTokenIpCheck());
        if(userDetails==null){
            if (deviceMetadataId>0)
                deviceService.removeDeviceById(deviceMetadataId);
            throw new BadCredentialsException(Utils.getMessageResource("global.login_required"));
        }

        if(!userDetails.isAccountNonLocked() || !userDetails.isEnabled() || !userDetails.isAccountNonExpired()) {
            if (deviceMetadataId>0)
                deviceService.removeDeviceById(deviceMetadataId);
            throw new InvalidDataException("loginDisabled", "global.loginDisabled");
        }

        UserTokenState userTokenState = tokenHelper.generateTokenStateFromData(userDetails.getUsername(),request, refreshToken.getData(),deviceMetadata,deviceMetadataId);
        if(userTokenState!=null) {
            Cookie authCookie = Utils.createCookie(TOKEN_COOKIE, userTokenState.getAccess_token(), "/", userTokenState.getExpires_in().intValue(), true);
            response.addCookie(authCookie);
        }

        return userTokenState;
    }



    public UserTokenState refreshToken(HttpServletRequest request, HttpServletResponse response, TokenDto refreshToken) {
        if (Utils.isStringSafeEmpty(refreshToken.getData())) {
            throw new BadCredentialsException(Utils.getMessageResource("global.login_required"));
        }

        String rfToken=refreshToken.getData();
        if(commonService.getJwtTokenCryptStatus()) {
            try {
                rfToken = cipherHelper.decipherString(rfToken, commonService.getJwtTokenKey());
            }catch (Exception e){
                throw new BadCredentialsException(Utils.getMessageResource("global.login_required"));
            }

        }

        UserDeviceMetadata userDeviceMetadata=null;
        User user=null;
        UserDetails userDetails=null;
        try {
            userDeviceMetadata=deviceService.findDeviceInfo(rfToken);
            deviceService.removeIdleAndExpireClient(userDeviceMetadata.getUserId());

            userDeviceMetadata=deviceService.findDeviceInfo(rfToken);
            user = userService.getUserInfo(userDeviceMetadata.getUserId());

            userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            if(userDetails==null)
                throw new InvalidDataException(user.getUsername(),"common.user.username_invalidHint");
            if(!userDetails.isAccountNonLocked() || !userDetails.isEnabled() || !userDetails.isAccountNonExpired())
                throw new InvalidDataException("loginDisabled", "global.loginDisabled");

            String requestIp = Utils.getClientFirstIp(request);
            String requestDeviceMetadata = deviceService.getDeviceDetails(request.getHeader("user-agent"));
            if((commonService.refreshTokenIpCheck() && !requestIp.equalsIgnoreCase(userDeviceMetadata.getIp())) || !requestDeviceMetadata.equalsIgnoreCase(userDeviceMetadata.getDeviceDetail()))
                throw new InvalidDataException("Invalid Request Ip", "Invalid Request Id");


            UserTokenState userTokenState = tokenHelper.generateTokenStateFromData(userDetails.getUsername(),request, refreshToken.getData(),userDeviceMetadata.getDeviceDetail(),userDeviceMetadata.getId());
            if(userTokenState!=null) {
                Cookie authCookie = Utils.createCookie(TOKEN_COOKIE, userTokenState.getAccess_token(), "/", userTokenState.getExpires_in().intValue(), true);
                response.addCookie(authCookie);
            }
            return userTokenState;

        }catch (Exception e){
            if (userDeviceMetadata!=null && userDeviceMetadata.getId()>0)
                deviceService.removeDeviceById(userDeviceMetadata.getId());
            throw new BadCredentialsException(Utils.getMessageResource("global.login_required"));
        }

    }



}
