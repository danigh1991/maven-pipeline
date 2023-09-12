package com.core.common.security.limit;

import com.core.common.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class IpLimitFilter extends OncePerRequestFilter/*implements Filter*/ {

    private static String[] LIMITED_PATHS = new String[]{/*"/discount","/brand","/store","/branch","/discount-cat","/blog","/shop","/product","/order"*/};

    Logger logger = LoggerFactory.getLogger(IpLimitFilter.class);

    @Autowired
    private IpTimeWindowManager ipTimeWindowManager;


    public IpLimitFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!ipTimeWindowManager.checkAndAddIpRequest(request,response)) {
            String ipAddress = Utils.getClientIp(request);
            Utils.sendError(request,response,429,Utils.getMessageResource("common.ipFilter.message",ipAddress));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isRestPublicUserServicePostCall(HttpServletRequest request) {
        String requestedUri = request.getRequestURI();
        return Arrays.stream(LIMITED_PATHS).anyMatch(limitedPath -> requestedUri.startsWith(limitedPath));
    }

    /*
    public IpLimitFilter() {
       // this.ipTimeWindowManager = ipTimeWindowManager;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = getHttpServletRequest(servletRequest);
        HttpServletResponse response=getHttpServletResponse(servletResponse);
        if(isRestPublicUserServicePostCall(request) && ! ipTimeWindowManager.addIpRequestAndCheck(request,response)) {
            String ipAddress = Utils.getClientIp(request);
            String message = "The ip address: " + ipAddress + " made more than " + IpTimeWindowManager.MAX_REQUEST_PER_IP_IN_WINDOW + " requests in " + IpTimeWindowManager.WINDOW_SIZE_IN_MINUTES + " minutes. It's suspicious.";
            response.sendError(429);
            response.addHeader("retUrl", request.getRequestURI());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private HttpServletRequest getHttpServletRequest(ServletRequest servletRequest) throws ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            return (HttpServletRequest) servletRequest;
        } else {
            Request springRequest = (Request) servletRequest.getAttribute("org.springframework.web.context.request.RequestContextListener.REQUEST_ATTRIBUTES");
            if (springRequest instanceof HttpServletRequest) {
                return springRequest;
            } else {
                throw new ServletException("At least the inner request should be a HttpServletRequest");
            }
        }
    }

    private HttpServletResponse getHttpServletResponse(ServletResponse servletResponse) throws ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            return (HttpServletResponse) servletResponse;
        } else {
                throw new ServletException("At least the inner request should be a HttpServletResponse");
        }
    }

    @Override
    public void destroy() {
    }*/

}
