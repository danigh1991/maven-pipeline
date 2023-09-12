package com.core.common.security.auth.a2fa;

import com.core.common.security.auth.AnonAuthentication;
import com.core.common.security.auth.OtpBasedAuthentication;
import com.core.common.security.auth.PreAuthenticationChecker;
import com.core.common.security.auth.TokenBasedAuthentication;
import com.core.common.services.CommonService;
import com.core.common.services.SecureCodeGenerator;
import com.core.common.services.UserService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.AllowedIpPath;
import com.core.datamodel.model.dbmodel.User;
import com.core.datamodel.model.dbmodel.UserAttempts;
import com.core.datamodel.model.enums.ELoginType;
import com.core.exception.InvalidDataException;
import com.core.exception.NoAccessException;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

//@Component("customAuthenticationProvider")
public class CustomAuthenticationProvider/* implements AuthenticationProvider*/ extends DaoAuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SecureCodeGenerator secureCodeGenerator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PreAuthenticationChecker preAuthenticationChecker;


    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        //final User user = userService.getUserInfo(auth.getName());
        User user;
        try {
            user = userService.getUserByUserNameOrEmail(auth.getName());

            String ip=Utils.getClientFirstIp(request);
            if(user.getLimitIpList()!=null && user.getLimitIpList().size()>0 && !user.getLimitIpList().stream().anyMatch(limitIp ->  ip.startsWith(limitIp)))
                throw new  BadCredentialsException(Utils.getMessageResource("global.accessDeniedMessage2"));
        }catch (UsernameNotFoundException ex){
            user =null;
        }

        ELoginType eLoginType =(user!=null && user.getLoginType()!=null) ? ELoginType.valueOf(user.getLoginType()): ELoginType.valueOf(commonService.getLoginType());
        if ((user == null)) {
            if(eLoginType==ELoginType.ONLY_OTP && userService.getNewUserForCommitRegister(auth.getName())!=null) {
                    user=userService.commitRegisterUser(auth.getName(),Utils.parsInt(((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode()));
                    eLoginType =(user!=null && user.getLoginType()!=null) ? ELoginType.valueOf(user.getLoginType()): ELoginType.valueOf(commonService.getLoginType());
            }else {
                throw new BadCredentialsException(Utils.getMessageResource("global.wrongUserData"));
            }
        }
        try{
            //Utils.userNameValidate(auth.getName());
            // to verify verification code
            if (/*commonService.getForce2FAuthentication()*/ eLoginType==ELoginType.ONLY_OTP || eLoginType==ELoginType.OTP_PASSWORD || (eLoginType== ELoginType.ONLY_PASSWORD && user.getUsing2FA())) {
                final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
                if (Utils.isStringSafeEmpty(verificationCode) || !secureCodeGenerator.verifyCode(verificationCode.trim(),user.getSecret(),commonService.getVerificationCodeLiveTimeSecond())) {
                    throw new BadCredentialsException(Utils.getMessageResource("global.wrongVerificationCode"));
                }
            }

            Authentication result=new AnonAuthentication();
            if (eLoginType==ELoginType.OTP_PASSWORD || (eLoginType== ELoginType.ONLY_PASSWORD)) {
                result= super.authenticate(auth);
            }else if (eLoginType==ELoginType.ONLY_OTP){
                UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getName());
                if (userDetails == null) {
                    throw new BadCredentialsException(Utils.getMessageResource("global.wrongUserData"));
                }
                preAuthenticationChecker.check(userDetails);
                OtpBasedAuthentication otpBasedAuthentication = new OtpBasedAuthentication(userDetails);
                otpBasedAuthentication.setOtp(((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode());
                SecurityContextHolder.getContext().setAuthentication(otpBasedAuthentication);
                result=otpBasedAuthentication;
            }
            userService.resetFailAttempts(auth.getName());
            return result;
        } catch (BadCredentialsException e) {
            Integer lockLevelResult=userService.updateFailAttemptsAndLockResult(auth.getName());
            if (lockLevelResult>0) {
                logger.error("User Account " + auth.getName() +  " is locked" +(lockLevelResult==3? "!" : " for" + (lockLevelResult==2?commonService.getLockMinuteLevel2():commonService.getLockMinuteLevel1())+ " minute."));
                throw new LockedException(Utils.getMessageResource((lockLevelResult==3? "global.lockedAccount" : (lockLevelResult==2? "global.lockedAccountLevel2" :"global.lockedAccountLevel1")) ,(lockLevelResult==3? null : (lockLevelResult==2?commonService.getLockMinuteLevel2():commonService.getLockMinuteLevel1()))));
            }
            throw e;
            //throw new BadCredentialsException(e.getMessage());
        } catch (LockedException e){
            String error = "";
            UserAttempts userAttempts = userService.getUserAttempts(auth.getName());
            if(userAttempts!=null){
                error =Utils.getMessageResource("global.lockedAccount_at", Utils.getShamsiDateTime(userAttempts.getLastmodified()));
            }else{
                error = e.getMessage();
            }
            logger.error(error);
            throw new LockedException(error);
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
