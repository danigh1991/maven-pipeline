package com.core.common.security.auth;


import com.core.datamodel.model.enums.EUserActions;
import com.core.datamodel.model.securitymodel.UserPrincipalDetails;
import com.core.common.security.TokenHelper;
import com.core.common.security.UserTokenState;
import com.core.common.services.UserService;
import com.core.common.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("AuthenticationSuccessHandler")
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;

    @Value("${jwt.refresh_expires_in}")
	private int REFRESH_EXPIRES_IN;

    @Value("${jwt.cookie}")
    private String TOKEN_COOKIE;

	@Autowired
	private TokenHelper tokenHelper;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private  UserService userService;



	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication ) throws IOException, ServletException {

		clearAuthenticationAttributes(request);



		UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails)authentication.getPrincipal();


		UserTokenState userTokenState = tokenHelper.generateTokenState( userPrincipalDetails.getUsername(),request);

		//todo remove after migrate to header cookie
		// Create token auth Cookie and  Add cookie to response
		String jwtResponse ="";
		if(userTokenState!=null) {
			Cookie authCookie = Utils.createCookie(TOKEN_COOKIE, userTokenState.getAccess_token(), "/", EXPIRES_IN, true);
			response.addCookie(authCookie);
			jwtResponse = objectMapper.writeValueAsString(userTokenState);
		}

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write( jwtResponse );
		userService.createUserLogQueue(EUserActions.LOGIN,Utils.getClientIp(request), Utils.getUserAgent(request), "");
	}
}
