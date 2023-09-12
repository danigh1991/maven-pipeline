package com.core.datamodel.util;

import com.core.context.MultiLingualDataContextHolder;
import com.core.datamodel.model.dbmodel.Currency;
import com.core.datamodel.model.dbmodel.Language;
import com.core.datamodel.model.enums.EScreenType;
import com.core.datamodel.services.CacheService;
import com.core.model.enums.ECalendarType;
import com.core.services.CalendarService;
import com.core.services.ModelUtilityService;
import com.core.model.enums.Calendar;
import com.core.services.MultiLingualService;
import com.core.util.BaseUtils;
import java.util.Date;
import java.util.Map;


public class ShareUtils extends BaseUtils {
    private static CalendarService calendarService;
    private static ModelUtilityService modelUtilityService;
    private static CacheService cacheService;
    private static MultiLingualService multiLingualService;


    public static void setCalendarService(CalendarService calendarService) {
        ShareUtils.calendarService = calendarService;
    }

    public static void setModelUtilityService(ModelUtilityService modelUtilityService) {
        ShareUtils.modelUtilityService = modelUtilityService;
    }

    public static void setCacheService(CacheService cacheService) {
        ShareUtils.cacheService = cacheService;
    }

    public static CacheService getCacheService() {
        return cacheService;
    }

    public static void setMultiLingualService(MultiLingualService multiLingualService) {
        ShareUtils.multiLingualService = multiLingualService;
    }

    public static MultiLingualService getMultiLingualService() {
        return multiLingualService;
    }



    //#region Calendar
    public static Long getCalendarTypeId() {
        return modelUtilityService.getCalendarTypeId();
    }




    public static Date getDateFromString(String date) {
        if(date == null)
            return null;
        return calendarService.getDateFromString(date);
    }

    public static Date getDateFromString(String date, ECalendarType forceCalendarType) {
        if(date == null)
            return null;
        return calendarService.getDateFromString(date,forceCalendarType);
    }

    public static Date getDateTimeFromString(String dateTime) {
        if(dateTime == null)
            return null;
        return calendarService.getDateTimeFromString(dateTime);
    }

    public static Date getDateTimeFromString(String dateTime, ECalendarType forceCalendarType) {
        if(dateTime == null)
            return null;
        return calendarService.getDateTimeFromString(dateTime, forceCalendarType);
    }


    public static Date addSecondToDate(Date dt, Integer second) {
        return calendarService.addSecondToDate(dt, second);
    }

    public static Date addMinuteToDate(Date dt, Integer minute) {
        return calendarService.addMinuteToDate(dt, minute);
    }

    public static Date addDayToDate(Date dt, Integer day) {
        return calendarService.addDayToDate(dt, day);
    }

    public static String getShamsiDate(Date date) {
        if(date == null)
            return "";
        return calendarService.getNormalDateFormat(date);
    }
    public static String getDateYYMM(Date date) {
        if(date == null)
            return "";
        return calendarService.getNormalDateFormatYYMM(date);
    }

    public static String getShamsiDateTime(Date date) {
        if(date == null)
            return "";
        return calendarService.getNormalDateTimeFormat(date);
    }
    public static String getShamsiDateTimeShort(Date date) {
        if(date == null)
            return "";
        return calendarService.getNormalDateTimeShortFormat(date);
    }

    public static String getNormalDateShortFormat(Date date) {
        if(date == null)
            return "";
        return calendarService.getNormalDateShortFormat(date);
    }

    public static String getNormalTimeFormat(Date date) {
        if(date == null)
            return "";
        return calendarService.getNormalTimeFormat(date);
    }

    public static String getDayCaption(Date date){
        if(date == null)
            return "";
        return ShareUtils.getDayCaption(calendarService.getDayOfWeek(date));
    }
    public static String getDayCaption(Integer weekDay){
        switch (weekDay){
            case 1:
               return ShareUtils.getMessageResource("common.calendar.sunday_info");
            case 2:
                return ShareUtils.getMessageResource("common.calendar.monday_info");
            case 3:
                return ShareUtils.getMessageResource("common.calendar.tuesday_info");
            case 4:
                return ShareUtils.getMessageResource("common.calendar.wednesday_info");
            case 5:
                return ShareUtils.getMessageResource("common.calendar.thursday_info");
            case 6:
                return ShareUtils.getMessageResource("common.calendar.friday_info");
            case 7:
                return ShareUtils.getMessageResource("common.calendar.saturday_info");
            default:
                return "";
        }


    }



    public static Date getDateTimeAt24(Date dt) {
        return calendarService.getDateTimeAt24(dt);
    }

    public static Date getDateTimeAt00(Date dt) {
        return calendarService.getDateTimeAt00(dt);
    }

    public static Date getDateOnly(Date dt) {
        return calendarService.getDateOnly(dt);
    }

    public static int getSecondDiff(Date fromDate, Date toDate) {
        return calendarService.dateDiff(fromDate, toDate, Calendar.SECOND);
    }

    public static int getMinuetDiff(Date fromDate, Date toDate) {
        return calendarService.dateDiff(fromDate, toDate, Calendar.MINUET);
    }

    public static int getHourDiff(Date fromDate, Date toDate) {
        return calendarService.dateDiff(fromDate, calendarService.getDateTimeAt24(toDate), Calendar.HOUR);
    }

    public static int getDayDiff(Date fromDate, Date toDate) {
        return calendarService.dateDiff(fromDate, calendarService.getDateTimeAt24(toDate), Calendar.DAY);
    }

    protected static CalendarService getCalendarService() {
        return calendarService;
    }


    public static Date getStartYearDate(Date date) {
        return calendarService.getStartYearDate(date);
    }

    public static String getNormalDateWithoutSlash(Date dt) {
        return calendarService.getNormalDateWithoutSlash(dt);
    }

    //#endregion


    //#region Currency
    public static Integer getPanelCurrencyRndNumCount() {
        return modelUtilityService.getPanelCurrencyRndNumCount();
    }

    public static Integer getCurrencyRndNumCount(Long currencyId) {
        return modelUtilityService.getCurrencyRndNumCount(currencyId);
    }

    public static String getPanelCurrencyShortName() {
        return modelUtilityService.getPanelCurrencyShortName();
    }

    public static String getCurrencyShortName() {
        return modelUtilityService.getCurrencyShortName();
    }

    public static Currency getDefaultCurrencyModel() {
        return (Currency) modelUtilityService.getActiveCurrencyInfo(Long.parseLong(getAppPropery("app.default_currency_id", "1")));
    }

    public static String getMySiteDomain() {
        return modelUtilityService.getMySiteDomain();
    }

    public static String getMyBrandName() {
        return modelUtilityService.getMyBrandName();
    }


    //#endregion


    public static String getCompleteUrl(String url) {
        return ShareUtils.getCompleteUrl(url, false);
    }

    public static String getCompleteUrl(String url, Boolean preparUrl) {
        url = preparUrl ? ShareUtils.prepareUrl(url) : urlTrailSlash(url);
        return getDomainUrl() + (url.startsWith("/") ? url : ("/" + url));
    }

    public static String getNonMultiLingualUrl(String url){
        boolean urlStartWithSlash = url.startsWith("/");
        url = (url + "/").replaceAll("^\\/(?i)[a-z]{2}\\/", "");
        return (urlStartWithSlash && !url.startsWith("/") ? "/" : "") + urlTrailSlash(url);
    }

    public static String getUrlByLanguage(Language language, Map<String, Object> requestScope) {
        String currentUrlQueryString = requestScope.get("javax.servlet.forward.query_string") == null ? "" : requestScope.get("javax.servlet.forward.query_string").toString();
        String currentUrl =requestScope.get("javax.servlet.forward.request_uri")!=null ? getNonMultiLingualUrl(requestScope.get("javax.servlet.forward.request_uri").toString()):"";
        //String currentAbsolutUrl = requestScope.get("currentAbsolutUrl").toString();
        String langUrl = language.getLocale().getLanguage();

        String LangPrefix = "";
        if (!MultiLingualDataContextHolder.getMultiLingualData().isBaseLang(language.getId())) {
            boolean urlStartWithSlash = currentUrl.startsWith("/");
            LangPrefix = (urlStartWithSlash ? "/" : "") + langUrl + (urlStartWithSlash ? "" : "/");
        }
        return urlTrailSlash(LangPrefix + urlTrailSlash(currentUrl) + (ShareUtils.isStringSafeEmpty(currentUrlQueryString) ? "" : "?") + currentUrlQueryString).toLowerCase();
    }

    public static String getCompleteUrlByLanguage(Language language, String completeUrl) {
        String langPrefix = language.getLocale().getLanguage();
        Boolean isBaseLang = MultiLingualDataContextHolder.getMultiLingualData().isBaseLang(language.getId());
        String currentUrl = completeUrl.replace(getDomainUrl(), "");
        currentUrl = getNonMultiLingualUrl(currentUrl);
        boolean urlStartWithSlash = currentUrl.startsWith("/");
        langPrefix = "/" + langPrefix + (urlStartWithSlash ? "" : "/");
        completeUrl =  getDomainUrl() + (isBaseLang ? currentUrl : langPrefix + currentUrl);
        return urlTrailSlash(completeUrl);
    }


    public static EScreenType getScreenType(String deviceName) {
        if (isMobileDevice(deviceName))
            return EScreenType.MOBILE;
        return EScreenType.DESKTOP;
    }

}
