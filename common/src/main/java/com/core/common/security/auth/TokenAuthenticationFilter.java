package com.core.common.security.auth;

import com.core.common.security.AuthenticationHelper;
import com.core.common.security.TokenHelper;
import com.core.common.security.limit.UserApiCallTimeWindowManager;
import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.exception.InvalidDataException;
import com.core.exception.MyException;
import com.core.exception.TooManyRequestsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private UserApiCallTimeWindowManager userApiCallTimeWindowManager;

    @Autowired
    private CommonService commonService;

    RequestMatcher tokenRequestMatcher = new RequestMatcher() {

        // Enabled Token protection on the following urls:
        private AntPathRequestMatcher[] requestMatchers = {
                // new AntPathRequestMatcher("/**")
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/panel/**"),
                new AntPathRequestMatcher("/nimdalenap/**"),
                new AntPathRequestMatcher("/panelshare/**"),
                new AntPathRequestMatcher("/resources/**"),
                new AntPathRequestMatcher("/img/**"),
                new AntPathRequestMatcher("/favicon.ico"),
                new AntPathRequestMatcher("/font/**")

        };

        @Override
        public boolean matches(HttpServletRequest request) {
            // If the request match one url the CSFR protection will be enabled
            for (AntPathRequestMatcher rm : requestMatchers) {
                if (rm.matches(request)) { return true; }
            }
            return false;
        } // method matches

    };

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!tokenRequestMatcher.matches(request)) {
            String authToken = tokenHelper.getToken(request);

            authenticationHelper.setAuthenticationByToken(request,response, authToken);
            if(Utils.isAuthUser() && commonService.getApiCallDurationLimit()>0 && commonService.getApiCallCountLimit()>0
                && !userApiCallTimeWindowManager.checkAndAddUserRequest(request,  response,Utils.getCurrentUserId())) {
                String ipAddress = Utils.getClientIp(request);
                Utils.sendError(request,response,429,Utils.getMessageResource("global.tooManyRequest",ipAddress));
                return;
            }
            switch (Utils.checkForceChangeCredential(request)) {
                case "api":
                    response.sendRedirect("/api/users/forceToChangePassword");
                case "page":
                    Utils.redirect302(request, response, "/userpanel#!/changepassword/0");
            }
        }else {
            SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
        }
        chain.doFilter(request, response);
    }

}
