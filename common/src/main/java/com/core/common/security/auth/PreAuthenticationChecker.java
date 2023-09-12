package com.core.common.security.auth;

import javax.servlet.http.HttpServletRequest;

import com.core.common.services.UserService;
import com.core.common.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
public class PreAuthenticationChecker implements UserDetailsChecker {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void check(UserDetails userDetails) {
        if (!userDetails.isAccountNonLocked()) {
            logger.debug("Failed to authenticate since user account is locked");
            throw new LockedException(Utils.getMessageResource("global.lockedAccount"));
        } else if (!userDetails.isEnabled()) {
            logger.debug("Failed to authenticate since user account is disabled");
            throw new DisabledException(Utils.getMessageResource("global.disableUser"));
        } else if (!userDetails.isAccountNonExpired()) {
            logger.debug("Failed to authenticate since user account has expired");
            throw new AccountExpiredException(Utils.getMessageResource("global.expiredUser"));
        }

    }


}
