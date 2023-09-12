package com.core.common.security;

import com.core.common.util.Utils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {

   /* @Value("${jwt.expires_in}")
    private int EXPIRES_IN;*/

    protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
    protected static final String RESPONSE_HEADER_NAME = "X-XSRF-HEADER";
    protected static final String RESPONSE_PARAM_NAME = "X-XSRF-PARAM";
    protected static final String RESPONSE_TOKEN_NAME = "X-XSRF-TOKEN";
    RequestMatcher csrfRequestMatcher = new RequestMatcher() {

        // Enabled CSFR protection on the following urls:
        private AntPathRequestMatcher[] requestMatchers = {
               /* new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/panel/**"),
                new AntPathRequestMatcher("/resources/**"),*/
                new AntPathRequestMatcher("/img/**")
        };

        @Override
        public boolean matches(HttpServletRequest request) {
            // If the request match one url the CSFR protection will be enabled
            for (AntPathRequestMatcher rm : requestMatchers) {
                if (rm.matches(request)) {
                    return false;
                }
            }
            return true;
        } // method matches

    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, javax.servlet.FilterChain filterChain) throws ServletException, IOException {

        // if (csrfRequestMatcher.matches(request)) {
        response=Utils.setXSRFToken(response,request);

        //}
        filterChain.doFilter(request, response);
    }
}
