package com.core.common.security.auth;

import com.core.common.util.Utils;
import com.core.exception.ResourceNotAuthorizedException;
import com.core.exception.response.RestExceptionResponse;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component("AuthenticationFailureHandler")
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String message="";
        if(exception instanceof  LockedException)
            message=exception.getMessage();
        else
            message=BaseUtils.getMessageResource("global.wrongUserData");

        String ip= Utils.getClientIp(request);
        String originalUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        logger.error((!Utils.isStringSafeEmpty(originalUri) ? "originalUri :" +originalUri + " ," : "" ) +"Error Code: UNAUTHORIZED , Ip : " + ip  + ", "+ exception.getMessage());

        Utils.sendError(request,response,401,message);
    }
}