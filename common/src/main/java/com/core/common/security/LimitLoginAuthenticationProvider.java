package com.core.common.security;


import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.exception.ResourceNotFoundException;
import com.core.datamodel.model.dbmodel.UserAttempts;
import com.core.common.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("authenticationProvider")
public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;


    @Autowired
    @Qualifier("appUserDetailsService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {

            Authentication auth = super.authenticate(authentication);

            //if reach here, means login success, else exception will be thrown
            //reset the user_attempts
            userService.resetFailAttempts(authentication.getName());

            return auth;

        } catch (BadCredentialsException e) {
            //invalid login, update to user_attempts
            Integer lockLevelResult=userService.updateFailAttemptsAndLockResult(authentication.getName());
            if (lockLevelResult>0) {
                logger.error("User Account " + authentication.getName() +  " is locked" +(lockLevelResult==3? "!" : " for" + (lockLevelResult==2?commonService.getLockMinuteLevel2():commonService.getLockMinuteLevel1())+ " minute."));
                throw new LockedException(Utils.getMessageResource((lockLevelResult==3? "global.lockedAccount" : (lockLevelResult==2? "global.lockedAccountLevel2" :"global.lockedAccountLevel1")) ,(lockLevelResult==3? null : (lockLevelResult==2?commonService.getLockMinuteLevel2():commonService.getLockMinuteLevel1()))));
            }
            throw e;
        } catch (LockedException e){
            //this user is locked!
            String error = "";
            UserAttempts userAttempts = userService.getUserAttempts(authentication.getName());
            if(userAttempts!=null){
                Date lastAttempts = userAttempts.getLastmodified();
                error = "User account is locked! =>  Username : " + authentication.getName() + " -> Last Attempts : " + lastAttempts;
            }else{
                error = e.getMessage();
            }

            throw new LockedException(error);
        } /*catch (ResourceNotFoundException e){
            throw e;
        }*/

    }

}