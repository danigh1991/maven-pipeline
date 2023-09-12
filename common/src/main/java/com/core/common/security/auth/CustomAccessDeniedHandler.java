package com.core.common.security.auth;

/*
import com.core.common.util.Utils;
import com.core.exception.response.RestExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("CustomAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static Logger logger = LogManager.getLogger(CustomAccessDeniedHandler.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        RestExceptionResponse exResponse = new RestExceptionResponse();
        exResponse.setErrorCode("403");
        exResponse.setErrorMessage(e.getMessage());

        String ip= Utils.getClientIp(httpServletRequest);
        String originalUri = httpServletRequest.getRequestURI();
        logger.error((!Utils.isStringSafeEmpty(originalUri) ? ">>>>originalUri :" +originalUri + " ," : "" ) +"Error Code: 403 , Ip : " + ip  + "  ," +e.getMessage());

        */
/*httpServletResponse.sendError(403);
        httpServletResponse.addHeader("retUrl", httpServletRequest.getRequestURI());*//*


        String jwtResponse = objectMapper.writeValueAsString(exResponse);
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.getWriter().write( jwtResponse );

    }
}
*/
