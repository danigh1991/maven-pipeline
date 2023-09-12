package com.core.common.util;

import com.core.common.security.TokenHelper;
import com.core.common.services.CommonService;
import com.core.context.MultiLingualDataContextHolder;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.util.ShareUtils;
import com.core.datamodel.model.enums.ETargetTypes;
import com.core.datamodel.model.securitymodel.RequestPerIpDetails;
import com.core.exception.*;
import com.core.datamodel.model.dbmodel.*;
import com.core.datamodel.model.enums.EActionSource;
import com.core.exception.response.RestExceptionResponse;
import com.core.model.wrapper.TypeWrapper;
import com.core.responsemodel.ResponseMessage;
import com.core.responsemodel.ResponseSiteMap;
import com.core.responsemodel.ResponseSiteMapIndex;
import com.core.common.services.UserService;
import com.google.gson.JsonIOException;
import com.google.schemaorg.JsonLdSerializer;
import com.google.schemaorg.JsonLdSyntaxException;
import com.google.schemaorg.core.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.mobile.device.Device;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.ui.ModelMap;
import org.springframework.web.util.WebUtils;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Boolean;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Utils extends ShareUtils {

    private static CommonService commonService;
    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("(<img.*?/>)");
    private static Pattern FIG_CAPTION_TAG_PATTERN = Pattern.compile("<figcaption.*?>.*?</figcaption>");
    private static Pattern SPACE_PATTERN = Pattern.compile("(\\S+){2}");

    private static UserService userService ;//= new UserServiceImpl();

    private static TokenHelper tokenHelper;
   // private static SeoService seoService= new SeoServiceImpl();


    public static void setCommonService(CommonService commonService) {
        Utils.commonService = commonService;
    }

    public static void setUserService(UserService userService ) {
        Utils.userService=userService;
    }

    public static void setTokenHelper(TokenHelper tokenHelper) {
        Utils.tokenHelper = tokenHelper;
    }

    public static String getFavIconUrl() {
        //favIconUrl = isStringSafeEmpty(favIconUrl) ? getFileResourceDomain() + "/favicon.ico" : favIconUrl;
        return commonService.getFavIconUrl();
    }

    public static Long getAffiliateState() {
        return commonService.getAffiliateState();
    }

    public static String getSiteName() {
        return commonService.getMyBrandName();
    }

    public static String getSiteEName() {
        return commonService.getMyBrandEName();
    }


    public static String getShopLogoUrl() {
        // "/assets/image/" + getThemeName() + "/logo.png" : shopLogoUrl;
        return commonService.getLogoUrl();
    }

    public static String getResourceVersion() {
        return "?v=" + commonService.getResourceVersion();
    }


    public static final boolean findInList(String[] list, String param) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equalsIgnoreCase(param))
                return true;
        }
        return false;
    }

    public static long getIdFromSlug(String Slug_Url) {
        String[] slugArray = Slug_Url.split("-");
        String categoryId = slugArray[slugArray.length - 1];
        long ret = 0;
        try {
            ret = Long.parseLong(categoryId);
        } catch (Exception ex) {

        }
        return ret;
    }



    public static boolean equalsStrings(final String s1, final String s2) {
        if (s1==null && s2==null) return true;
        if(isStringSafeEmpty(s1) && !isStringSafeEmpty(s2)) return false;
        if(!isStringSafeEmpty(s1) && isStringSafeEmpty(s2)) return false;

        return s1.equals(s2);
    }

    public static boolean isSafeEmpty(final Double s) {
        return s == null || s == 0;
    }

    public static boolean isSafeEmpty(final Long s) {
        return s == null || s == 0;
    }

    public static boolean validatePercentage(final Double s) {
        return s > 0 && s < 100;
    }

    public static boolean stringCompare(final String s1, final String s2) {
        if (s1 == null && s2 == null)
            return true;
        else if (s1 == null || s2 == null)
            return false;
        else
            return s1.replace(" ", "").equalsIgnoreCase(s2.replace(" ", ""));
    }

    private static String imageServiceUrl;

    private static String fileUploadRoot;
    public static String getfileUploadRoot() {
        fileUploadRoot = isStringSafeEmpty(fileUploadRoot) ? getAppPropery("app.file_upload_root", "") : fileUploadRoot;
        return fileUploadRoot;
    }

    public static InputStream getImageAsStream(String path) throws IOException {
        String imagePath = getfileUploadRoot() + "/" + path;
        FileSystemResource imgFile1 = null;
        if (Files.exists(Paths.get(imagePath))) {
            imgFile1 = new FileSystemResource(imagePath);
            return imgFile1.getInputStream();
        }
        return null;
    }

    public static String getUserImageUrl(Long userId) {
        imageServiceUrl = isStringSafeEmpty(imageServiceUrl) ? getAppPropery("app.img_service_url", getFileResourceDomain() + "/img") : imageServiceUrl;
        String retUrl = imageServiceUrl + "/u/" + userId;
        return retUrl;
    }

    public static EActionSource getActionSource(HttpServletRequest request, String section) {
        EActionSource ActionSource = null;
        String referer = "discount";
        if (request.getHeader(HttpHeaders.REFERER) != null)
            referer = request.getHeader(HttpHeaders.REFERER).toString();
        String requestURI = request.getRequestURI();

        if (referer.contains("tjoor") || referer.contains("localhost")) {
            if (section != null) {
                switch (section) {
                    case "det_related": {
                        ActionSource = EActionSource.DETAILS_RELATED_DISCOUNTS;
                    }
                    case "det_maxdiscounts": {
                        ActionSource = EActionSource.DETAILS_MAX_DISCOUNTS;
                    }
                    case "det_mostvisitdiscount": {
                        ActionSource = EActionSource.DETAILS_MOSTVISITED_DISCOUNTS;
                    }
                    case "det_specialdiscounts": {
                        ActionSource = EActionSource.DETAILS_SPECIAL_DISCOUNTS;
                    }
                    case "det_newdiscounts": {
                        ActionSource = EActionSource.DETAILS_NEW_DISCOUNTS;
                    }
                    case "det_populardiscount": {
                        ActionSource = EActionSource.DETAILS_POPULAR_DISCOUNTS;
                    }
                    case "home_maxdiscounts": {
                        ActionSource = EActionSource.HOME_MAX_DISCOUNTS;
                    }
                    case "home_newdiscounts": {
                        ActionSource = EActionSource.HOME_NEW_DISCOUNTS;
                    }
                    case "home_populardiscounts": {
                        ActionSource = EActionSource.HOME_POPULAR_DISCOUNTS;
                    }
                    case "home_mostvisitdiscounts": {
                        ActionSource = EActionSource.HOME_MOST_VISITED_DISCOUNTS;
                    }
                    case "home_lasttimediscounts": {
                        ActionSource = EActionSource.HOME_LAST_TIME_DISCOUNTS;
                    }
                    case "panel_followdiscounts": {
                        ActionSource = EActionSource.PANEL_FOLLOW_DISCOUNTS;
                    }
                    case "panel_likediscounts": {
                        ActionSource = EActionSource.PANEL_LIKE_DISCOUNTS;
                    }
                    case "storebranchdetails_discountlist": {
                        ActionSource = EActionSource.STORE_BRANCH_DETAIL;
                    }
                    case "storedetails_discountlist": {
                        ActionSource = EActionSource.STORE_DETAILS;
                    }
                    case "telegram": {
                        ActionSource = EActionSource.TELEGRAM;
                    }

                    default:
                        ActionSource = EActionSource.DETAILS_DIRECT;

                }

            } else if (requestURI.contains("discount")) {
                ActionSource = EActionSource.DETAILS_DIRECT;
            } else if (requestURI.contains("product")) {
                ActionSource = EActionSource.PRODUCT_DETAILS_DIRECT;
            }else{
                ActionSource = EActionSource.OTHER_EXTERNAL_SITE;
            }

        } else {
            ActionSource = EActionSource.OTHER_EXTERNAL_SITE;
        }
        return ActionSource;
    }

    public static String getAsciiEncodeUrl(String mUrl) {
        URI uri = null;
        try {
            uri = new URI(mUrl);
        } catch (URISyntaxException e) {
        }
        String url = uri != null ? uri.toASCIIString() : mUrl;
        return url;
    }

    public static String redirect301(HttpServletResponse response, String newUrl) {

        String url = getAsciiEncodeUrl(newUrl);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", url);
        response.setHeader("Connection", "close");
        return "layout.basic";
        //return "redirect:" + url;
    }
    public static String redirect301(HttpServletRequest request,HttpServletResponse response, String newUrl) {
        String queryString=request.getQueryString();
        queryString=Utils.isStringSafeEmpty(queryString)?"":"?"+queryString;
        String url = getAsciiEncodeUrl(newUrl+queryString);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", url);
        response.setHeader("Connection", "close");
        return "layout.basic";
        //return "redirect:" + url;
    }


    public static String redirect302(HttpServletRequest request,HttpServletResponse response, String newUrl) {
        String queryString=request.getQueryString();
        queryString=Utils.isStringSafeEmpty(queryString)?"":"?"+queryString;

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", newUrl+queryString);
        response.setHeader("Connection", "close");
        return "layout.basic";
        //return "redirect:" + newUrl;
    }

    public static String urlDispatcher(HttpServletRequest request,HttpServletResponse response, String newUrl) throws ServletException, IOException {
     //   System.out.println("dispatcher.forward>>>>>>>>>>>>>>>>>>>>>>> " + newUrl);
        RequestDispatcher dispatcher = request.getRequestDispatcher(newUrl);
        dispatcher.forward(request, response);
        return "layout.basic";
        //return "redirect:" + newUrl;
    }


    public  static Map<String,Object> addDeviceToParams(Device device, Map<String,Object> requestParams){
        if(requestParams == null)
            requestParams = new HashMap<>();
        String deviceName = getClientDevice(device);
        requestParams.put("device",deviceName);
        requestParams.put("isMobileDevice", isMobileDevice(deviceName));
        requestParams.put("isLeftToRight", commonService.getCurrentLanguageInfo().getLeftToRight());
        requestParams.put("currentLang", MultiLingualDataContextHolder.getMultiLingualData().getCurrentLanguageShortName());
        requestParams.put("defaultLang", MultiLingualDataContextHolder.getMultiLingualData().getDefaultLanguageShortName());
        requestParams.put("isDefaultLang", MultiLingualDataContextHolder.getMultiLingualData().currentLanguageEqualToBaseLang());
        requestParams.put("calendarType", commonService.getCalendarType());
        requestParams.put("showAddToBasket", "true");
        requestParams.put("showFastView", "true");
        requestParams.put("showShippingIcons", "true");
        return requestParams;
    }

    public  static Map<String,Object> addDeviceParamToModel(Map<String,Object> requestParams, Map<String,Object> model){
        if(model == null)
            model = new ModelMap();
        if(requestParams != null) {
            model.put("device", requestParams.get("device"));
            model.put("isMobileDevice", requestParams.get("isMobileDevice"));
            model.put("isLeftToRight", requestParams.get("isLeftToRight"));
            model.put("currentLang", requestParams.get("currentLang"));
            model.put("defaultLang", requestParams.get("defaultLang"));
            model.put("isDefaultLang", requestParams.get("isDefaultLang"));
            model.put("calendarType", requestParams.get("calendarType"));
            model.put("showAddToBasket", requestParams.get("showAddToBasket"));
            model.put("showFastView", requestParams.get("showFastView"));
            model.put("showShippingIcons", requestParams.get("showShippingIcons"));
        }
        return model;
    }




    public static User getUserInfo(long userId) {
        User user = userService.getUserInfo(userId);
        return user;
    }





    //TODO : Get Breadcrump JsonLd https://stackoverflow.com/questions/33688608/how-to-markup-the-last-non-linking-item-in-breadcrumbs-list-using-json-ld
    public static String getSchemaBreadcrump(ArrayList<HashMap.Entry<String, String>> linkList) {
        JsonLdSerializer serializer = new JsonLdSerializer(true /* setPrettyPrinting */);
        BreadcrumbList.Builder builder = CoreFactory.newBreadcrumbListBuilder();

        ItemList.Builder itemListBuilder = CoreFactory.newItemListBuilder();

        for (HashMap.Entry<String, String> item : linkList) {
            builder.addItemListElement(
                    itemListBuilder.addItemListElement("adasdadsads"));
        }
        BreadcrumbList object = builder.build();

        //TODO Complete JsonLte
        try {
            String jsonLdStr = serializer.serialize(object);

            return jsonLdStr;
        } catch (JsonLdSyntaxException e) {
            // Errors related to JSON-LD format and schema.org terms in JSON-LD
        } catch (JsonIOException e) {
            // Errors related to JSON format
        }
        return "";
    }

    public static boolean needToRedirect(String validSlug, String urlSlug) {

        String encodeValidSlug = getAsciiEncodeUrl(validSlug);
        String encodeUrlSlug = getAsciiEncodeUrl(urlSlug);

        return !encodeValidSlug.equals(encodeUrlSlug);

    }



    /*    public static void sendOneSignalNotification(String Segmant,String message,String header,String subtitle,
                                                 String urlpath,String iconpath,String imagepath)
    {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic NjYzYjllNDgtMzU2YS00M2JhLWE0YmEtZDQzMGMwNTA5MzA0");//for localhost:8285
            //con.setRequestProperty("Authorization", "Basic MzA3MWZhNDYtNjIzOS00OGI2LWI5MzgtMjg4MzE1OWUwM2Uw");//for http://localhost:85
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"3e97143b-c21a-49a7-b7ed-404dbed35528\"," //for localhost:8285
                    //+   "\"app_id\": \"336fe984-1a64-4c63-a0c5-f17658aee9d5\"," //for http://localhost:85
                    +   "\"safari_web_id\": \"web.onesignal.auto.41aef4b6-51a4-4778-92bc-f3aa3e26acde\"," //for http://localhost:85
                    +   "\"included_segments\": [\""+Segmant+"\"],"
                    //+   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \""+message+"\"},"
                    +   "\"headings\": {\"en\": \""+header+"\"},"
                    +   "\"subtitle\": {\"en\": \""+subtitle+"\"},"
                    +   "\"url\": \""+urlpath+"\","
                    +   "\"chrome_web_image\": \""+imagepath+"\","
                    +   "\"chrome_web_icon\": \""+iconpath+"\""
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }*/


    public static ResponseMessage returnCustomResponse(Object obj, Integer statusCode, String message) {
        ResponseMessage rs = new ResponseMessage();
        rs.setReturnObject(obj);
        rs.setStatusCode(statusCode);
        rs.setMessage(message);
        return rs;
    }

    public static ResponseMessage returnSucsess(Object obj) {
        ResponseMessage rs = new ResponseMessage();
        rs.setReturnObject(obj);
        rs.setStatusCode(200);
        rs.setMessage(Utils.getMessageResource("global.operation.success_info"));
        return rs;
    }

    public static ResponseMessage returnFailure(Object obj) {
        ResponseMessage rs = new ResponseMessage();
        rs.setReturnObject(obj);
        rs.setStatusCode(400);
        rs.setMessage("Operation Failure");
        return rs;
    }

    public static ResponseSiteMap returnSiteMaps(Object obj) {
        ResponseSiteMap rs = new ResponseSiteMap();
        rs.setSitemaps(obj);
        return rs;
    }

    public static ResponseSiteMapIndex returnSiteMapIndexes(Object obj) {
        ResponseSiteMapIndex rs = new ResponseSiteMapIndex();
        rs.setSitemaps(obj);
        return rs;
    }

    public static Integer getWordCount(String value) {
        int count = 0;
        if (value != null) {
            value = value.replaceAll("\0", "").replaceAll("&nbsp;", "");
            value=FIG_CAPTION_TAG_PATTERN.matcher(value).replaceAll(" ");
            value= cleanHTML(value);
            Matcher matcher = SPACE_PATTERN.matcher(value);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }

    public static Integer getImageCount(String value) {
        int count = 0;
        if (value != null) {
            value = value.replaceAll("\0", "");
            Matcher matcher = IMAGE_TAG_PATTERN.matcher(value);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }



    public static String getFullName(String firstName, String lastName) {
        String userFullName = (isStringSafeEmpty(firstName) && isStringSafeEmpty(lastName)) ?
                Utils.getMessageResource("global.noName")//userService.getCurrentUser().getUsername()
                : ((!isStringSafeEmpty(firstName) ? firstName : "") + " " + (!isStringSafeEmpty(lastName) ? lastName : ""));
        return userFullName;
    }

    public static String replaceEnterWithBr(String value) {
        return value.replace("\n", "<br/>");
    }

    public static String getLogoUrl() {
        return getFileResourceDomain() + "/assets/image/logo.png";
    }

    public static String getCompleteLogoUrl() {
        return getLogoUrl();
    }

    public static String getCookie(HttpServletRequest request, String cookieName, String defaultValue) {
        String result = defaultValue;
        if (request.getCookies() == null)
            return result;

        for (int i = 0; i < request.getCookies().length; i++) {
            if (request.getCookies()[i].getName().equals(cookieName)) {
                return request.getCookies()[i].getValue();
            }
        }
        return result;
    }
/*    public static String getCookie(HttpServletResponse response, String cookieName, String defaultValue) {
        String result = defaultValue;
        if (response.ggetCookies() == null)
            return result;

        for (int i = 0; i < request.getCookies().length; i++) {
            if (request.getCookies()[i].getName().equals(cookieName)) {
                return request.getCookies()[i].getValue();
            }
        }
        return result;
    }*/

    public static void setCookie(HttpServletResponse response, String cookieName, String value) {
        setCookie(response, cookieName, value, "/", false, 86400);
    }

    public static void setCookie(HttpServletResponse response, String cookieName, String value, String path) {
        setCookie(response, cookieName, value, path, false, 86400);
    }

    public static void setCookie(HttpServletResponse response, String cookieName, String value, String path, Boolean httpOnly, Integer expireTime) {
        response.addCookie(Utils.createCookie(cookieName, value , path ,expireTime,httpOnly));
        /*Cookie authCookie = new Cookie(cookieName, value);
        authCookie.setPath(path);
        if(httpOnly!=null)
           authCookie.setHttpOnly(httpOnly);
        if(expireTime!=null)
           authCookie.setMaxAge(expireTime);
        response.addCookie(authCookie);*/
    }


    public static void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


    public static void setCityCookie(HttpServletResponse response, String cityEname) {
        setCookie(response, "cy", cityEname, "/blog", false, 2592000);
    }

    public static String getCityCookie(HttpServletRequest request) {
        String cityEname = getCookie(request, "cy", "tehran").trim();
        return cityEname;
    }

    public static Cookie createCookie(String name,String value ,String path ,Integer expire ,Boolean httpOnly) {
        String domain="";
        if (isAppProductionMode()) {
            domain = getShortDomainUrl();
            if (!Utils.stringCompare(getDomainUrl(),getFileResourceDomain()))
                domain ="." + domain;
        }
        return createCookie( name,value ,path ,expire , httpOnly,domain);
    }
    public static Cookie createCookie(String name,String value ,String path ,Integer expire ,Boolean httpOnly,String domain) {
        Cookie cookie = new Cookie(name, (value));

        if (!isStringSafeEmpty(domain))
           cookie.setDomain(domain);
        cookie.setPath(path);
        if(httpOnly!=null)
           cookie.setHttpOnly(true);
        if(expire!=null)
           cookie.setMaxAge(expire);
        return cookie;
    }
    public static Cookie createCookie(String name,String value ,String path ) {
        Cookie cookie = new Cookie(name, (value));
        //cookie.setHttpOnly(true);
        cookie.setPath(path);
        return cookie;
    }


    public static final boolean checkIfResourceExists(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        int code = conn.getResponseCode();
        conn.disconnect();
        return code == 200;
    }

    public static boolean hasRoleType(ERoleType eRoleType) {
        return userService.hasRoleType(eRoleType);
    }


    public static boolean hasRole(String role) {
        return userService.hasExistRole(role);
    }
    public static boolean hasRole(Long userId,String role) {
        return userService.hasExistRole(userId,role);
    }
/*    public static boolean isAuthUser() {
        return userService.isAuthUser();
    }

    public static Long getCurrentUserId() {
        if (userService.isAuthUser())
           return userService.getCurrentUser().getId();
        return 0l;
    }*/

    public static boolean hasPermission(String permission) {
        return userService.hasExistPermission(permission);
    }

    public  static boolean isBotAgent(String agent){
        UserAgent userAgent = UserAgent.parseUserAgentString( agent);
        if (userAgent.getBrowser().getBrowserType().getName().toLowerCase().contains("bot"))
            return  true;

        return false;
    }
    public  static HttpServletResponse setXSRFToken(HttpServletResponse response,HttpServletRequest request){
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
            String token = csrf.getToken();
            response.setHeader("X-XSRF-TOKEN", token);

            if ((cookie == null) || (token != null && !token.equals(cookie.getValue()))) {
                cookie = createCookie("XSRF-TOKEN",token,"/");
                /*cookie = new Cookie("XSRF-TOKEN", token);
                cookie.setPath("/");
                //    cookie.setHttpOnly(false);
                //cookie.setDomain("localhost");
                //cookie.setMaxAge(1000 );*/
                response.addCookie(cookie);
            }
        }
        return response;
    }

    public static List<Long>  deleteFromList(List<Long> inputList,Long number){
        for (int i = 0; i < inputList.size(); i++) {
            if (inputList.get(i).equals(number))
                inputList.remove(i);
        }
        return inputList;
    }

    public static String injectCssAndLinksForgoogle(HttpServletRequest request , String content,String linkAddress){
        if (detectgoogle(request)) {
            //content = content.replaceFirst("</style>", " .alinktu{color: #039be5 !important;border-bottom: dashed 1px;}</style>");
            content = content.replaceFirst("</head>", "<style> .alinktu{color: #039be5 !important;border-bottom: dashed 1px;}</style></head>");
            content=content.replaceFirst("<injectedAdsBox/>","");
            return content.replaceFirst("EOF-FIRST-PARAGRAPH","");
        }else {
            //content= content.replaceFirst("</style>", " .hdr a{color: #fff;}a{text-decoration: none;color: #454545;}</style>");
            content = content.replaceFirst("</head>", "<style> .hdr a{color: #fff;}a{text-decoration: none;color: #454545;}</style></head>");
            return content.replaceFirst("EOF-FIRST-PARAGRAPH",
                                  "<map name=\"planetmap\">" +
                                              "  <area shape=\"circle\" coords=\"0,0,1,1\" href=\"" + linkAddress +" \" alt=\"\">" +
                                              "</map>");
        }
      //  return content;
    }

    public static String replaceShowAd(String destination,HttpServletRequest request) {
        String showAdTag="<showAd/>";
        destination = destination.replaceAll(showAdTag, (!detectgoogle(request))+"");
        return destination;
    }

    public static Boolean detectgoogle(HttpServletRequest request)
    {
        String userIp="#" + getClientIp(request);
        List<String> ipList =new ArrayList<>(Arrays.asList(/*"local","127.0.","0:0:0:",*/
                "66.102.", "66.249.", "64.233.", "72.14.", "185.206.68.",
                "74.125.", "209.85.", "17.203.53.", "64.62.252.164", "216.239.",
                "8.6.", "8.8.", "8.35.", "8.34.", "23.236.", "23.251.", "63.161.",
                "63.166.", "64.9.", "64.18.", "65.167.", "65.170.", "65.171.",
                "70.32.", "72.14.", "74.125.", "89.207.", "104.154.", "104.132.",
                "107.167.", "107.178.", "108.59.", "108.170.", "108.177.", "130.211.",
                "142.250.", "144.188.", "146.148.", "162.216."));
        for (String ip: ipList) {
          if (userIp.startsWith("#"+ip))
              return true;
        }
// detect bot
        String agent= getUserAgent(request).toLowerCase();
        Pattern botPatterns =  Pattern.compile("bot|crawl|slurp|spider|Google|google|check_http|nagios", Pattern.CASE_INSENSITIVE);

        if (botPatterns.matcher(agent).matches())
            return true;
        return false;
    }



/*    public static String linkBulding(String content,Integer maxLinkBuilding){
        try {
            return seoService.linkBuilding(content ,maxLinkBuilding);
        }catch (Exception e){
            e.printStackTrace();
            return content;
        }
    }*/

    public static Integer addPageParameter(Map<String,Object> requestParams, Integer pageSize, Boolean singlePage){
        Integer pageNum = 1;
        if(!requestParams.containsKey("start")) {
            pageNum = getAsIntegerFromMap(requestParams, "page", false, 1);
            Integer start = singlePage ? (pageNum - 1) * pageSize : 0;
            Integer count = singlePage ? pageSize : (pageNum * pageSize);
            if (requestParams.containsKey("page"))
                requestParams.remove("page");

            requestParams.put("start", start.toString());
            requestParams.put("count", count.toString());
        }
        return pageNum;
    }





    public static Long getNotifyType() {
        try {
            return commonService.getNotifyType();
        }catch (MyException ex){
            ex.printStackTrace();
            return 0l;
        }
    }


    public static Integer  getOperationRequestWaitMinuteTime() {
        try {
            return commonService.getOperationRequestWaitMinuteTime();
        }catch (MyException ex){
            ex.printStackTrace();
            return 10;
        }
    }

    public static String getFixMetaHeaders() {
        return commonService.getFixMetaHeaders();
    }




    public static RequestPerIpDetails cleanExpiredRequests(RequestPerIpDetails requestPerIpDetails, Integer timeSizeInSecond) {
        long expiredEpochMinute = requestPerIpDetails.getLastEpochMinute() - (timeSizeInSecond);

        List<Long> tmpRequestsPerIp=new ArrayList<>();
        requestPerIpDetails.getRequestsPerIp().stream().forEach(ipEpoch -> {
            if(ipEpoch.longValue()>=expiredEpochMinute)
                tmpRequestsPerIp.add(ipEpoch);
        });
        requestPerIpDetails.setRequestsPerIp(tmpRequestsPerIp);

        if ( requestPerIpDetails.getRequestsPerIp().isEmpty()) {
            requestPerIpDetails.getRequestsPerIp().clear();
        }
        return requestPerIpDetails;
    }

    public static RequestPerIpDetails refreshRequests(RequestPerIpDetails requestPerIpDetails, Long epochSecond) {
        if (requestPerIpDetails==null)
            requestPerIpDetails = new RequestPerIpDetails();

        requestPerIpDetails.getRequestsPerIp().add(epochSecond);

        long epochMinute = epochSecond - (epochSecond % 60);
        requestPerIpDetails.setLastEpochMinute(epochMinute);
        return requestPerIpDetails;
    }


    //#region Register Config
    private static String userNameField;
    public static String getUserNameField() {
        userNameField = isStringSafeEmpty(userNameField) ? getAppPropery("app.register.userNameField", "mobile") : userNameField;
        return userNameField.toLowerCase();
    }
    public static Boolean getMobileIsRequired() {
        return commonService.getMobileIsRequired();
    }

    public static Boolean getEmailIsRequired() {
        return commonService.getEmailIsRequired();
    }
    public static String getMobilePattern() {
        return commonService.getMobilePattern();
    }

    public static Locale getDefaultLocale() {
        return commonService.getDefaultLocale();    }
    //#endregion

    public static Long getCurrentLanguageId(){
        return commonService.getCurrentLanguageInfo().getId();
    }
    public static String getCurrentLanguageShortName(){
        return commonService.getCurrentLanguageInfo().getShortName();
    }


    public static Object addGlobalCacheKey(Object key){
        //if(!MultiLingualDataContextHolder.getMultiLingualData().currentLanguageEqualToBaseLang())
        if (MultiLingualDataContextHolder.getMultiLingualData()!=null)
           key=MultiLingualDataContextHolder.getMultiLingualData().getCurrentLanguageId() +"_"+ key;
        else
           key=commonService.getDefaultLanguage() +"_"+ key;
        return key;
    }
    public static List<Object> addGlobalCacheKeys(Object key){
        List<Object> keys=new ArrayList<>();
        Object finalKey = key;
        commonService.getActiveLanguages().forEach(l -> {
             keys.add(l.getId()+"_"+ finalKey);
        });
        return keys;
    }


    public static String getMainIp(String ip){
        if(Utils.isStringSafeEmpty(ip))
            return "";
        String[] ips=ip.split(",");
        if(ips.length==0)
            return "";

        return ips[0];
    }


    public static void checkLoginAndAuthorize(String authority){
        if (!Utils.isAuthUser())
            throw new ResourceNotAuthorizedException("No Auth","global.accessDeniedMessage1");
        if (!Utils.hasPermission(authority))
            throw new NoAccessException("No Access","global.accessDeniedMessage2");
    }



    //#region Message
    public static MessageResource getMessageInfo(String key){
        return commonService.getMessageInfo(key);
    }

    //#endregion

    public static Boolean isValidLong(String num) {
        try {
            Long.parseLong(num);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static void userNameValidate(String userName) {
        if (Utils.getUserNameField().equals("mobile")) {
            if (!Utils.isValidByPattern(userName, commonService.getMobilePattern())/*Mobile Pattern Check*/)
                throw new InvalidDataException("InvalidData", "common.user.mobileNumber_len");
        } else if (Utils.getUserNameField().equals("email")) {
            if (!Utils.isValidEmail(userName))/*Email Pattern Check*/
                throw new InvalidDataException("InvalidData", "common.user.email_check");
        }
    }

    public static RequestMatcher changeCredentialRequestMatcher = new RequestMatcher() {
        private AntPathRequestMatcher[] requestMatchers = {
                new AntPathRequestMatcher("/api/auth/logout"),
                new AntPathRequestMatcher("/api/admin/clearAllCache"),
                new AntPathRequestMatcher("/api/admin/clearCache"),
                new AntPathRequestMatcher("/api/users/forceToChangePassword"),
                new AntPathRequestMatcher("/api/users/updatePassword/**"),
                new AntPathRequestMatcher("/panel#!/changepassword/**"),
                new AntPathRequestMatcher("/nimdalenap#!/changepassword/**"),
                new AntPathRequestMatcher("/panel/**"),
                new AntPathRequestMatcher("/nimdalenap/**"),
                new AntPathRequestMatcher("/panelshare/**"),
                new AntPathRequestMatcher("/api/common/getJsMessageResources.js"),
                new AntPathRequestMatcher("/api/common/getJsonMessageResources.json")

        };

        @Override
        public boolean matches(HttpServletRequest request) {
            for (AntPathRequestMatcher rm : requestMatchers) {
                if (rm.matches(request)) { return true; }
            }
            return false;
        }

    };

    public static String checkForceChangeCredential(HttpServletRequest request) {
        if (request!=null && Utils.isAuthUser() && !Utils.getCurrentUser().isCredentialsNonExpired() && !changeCredentialRequestMatcher.matches(request)) {
            if(request.getRequestURI().startsWith("/api/"))
               return "api";
            else
               return "page";
        }
        return "other";
    }


    public static void writeHtml(HttpServletResponse response, HttpServletRequest request, String value, String contentType) {
        response.setContentType(contentType);
        try {
            response.getWriter().write(value);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("global.resolver_error", e.getMessage());
        }
    }


    public static void sendError(HttpServletRequest request, HttpServletResponse response,Integer errorStatus,String errorMessage) throws IOException{
            if(request.getRequestURI().startsWith("/api/")) {
                RestExceptionResponse exResponse = new RestExceptionResponse();
                exResponse.setErrorCode(errorStatus.toString());
                exResponse.setErrorMessage(errorMessage);
                response.setContentType("application/json; charset=UTF-8");
                response.setStatus(errorStatus);
                response.getWriter().write( Utils.getAsJson(exResponse) );
            }else{
                response.sendError(errorStatus,errorMessage);
                response.addHeader("retUrl", request.getRequestURI());
            }
    }


    public static Object getCommonConfigValue(String name) {
        return commonService.getConfigValue(name);
    }

}

