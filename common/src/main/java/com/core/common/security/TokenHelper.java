package com.core.common.security;

import com.core.common.services.CommonService;
import com.core.common.services.impl.DeviceService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.UserDeviceMetadata;
import com.core.datamodel.model.securitymodel.UserPrincipalDetails;
import com.core.exception.InvalidDataException;
import com.core.model.enums.EEncodeType;
import com.core.util.BaseUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component("tokenHelper")
public class TokenHelper {
    //related https://github.com/jwtk/jjwt

    @Value("${app.MyBrand_EName}")
    private String APP_NAME;

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;

    @Value("${jwt.refresh_expires_in}")
    private int REFRESH_EXPIRES_IN;

    @Value("${jwt.header}")
    private String AUTH_HEADER;

    @Value("${jwt.cookie}")
    private String AUTH_COOKIE;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserDetailsService userDetailsService;

    //private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    @Autowired
    private CipherHelper cipherHelper;


    private RequestMatcher headerOnlyTokenRequestMatcher = new RequestMatcher() {

        // Enabled Token protection on the following urls:
        private AntPathRequestMatcher[] requestMatchers = {
                new AntPathRequestMatcher("/api/**"),
        };

        @Override
        public boolean matches(HttpServletRequest request) {
            for (AntPathRequestMatcher rm : requestMatchers) {
                if (rm.matches(request)) { return true; }
            }
            return false;
        }

    };


    public String getDataFromToken(String token,String secretKey) {
        return this.getUsernameFromToken(this.getClaimsFromToken(token,secretKey));
    }


    public String getUsernameFromToken(String token) {
          return this.getUsernameFromToken(this.getClaimsFromToken(token));
    }
    public String getUsernameFromToken(Claims claims ) {
        String username;
        try {
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    public String getIssuerFromToken(String token) {
        return this.getIssuerFromToken(this.getClaimsFromToken(token));
    }

    public String getIssuerFromToken(Claims claims ) {
        String issuer;
        try {
            issuer = claims.getIssuer();
        } catch (Exception e) {
            issuer = null;
        }
        return issuer;
    }

    public String getAudienceFromToken(String token) {
        return this.getAudienceFromToken(this.getClaimsFromToken(token));
    }

    public String getAudienceFromToken(Claims claims) {
        String audience;
        try {
            audience = claims.getAudience();
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    private SecretKey getJWTTokenKey(String key){
        return this.getJWTTokenKey(key,EEncodeType.BASE64);
    }
    private SecretKey getJWTTokenKey(String key, EEncodeType eEncodeType){
        if(eEncodeType==EEncodeType.BYTE_ARRAY)
            return Keys.hmacShaKeyFor(key.getBytes());
        else if(eEncodeType==EEncodeType.BYTE_ARRAY_UTF_8)
            return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        else if(eEncodeType==EEncodeType.BYTE_ARRAY_UTF_16)
            return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_16));
        else if(eEncodeType==EEncodeType.BASE64)
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        else if(eEncodeType==EEncodeType.BASE64URL)
            return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(key));
        else
            throw new InvalidDataException("InvalidData", "Invalid EncodeType");
    }

    public String generateToken(String username,Map<String,Object> additional) {
        return this.generateToken(username,null,null,additional,null,null);
    }

    public String generateToken(String username,Integer expirationSecond,Map<String,Object> additional) {
        return this.generateToken(username,null,expirationSecond,additional,null,null);
    }

    public String generateToken(String username,Date expirationDate) {
        return this.generateToken(username,expirationDate,null,null,null,null);
    }
    public String generateToken(String username,Date expirationDate,String secretKey) {
        return this.generateToken(username,expirationDate,null,null,secretKey,null);
    }
    public String generateToken(String username,String audience) {
        return this.generateToken(username,null,null,null,null,audience);
    }


    private String generateToken(String username,Date expirationDate,Integer expirationSecond,Map<String,Object> additional,String secretKey,String audience) {
        JwtBuilder jwtBuilder= Jwts.builder();
        if (additional!=null && additional.size()>0)
            jwtBuilder.addClaims(additional);
        jwtBuilder.setIssuer(commonService.getMySiteDomain());
        jwtBuilder.setSubject(username);
        jwtBuilder.setIssuedAt(this.generateCurrentDate());

        if(expirationDate!=null)
           jwtBuilder.setExpiration(expirationDate);
        else
           jwtBuilder.setExpiration(expirationSecond!=null?this.generateExpirationDate(expirationSecond):this.generateExpirationDate());

        if (!Utils.isStringSafeEmpty(audience))
           jwtBuilder.setAudience(audience);

        jwtBuilder.signWith(!Utils.isStringSafeEmpty(secretKey)?  this.getJWTTokenKey(secretKey):this.getJWTTokenKey(SECRET)/*, SIGNATURE_ALGORITHM */);
        return jwtBuilder.compact();
    }

    private String generateRefreshToken(UserDeviceMetadata userDeviceMetadata,String secretKey) {
        /*String refreshToken= BaseUtils.createUniqueRandom() ;
        Utils.enCrypt(refreshToken);*/
        return BaseUtils.createUniqueRandom();
    }


    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims =Jwts.parserBuilder()
                    .setSigningKey(this.getJWTTokenKey(SECRET))
                    .build()
                    .parseClaimsJws(token).getBody();

            /*claims = Jwts.parser()
                    .setSigningKey(this.SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();*/
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    public Claims getClaimsFromToken(String token,String secretKey) {
        Claims claims;
        try {
            claims =Jwts.parserBuilder()
                    .setSigningKey(!Utils.isStringSafeEmpty(secretKey)?this.getJWTTokenKey(secretKey):this.getJWTTokenKey(this.SECRET))
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


    String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(this.getJWTTokenKey(SECRET)/*, SIGNATURE_ALGORITHM  */)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        final Date expirationDate = getClaimsFromToken(token).getExpiration();
        return expirationDate.compareTo(generateCurrentDate()) > 0;
    }
    public Boolean isTokenBeExpired(String token) {
        return this.isTokenBeExpired(this.getClaimsFromToken(token));
    }

    public Boolean isTokenBeExpired(Claims claims) {
        Boolean result;
        try{
            final Date expirationDate = claims.getExpiration();
            result= expirationDate.compareTo(generateCurrentDate()) <= 0;
        } catch (Exception e) {
            result =true;
        }
        return result;
    }

    public String refreshToken(Claims claims/*String token*/) {
        String refreshedToken;
        try {
            //final Claims claims = getClaimsFromToken(token);
            if(claims.getAudience() != null && claims.getAudience().contains("resetPassword")) {
                String audience=claims.getAudience();
                String[] audiences=audience.split("_");
                if (audiences.length>1) {
                    Integer try_Number= Utils.parsInt(audiences[1]);
                    if (try_Number>4)
                        audience="";
                    else
                        audience = audiences[0] + "_" + (try_Number + 1);
                }
                else {
                    audience = audiences[0] + "_1" ;
                }
                claims.setAudience(audience);
            }
            claims.setIssuedAt(generateCurrentDate());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    private long getCurrentTimeMillis() {
        return DateTime.now().getMillis();
    }

    private Date generateCurrentDate() {
        return new Date(getCurrentTimeMillis());
    }

    private Date generateExpirationDate() {
        return new Date(getCurrentTimeMillis() + this.EXPIRES_IN * 1000);
    }
    private Date generateExpirationDate(Integer expirationSecond) {
        return new Date(getCurrentTimeMillis() + expirationSecond * 1000);
    }

    public String getToken( HttpServletRequest request ) {
        /**
         *  Getting the token from Cookie store
         */
        if(!headerOnlyTokenRequestMatcher.matches(request) || request.getRequestURI().startsWith("/api/order/getQRCode")) {
            Cookie authCookie = getCookieValueByName(request, AUTH_COOKIE);
            if (authCookie != null) {
                return authCookie.getValue();
            }
        }
        /**
         *  Getting the token from Authentication header
         *  e.g Bearer your_token
         */
        String authHeader = request.getHeader(AUTH_HEADER);
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Find a specific HTTP cookie in a request.
     *
     * @param request
     *            The HTTP request object.
     * @param name
     *            The cookie name to look for.
     * @return The cookie, or <code>null</code> if not found.
     */
    public Cookie getCookieValueByName(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (int i = 0; i < request.getCookies().length; i++) {
            if (request.getCookies()[i].getName().equals(name)) {
                return request.getCookies()[i];
            }
        }
        return null;
    }


    public UserTokenState generateTokenState(String username, HttpServletRequest request ){

        UserPrincipalDetails upd =(UserPrincipalDetails) userDetailsService.loadUserByUsername(username);
        if (upd == null)
            return null;

        Map<String,Object> additional=null;
        additional= new HashMap<>();
        UserDeviceMetadata userDeviceMetadata=deviceService.registerDevice(upd.getId(),this.generateExpirationDate(REFRESH_EXPIRES_IN),request);
        additional.put("clientIp",Utils.getClientFirstIp(request));
        additional.put("deviceMetadata",userDeviceMetadata.getDeviceDetail());
        additional.put("deviceMetadataId",userDeviceMetadata.getId().toString());

        String jws = this.generateToken(username,additional);
        if(commonService.getJwtTokenCryptStatus())
            jws=cipherHelper.cipherString(jws,commonService.getJwtTokenKey());

        String refreshJws = this.generateRefreshToken(userDeviceMetadata ,null);
        userDeviceMetadata=deviceService.setRefreshTokenForDevice(userDeviceMetadata.getId(),refreshJws);
        if(commonService.getJwtTokenCryptStatus())
            refreshJws=cipherHelper.cipherString(refreshJws,commonService.getJwtTokenKey());

        UserTokenState userTokenState = new UserTokenState(jws,refreshJws, EXPIRES_IN,upd.getId(),upd.getFirstName(), upd.getLastName(), upd.getUser().getLogoPath());
        return userTokenState;
    }

    public UserTokenState generateTokenStateFromData(String username, HttpServletRequest request , String refreshToken,String deviceMetadata,Long deviceMetadataId){
        UserPrincipalDetails upd =(UserPrincipalDetails) userDetailsService.loadUserByUsername(username);
        if (upd == null)
            return null;
        Map<String,Object> additional=null;
        additional= new HashMap<>();

        additional.put("clientIp",Utils.getClientFirstIp(request));
        additional.put("deviceMetadata",deviceMetadata);
        additional.put("deviceMetadataId",deviceMetadataId.toString());
        String jws = this.generateToken(username,additional);

        if(commonService.getJwtTokenCryptStatus())
            jws=cipherHelper.cipherString(jws,commonService.getJwtTokenKey());

        UserTokenState userTokenState = new UserTokenState(jws,refreshToken, EXPIRES_IN,upd.getId(),upd.getFirstName(), upd.getLastName(), upd.getUser().getLogoPath());
        return userTokenState;
    }


}
