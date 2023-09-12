package com.core.common.security.auth;

import com.core.common.services.UserService;
import com.core.common.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component("LogoutSuccess")
public class LogoutSuccess implements LogoutSuccessHandler {

    @Autowired
    ObjectMapper objectMapper;
    @Value("${jwt.cookie}")
    private String TOKEN_COOKIE;

    @Autowired
    private UserService userService;

    private static Logger logger = LogManager.getLogger(LogoutSuccess.class);
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        Map<String, String> result = new HashMap<>();
        result.put( "result", "success" );

      //todo remove user device metadata

     //   response.addHeader("Access-Control-Allow-Origin","*");
     //  response.addHeader("Access-Control-Allow-Credentials", "true");
     //   response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, HEAD");
        response.setContentType("application/json");
		response.getWriter().write( objectMapper.writeValueAsString( result ) );

        // delete Token Cookie in response
        Cookie authCookie = Utils.createCookie(TOKEN_COOKIE,null, "/" , 0 ,false);
        response.addCookie( authCookie );

     //   response.setStatus(HttpServletResponse.SC_OK);
        logger.info(LogoutSuccess.class.getSimpleName() + " : Logout Successfull" );

        // user Id not access
       // userService.createUserLogQueue(EUserActions.LOGOUT,Utils.getClientIp(httpServletRequest), Utils.getUserAgent(httpServletRequest), "");

    }


}