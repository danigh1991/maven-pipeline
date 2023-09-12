package com.core.common.services;

import com.core.common.model.contextmodel.*;
import com.core.common.model.enums.EConfigurationResultType;
import com.core.common.model.enums.EConfigurationType;
import com.core.common.model.enums.EConfirmableOperation;
import com.core.common.model.enums.EUserConcurrentStrategy;
import com.core.datamodel.model.cachemodel.RelatedReference;
import com.core.datamodel.model.dbmodel.*;
import com.core.datamodel.model.dbmodel.Currency;
import com.core.datamodel.model.enums.*;
import com.core.datamodel.model.wrapper.*;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.ResultPageable;
import org.springframework.mobile.device.Device;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


public interface CommonService extends Service {


    String getNowDate();
    List<MobileAppVersion> getAllActiveMobileAppVersion();
    Map<String,Object> checkNeedToUpdate(String currentVersion);
    Map<String,Object> getMobileAppConfig();
    MobileAppVersion getLastActiveMobileAppVersion();
    Map<String,Object> getLastActiveMobileAppPaths();

    //#region  ContactUs
    String addContactUs(CContactUs cContactUs, String ip, String agent, String machineId, String trackingCode);
    ResultListPageable<ContactUs> getContactUsList(Integer type, Integer start, Integer count);
    ContactUs getContactUsInfo(Long id);
    String replyToContactUs(CContactUsReply cContactUsReply);
    //#endregion


    //#region  Feedback
    String addFeedback(FeedbackDto feedbackDto, String ip, String agent, String machineId, String trackingCode);
    ResultListPageable<Feedback> getAllFeedbackList(Integer start, Integer count);
    ResultListPageable<Feedback> getFeedbackList(Integer start, Integer count);
    ResultListPageable<Feedback> getFeedbackList(Long userId ,Integer start, Integer count);
    Feedback getFeedbackInfo(Long feedbackId);
    String removeFeedback(Long feedbackId);
    //#endregion


    //#region  Site Theme
    SiteTheme getDefaultSiteThemeDetails();
    SiteTheme getSiteThemeInfo(Long siteThemeId);
    SiteTheme getActiveSiteThemeInfo(Long siteThemeId);
    //#endregion


    //#region  Configuration
     void registerConfigModel(String name, EConfigurationType type, EConfigurationResultType resultType,String defaultValue);
     <T> T getConfigValue(String name);

     List<ConfigurationType> getAllConfigs();
     List<ConfigurationType> getConfigsTypeBySysName(String sysName);
     Configuration getConfigurationInfoFromDb(String name);
     Configuration editConfiguration(CConfiguration cConfiguration);

    Long getFinancialUserId();
    String getRobotTxt();

    String getValidActiveBankGateways();
    List<EBank> getValidActiveBanks();

    //Saman Bank
    String getSamanBankTerminalId();
    String getSamanBankPassword();


    String getAdminMobileNumbers();
    String getAdminEmailsAddress();
    Boolean getAdminMessageForContactUs();
    Boolean getAdminMessageForError();

    Boolean getSendUserActionSms();
    Double getMinRequestRefundMoney();

    String getMyBrandName();
    String getMyBrandEName();
    String getMySiteTitle();
    String getMySiteDescription();
    String getMySiteDomain();

    Long  getDefaultSiteTheme();

    String  getLogoUrl();
    String  getResourceVersion();
    String  getFavIconUrl();
    Long  getAffiliateState();

    Long  getDefaultLanguage();
    Long  getDefaultCurrency();
    Long  getPanelCurrency();


    Long getCalendarType();
    Long getNotifyType();
    Long getLoginType();


    String getRegisterRolesPage();
    String getDefaultTimeZone();
    void setDefaultTimeZone();

    String getFixMetaHeaders();

    String getMandatoryFinanceDestNumExp();
    Boolean getMobileIsRequired();
    Boolean getEmailIsRequired();
    String getMobilePattern();

    Integer getVerificationCodeLiveTimeSecond();
    Integer getRegisterUserLiveTimeSecond();
    String getInviteFriendMessage();

    EUniqueCodeType loginUniqueCodeType();
    Boolean getForce2FAuthentication();

    Integer getUniqueCodeValidationType();
    String getShahkarPrivetKey();
    String getShahkarHashAlgorithm();



    //#region  Security
    Integer getPasswordValidityDay();
    Boolean getForceChangePasswordAfterReset();
    Integer getUserConcurrentLoginCount();
    EUserConcurrentStrategy getUserConcurrentStrategy();
    Integer getAllowedIdleTime();
    String getJwtTokenKey();
    Boolean getJwtTokenCryptStatus();

    Integer getMaxAttemptForLockLevel1();
    Integer getLockMinuteLevel1();
    Integer getMaxAttemptForLockLevel2();
    Integer getLockMinuteLevel2();
    Integer getMaxAttemptForAlwaysLock();

    Integer getApiCallDurationLimit();
    Integer getApiCallCountLimit();

    //#endregion Security


    //#region  TopUp Config
    Integer getMciProductList_CacheTime();
    Integer getInternetPackageList_CacheTime();
    //#endregion TopUp Config



    //#region  Samat IPG
    String getSamatIpgBaseUrl();
    String getSamatIpgIdentity();
    String getSamatIpgPrivateKey();
    String getSamatIpgHashAlgorithm();
    //#endregion  Samat IPG


    Integer  getOperationRequestWaitMinuteTime();
    String getAboutApp();
    Boolean tokenIpCheck();
    Boolean refreshTokenIpCheck();

    Integer getDefaultQueryCount();
    Boolean getExternalDepositRequestStatus();

    //#endregion


    //#region Domain, Language And Currency
    List<DomainWrapper> getActiveDomainWrappersByUrl(String domainUrl);
    DomainWrapper getCurrentActiveDomainWrapperByUrl(String domainUrl);
    DomainWrapper getDefaultActiveDomainWrapper();


    Language getLanguageInfo(Long languageId);
    Language getDefaultLanguageInfo();
    Language getLanguageInfo(String shortName);
    Language getActiveLanguageInfo(Long languageId);
    Language getActiveLanguageInfo(String shortName);
    List<Language> getActiveLanguages();
    Language getCurrentLanguageInfo();


    Locale getDefaultLocale();
    Currency getCurrencyInfo(Long currencyId);
    Currency getActiveCurrencyInfo(Long currencyId);
    Integer getActiveCurrencyRndNumCount(Long currencyId);
    String getActiveCurrencyShortName(Long currencyId);
    //#endregion


    //#region Admin Notices
     String sendNoticeToAdmin(String subject,String smsMessage, String emailMessage,ETargetTypes eTargetTypes, Long targetId);
     String sendNoticeToAdmin(String subject,String smsMessage, String emailMessage,ETargetTypes eTargetTypes, Long targetId,String adminMobileNumbers,String adminEmailsAddress);
    //#endregion


    //#region Path Limitation for fake visit and crawler and AllowedIpPath
    List<PathLimitation> getLimitationPathConfigs();
    List<String> getLimitationPaths();
    List<String> getBlockedIps();


    List<AllowedIpPath> getActiveAllowedIpPaths();
    List<AllowedIpPath> getAllAllowedIpPaths();
    //#endregion


    //#region   City And Region
    List<Province> getProvinceList();
    List<Province> getActiveProvinceList();
    Province getProvinceInfo(Long provinceId);
    Province getActiveProvinceInfo(Long provinceId);
    Province getActiveProvinceInfo(Long provinceId, Long countryId);

    List<City> getCityList();
    List<City> getActiveCity(Long provinceId);

    City getCityInfo(Long cityId);
    City getActiveCityInfo(Long cityId);
    City getCityById(Long cityId);
    City getCityByEnglishName(String englishName);

    List<Region> getRegionList(Long cityId);
    Region getRegionInfo(Long regionId);
    Region getRegionByIdAndCityId(Long cityId,Long regionId);
    List<Country> getCountries();


    Country getCountryInfo(Long countryId);
    Country getActiveCountryInfo(Long countryId);
    //#endregion


    //#region  Redirect Url
    List<RedirectUrl> getActiveRedirectUrls();
    //#endregion


    //#region Message Resource

    List<MessageResource> getMessageResources(Integer messageResourceType);
    MessageResource getMessageInfo(Long messageId);
    MessageResource getMessageInfo(String key);
    MessageResource editMessageResource(EMessageResource eMessageResource);
    Map<String, String> getJsonMessageResources();
    String getJsMessageResources();
    //#endregion

    //#region Notify
    void sendNotification(Long smsBodyCodeId,Long recipientUserId,List<String> mobiles,String[] mobileMessage,List<String> emails,String[] emailMessage,Long targetTypeId,Long targetId) ;
    void sendConfirmCode(Long smsBodyCodeId,Long userId,String[] mobileMessage,String[] emailMessage) ;
    void sendConfirmCode(Long smsBodyCodeId,String userMobileNumber,String userEmail,String[] mobileMessage,String[] emailMessage) ;
    void sendConfirmCodeAsync(Long smsBodyCodeId,String userMobileNumber,String userEmail,String[] mobileMessage,String[] emailMessage);
    //#endregion



    //#region Confirm Code
      String generateAndSendConfirmCode(Long userId,EConfirmableOperation eConfirmableOperation, String referenceKey, Map<String,Object> additionalData);
      Boolean  validateConfirmCodeData(Long userId,EConfirmableOperation eConfirmableOperation, String referenceKey,Integer userCode);
    //#endregion



    List<FileManagerAction> getFileManagerConfig(String targetType, Long id);
    FileManagerAction getFileManagerAction(Long fileManagerActionId);


    //#region Cache Service
    String clearCache( String cacheName);
    String clearAllCache();
    Object putInCache( String cacheName,String key, String value,Integer timeToLive);
    Set<Map.Entry> getCacheValues(String cacheName, String sqlPredicateString);
   //#endregion Cache Service

    //#region FileManagerAction
    List<FileManagerAction> getFileManagerActions();
    //#endregion


}

