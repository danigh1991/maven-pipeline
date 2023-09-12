package com.core.common.services.impl;

import com.core.common.model.ConfigurationModel;
import com.core.common.model.enums.EConfigurationResultType;
import com.core.common.model.enums.EConfigurationType;
import com.core.common.model.enums.EConfirmableOperation;
import com.core.common.model.enums.EUserConcurrentStrategy;
import com.core.common.model.sms.SmsServiceResponse;
import com.core.common.services.factory.SmsServiceFactory;
import com.core.context.MultiLingualDataContextHolder;
import com.core.datamodel.model.dbmodel.Currency;
import com.core.datamodel.services.CacheService;
import com.core.model.wrapper.ResultListPageable;
import com.core.services.MultiLingualService;
import com.core.exception.*;
import com.core.common.model.contextmodel.*;
import com.core.datamodel.model.dbmodel.*;
import com.core.datamodel.model.enums.*;
import com.core.model.enums.EMyQueueOperation;
import com.core.model.qmodel.MyQueueData;
import com.core.model.qmodel.QNotifyObject;
import com.core.datamodel.model.wrapper.*;
import com.core.queue.service.QNotifierService;
import com.core.datamodel.repository.*;
import com.core.common.services.*;
import com.core.common.util.Utils;
import com.core.services.CalendarService;
import com.core.util.BaseUtils;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//@Service("abstractCommonServiceImpl")
public abstract class AbstractCommonServiceImpl extends AbstractService implements CommonService {

    @Value("${app.configuration_cache_enable}")
    private Boolean configurationCacheEnable;

    @Value("${app.business_cache_enable}")
    Boolean businessCacheEnable;

    @Value("${app.configuration_base_languageId}")
    private Long baseLanguageId;

    @Value("${app.short_domain_url}")
    protected String short_domain_url ;

    @Autowired
    protected  UserService userService;

    @Autowired
    private TargetTypeRepository targetTypeRepository;

    @Autowired
    private ContactUsRepository contactUsRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FileManagerActionRepository fileManagerActionRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private  NotificationService notificationService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ConfigurationTypeRepository configurationTypeRepository;

    @Autowired
    private QNotifierService qNotifierService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private BaseCurrencyRepository currencyRepository;

    @Autowired
    private PathLimitationRepository pathLimitationRepository;

    @Autowired
    private BlockedIpRepository blockedIpRepository;

    @Autowired
    private AllowedIpPathRepository allowedIpPathRepository;

    @Autowired
    private HtmlCompressor htmlCompressor;

    @Autowired
    private SiteThemeRepository siteThemeRepository;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MultiLingualService multiLingualService;

    @Autowired
    private BaseCityRepository cityRepository;

    @Autowired
    private BaseProvinceRepository provinceRepository;

    @Autowired
    private BaseCountryRepository countryRepository;

    @Autowired
    private RedirectUrlRepository redirectUrlRepository;

    @Autowired
    private BaseRegionRepository regionRepository;

    @Autowired
    private MessageResourceRepository messageResourceRepository;

    @Autowired
    private SmsServiceFactory smsServiceFactory;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private MobileAppVersionRepository mobileAppVersionRepository;

    @Autowired
    private CommonService  commonService;


    //#region   Comment



    @Override
    public String getNowDate() {
        return  calendarService.getNormalDateTimeFormat( new Date());
    }

    @Override
    public List<MobileAppVersion> getAllActiveMobileAppVersion() {
        List<MobileAppVersion> result = (List<MobileAppVersion>) cacheService.getCacheValue("mobileAppVersion", "activeMobileAppVersion");
        if (result != null)
            return result;
        result= mobileAppVersionRepository.findAllActiveMobileAppVersions();

        if (result != null)
           cacheService.putCacheValue("mobileAppVersion",  "activeMobileAppVersion", result,15,  TimeUnit.DAYS);
        return result;
    }

    @Override
    public Map<String,Object> checkNeedToUpdate(String currentVersion) {
        Map<String,Object> result=new HashMap<>();
        List<MobileAppVersion> allActiveMobileAppVersions= this.getAllActiveMobileAppVersion();
        if(allActiveMobileAppVersions==null || allActiveMobileAppVersions.size()==0)
            throw new ResourceNotFoundException("common.mobileAppVersions_notfound");

        Integer currentVersionAsInt=Integer.valueOf(currentVersion.replace(".",""));

        if (allActiveMobileAppVersions.stream().filter(v-> Integer.valueOf(v.getAppVersion().replace(".","")).equals(currentVersionAsInt) ).count()==0)
            throw new ResourceNotFoundException("common.currentVersion_notfound");

        result.put("lastVersion",allActiveMobileAppVersions.get(allActiveMobileAppVersions.size()-1).getAppVersion());
        result.put("forceUpdate",allActiveMobileAppVersions.stream().filter(v-> Integer.valueOf(v.getAppVersion().replace(".",""))>currentVersionAsInt && v.getForceUpdate()).count()>0);

        return result;
    }

    @Override
    public Map<String, Object> getMobileAppConfig() {
        Map<String,Object> result=new HashMap<>();
        result.put("userName",Utils.getCurrentUser() == null ? "" : Utils.getCurrentUser().getUsername());
        result.put("firstName",Utils.getCurrentUser() == null ? "" : Utils.getCurrentUser().getFirstName());
        result.put("lastName",Utils.getCurrentUser() == null ? "" : Utils.getCurrentUser().getLastName());
        result.put("shaparkUrl",commonService.getConfigValue("shpTsmBaseUrl"));
        result.put("inviteFriendMessage",String.format(commonService.getInviteFriendMessage(),userService.isAuthUser()? userService.getCurrentUserInfo().getAffiliateCode() :""));
        return result;
    }

    @Override
    public MobileAppVersion getLastActiveMobileAppVersion() {
        MobileAppVersion result = (MobileAppVersion) cacheService.getCacheValue("mobileAppVersion", "lastActiveMobileAppVersion");
        if (result != null)
            return result;

        Optional<MobileAppVersion> tmpResult= mobileAppVersionRepository.findLastActiveMobileAppVersion();
        if(tmpResult.isPresent())
            cacheService.putCacheValue("mobileAppVersion",  "lastActiveMobileAppVersion", tmpResult.get(),15,  TimeUnit.DAYS);
        return tmpResult.get();
    }

    @Override
    public Map<String, Object> getLastActiveMobileAppPaths() {
        Map<String, Object> result=new HashMap<>();
        MobileAppVersion mobileAppVersion=this.getLastActiveMobileAppVersion();
        if(mobileAppVersion!=null){
//            if(!Utils.isStringSafeEmpty(mobileAppVersion.getIosPath()))
                result.put("iosPath",mobileAppVersion.getIosPath());
//            if(!Utils.isStringSafeEmpty(mobileAppVersion.getAndroidPath()))
                result.put("androidPath",mobileAppVersion.getAndroidPath());
//            if(!Utils.isStringSafeEmpty(mobileAppVersion.getWebPath()))
                result.put("webPath",mobileAppVersion.getWebPath());
        }
        return result;
    }


    //#region  ContactUs
    @Override
    @Transactional
    public String addContactUs(CContactUs cContactUs, String ip, String agent, String machineId,String trackingCode) {
        Long userId=null;
        if ( userService.isAuthUser()) {
            userId = userService.getCurrentUser().getId();
            cContactUs.setName(Utils.getFullName(userService.getCurrentUser().getFirstName(), userService.getCurrentUser().getLastName()));
        }
        if (Utils.isStringSafeEmpty(cContactUs.getName()))
            cContactUs.setName(Utils.getMessageResource("global.guest"));

        if (/*userId==null && */(cContactUs.getEmail()==null  || cContactUs.getEmail().trim()=="") )
            throw  new InvalidDataException("Invalid Data","common.contactUs.email_required");

       /* if (userId==null && (cContactUs.getMobileNumber()==null  || cContactUs.getMobileNumber().trim()=="") )
            throw  new InvalidDataException("نقص اطلاعات","وارد نمودن شماره موبایل الزامی می باشد.");*/
        ContactUs contactUs = new ContactUs();
        contactUs.setContactTarget(((Integer)EContactTarget.valueOf(cContactUs.getContactTargetId().intValue()).value()).longValue());
        contactUs.setEmail(cContactUs.getEmail());
        contactUs.setMobileNumber(cContactUs.getMobileNumber());
        contactUs.setName(cContactUs.getName());
        contactUs.setUserId(userId);
        contactUs.setComment(cContactUs.getComment());
        contactUs.setIp(ip);
        contactUs.setAgent(agent);
        contactUs.setMachineId(machineId);
        contactUs.setLanguageId(MultiLingualDataContextHolder.getMultiLingualData().getCurrentLanguageId());
        contactUs.setTrackingCode(trackingCode);

        contactUs = contactUsRepository.save(contactUs);

        //Send Sms or email For Admin
        String message=cContactUs.getComment() + (!Utils.isStringSafeEmpty(contactUs.getEmail()) ? "\n" + Utils.getMessageResource("eNotifyType.email") +  " : " + contactUs.getEmail() : "") +  (!Utils.isStringSafeEmpty(contactUs.getMobileNumber())? "\n" + Utils.getMessageResource("eNotifyType.mobile") +  " : "+ contactUs.getMobileNumber() : "");
        this.sendNoticeToAdmin(this.getMySiteDomain() +": "+ Utils.getMessageResource("eTargetTypes.contact_us")  + " - " + contactUs.getName() ,Utils.getMessageResource("eTargetTypes.contact_us")  + " - " + contactUs.getName() ,message,ETargetTypes.CONTACT_US,contactUs.getId());
        // run On queue For Commit Like Meta
        // if (userId!=null) {
        // }
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Override
    public ResultListPageable<ContactUs> getContactUsList(Integer type, Integer start, Integer count) {
        if (type==0)
            return generatePageableResult(contactUsRepository.findAllContact(gotoPage(start,count+1)),count);
        else
            return generatePageableResult(contactUsRepository.findAllNoReplyContact(gotoPage(start,count+1)),count);
    }

    @Override
    public ContactUs getContactUsInfo(Long id) {
        return contactUsRepository.findByEntityId(id);
    }

    @Override
    public String replyToContactUs(CContactUsReply cContactUsReply) {
        ContactUs contactUs= contactUsRepository.findByEntityId(cContactUsReply.getContactUsId());
        if (contactUs == null)
            throw new ResourceNotFoundException(cContactUsReply.getContactUsId().toString(), "common.contactUs.id_notFound" , cContactUsReply.getContactUsId() );

        Date newDate=new Date();
        String dateStr =calendarService.getNormalDateTimeFormat(newDate);
        String reply=dateStr + "\n" + cContactUsReply.getReplyComment();
        if (Utils.isStringSafeEmpty(contactUs.getReply()))
            contactUs.setReply(reply);
        else
            contactUs.setReply(contactUs.getReply() + "\n" + reply);
        contactUs.setReplyDate(newDate);
        contactUsRepository.save(contactUs);
        if (cContactUsReply.getSendEmail()) {
            notificationService.createEmailNotification(newDate, contactUs.getEmail(), Utils.getMessageResource("common.contactUs.notify_subject"), reply,
                    Notification.Medium.EMAIL, Notification.Status.NEW, contactUs.getUserId(),
                    ((Integer) ETargetTypes.CONTACT_US.value()).longValue(), contactUs.getId(), null, null, null, null);
            ENotifyType eNotifyType=ENotifyType.valueOf(this.getNotifyType());
            if (cContactUsReply.getSendSms() && (eNotifyType== ENotifyType.MOBILE_EMAIL || eNotifyType==ENotifyType.MOBILE)) {
                if (contactUs.getUserId() != null) {
                    User contactUser = userService.getUserInfo(contactUs.getUserId());
                    if (contactUser != null && !Utils.isStringSafeEmpty(contactUser.getMobileNumber()))
                        notificationService.createSmsNotification(newDate, contactUser.getMobileNumber() /*contactUs.getMobileNumber()*/,
                                Utils.getMessageResource("common.contactUs.notify_subject"),
                                Utils.getMessageResource("common.contactUs.sms_message") +  short_domain_url /*contactUs.getReply()*/,
                                Notification.Medium.SMS, Notification.Status.NEW, contactUs.getUserId(),
                                ((Integer) ETargetTypes.CONTACT_US.value()).longValue(), contactUs.getId(), null, null, null, null);
                }
            }
        }
        return Utils.getMessageResource("global.operation.success_info");
    }

    //#endregion

    //#region  Feedback

    @Transactional
    @Override
    public String addFeedback(FeedbackDto feedbackDto, String ip, String agent, String machineId, String trackingCode) {
        if (!userService.isAuthUser())
            throw new AccessDeniedException(Utils.getMessageResource("global.login_required"));

        Feedback feedback = new Feedback();
        feedback.setUserId(Utils.getCurrentUserId());
        feedback.setComment(feedbackDto.getComment());
        feedback.setIp(ip);
        feedback.setAgent(agent);
        feedback.setMachineId(machineId);
        feedback.setLanguageId(MultiLingualDataContextHolder.getMultiLingualData().getCurrentLanguageId());
        feedback.setTrackingCode(trackingCode);

        feedback = feedbackRepository.save(feedback);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Override
    public ResultListPageable<Feedback> getAllFeedbackList(Integer start, Integer count) {
        return generatePageableResult(feedbackRepository.findAllFeedback(gotoPage(start,count+1)),count);
    }

    @Override
    public ResultListPageable<Feedback> getFeedbackList(Integer start, Integer count) {
        return this.getFeedbackList(Utils.getCurrentUserId(),start,count);
    }

    @Override
    public ResultListPageable<Feedback> getFeedbackList(Long userId, Integer start, Integer count) {
        return generatePageableResult(feedbackRepository.findAllFeedback(userId,gotoPage(start,count+1)),count);
    }

    @Override
    public Feedback getFeedbackInfo(Long feedbackId) {
        if (feedbackId == null)
            throw new InvalidDataException("Invalid Data", "common.feedback.id_required");

        Optional<Feedback> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = feedbackRepository.findByEntityId(feedbackId);
        else
            result = feedbackRepository.findByEntityId(feedbackId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.feedback.id_notFound");
        return result.get();
    }

    @Transactional
    @Override
    public String removeFeedback(Long feedbackId) {
        Feedback feedback = this.getFeedbackInfo(feedbackId);

        feedbackRepository.delete(feedback);
        return Utils.getMessageResource("global.operation.success_info");

    }

    //#endregion



    //#region  Site Theme
    @Override
    public SiteTheme getDefaultSiteThemeDetails() {
        SiteTheme siteTheme;
        if(MultiLingualDataContextHolder.getMultiLingualData()!=null && MultiLingualDataContextHolder.getMultiLingualData().getCurrentThemeId()!=null && MultiLingualDataContextHolder.getMultiLingualData().getCurrentThemeId()>0)
            return this.getActiveSiteThemeInfo(MultiLingualDataContextHolder.getMultiLingualData().getCurrentThemeId());

        if (configurationCacheEnable) {
            siteTheme = (SiteTheme) cacheService.getCacheValue("configuration", "defaultSiteThemeDetails");
            if (siteTheme != null)
                return siteTheme;
        }
        siteTheme=this.getSiteThemeInfo(this.getDefaultSiteTheme());
        if( siteTheme!=null) {
            siteTheme.setCss(htmlCompressor.compress(siteTheme.getCss()));
            if (configurationCacheEnable)
                cacheService.putCacheValue("configuration", "defaultSiteThemeDetails", siteTheme, 15, TimeUnit.DAYS);
        }
        return siteTheme;

    }



    @Override
    public SiteTheme getSiteThemeInfo(Long siteThemeId) {
        if (siteThemeId == null)
            throw new InvalidDataException("Invalid Data", "common.siteTheme.id_required");
        SiteTheme siteTheme =siteThemeRepository.findByEntityId(siteThemeId);
        if (siteTheme== null)
            throw new ResourceNotFoundException(siteThemeId.toString(), "common.siteTheme.id_notFoundByParam" , siteThemeId );
        return siteTheme;

    }

    @Override
    public SiteTheme getActiveSiteThemeInfo(Long siteThemeId) {
        if (siteThemeId == null)
            throw new InvalidDataException("Invalid Data", "common.siteTheme.id_required");
        SiteTheme siteTheme = siteThemeRepository.findActiveById(siteThemeId);
        if (siteTheme == null)
            throw new ResourceNotFoundException(siteThemeId.toString(), "common.siteTheme.id_notFoundByParam", siteThemeId);
        return siteTheme;
    }

    //#endregion


    //#region  Configuration
    public Configuration getConfigurationByName(String name) {
        Configuration configuration;
        String key=(String)(name!="activeLanguages" && name!="defaultLanguage" ? Utils.addGlobalCacheKey(name):name);
        if (configurationCacheEnable) {
            configuration = (Configuration) cacheService.getCacheValue("configuration",key);
            if (configuration != null)
                return configuration;
        }
        configuration= configurationRepository.findByName(name);
        if( configuration!=null && configurationCacheEnable) {
           cacheService.putCacheValue("configuration", key, configuration, 15, TimeUnit.DAYS);
        }/*else
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+name);*/
        return configuration;
    }

    @Override
    public void registerConfigModel(String name, EConfigurationType type, EConfigurationResultType resultType,String defaultValue){
        if(configurationModels.get(name)!=null)
            throw new InvalidDataException("Invalid Data", "Duplicate Configuration Name");
        configurationModels.put(name,ConfigurationModel.builder()
                                     .name(name).type(type).resultType(resultType)
                                     .defaultValue(defaultValue).build());
    }

    @Override
    public <T> T getConfigValue(String name) {
        ConfigurationModel configurationModel= configurationModels.get(name);
        if(configurationModel==null)
            throw new InvalidDataException("Invalid Data", "common.configuration." + name +"_notFound");

        Configuration configuration=this.getConfigurationByName(name);
        if (configuration==null && configurationModel.getDefaultValue()==null )
            throw new InvalidDataException("Invalid Data", "common.configuration." + name +"_notFound");
        else if(configuration==null)
            return (T) this.getValue(configurationModel.getDefaultValue(),this.getJavaClass(configurationModel.getResultType()));
        else
            return (T) this.getValue(configuration,this.getJavaClass(configurationModel.getResultType()));
    }

    private Class<?> getJavaClass(EConfigurationResultType resultType){
        if(EConfigurationResultType.INTEGER==resultType)
            return Integer.class;
        else if(EConfigurationResultType.LONG==resultType)
            return Long.class;
        else if(EConfigurationResultType.BOOLEAN==resultType)
            return Boolean.class;
        else if(EConfigurationResultType.STRING==resultType)
            return String.class;
        else
            return null;
    }
    private <T> T getValue(String val,Class<T> returnType){
        if(returnType == Integer.class){
            return (T) Integer.valueOf(val);
        }else if(returnType == Long.class) {
            return (T) Long.valueOf(val);
        }else if(returnType == Boolean.class) {
            return (T) Boolean.valueOf(val);
        }else if(returnType == String.class) {
            return (T) val;
        }else
            return null;
    }
    private <T> T getValue(Configuration configuration,Class<T> returnType){
         if(returnType == Integer.class){
             return (T) Integer.valueOf(configuration.getNumValue().intValue());
         }else if(returnType == Long.class) {
             return (T) configuration.getNumValue();
         }else if(returnType == Boolean.class) {
             return (T) Boolean.valueOf((configuration.getNumValue()==0)? false:true);
         }else if(returnType == String.class) {
             if(configuration.getEncrypted())
                 return (T) BaseUtils.deCrypt(configuration.getChrValue());
             else
                 return (T) configuration.getChrValue();



         }else
             return null;
     }



    @Override
    public List<ConfigurationType> getAllConfigs() {
        return configurationTypeRepository.findAllOrOrderById();
    }

    @Override
    public List<ConfigurationType> getConfigsTypeBySysName(String sysName) {
        //todo check read permision
        if (!hasPermission(sysName))
            throw new AccessDeniedException("global.accessDeniedMessage");
        return configurationTypeRepository.findBySysName(sysName);
    }

    @Override
    public Configuration getConfigurationInfoFromDb(String name) {
        Configuration configuration= configurationRepository.findByName(name);
        if (configuration==null)
            throw new ResourceNotFoundException(name + "Not Found","common.configuration.id_notfoundByParam" , name.trim() );
        return configuration;
    }

    @Override
    public Configuration editConfiguration(CConfiguration cConfiguration) {
         Configuration configuration=this.getConfigurationInfoFromDb(cConfiguration.getName());

        if (!hasPermission(configuration.getConfigurationType().getSysName()))
            throw new AccessDeniedException("global.accessDeniedMessage");

        if(!configuration.getEdit())
             return configuration;

        try {
            this.checkingConfigurationDataInModels(cConfiguration);
        }catch (ResourceNotFoundException e){
            this.checkingConfigurationData(cConfiguration);
        }

         if(configuration.getEncrypted() && !Utils.isStringSafeEmpty(cConfiguration.getChrVal()))
             configuration.setChrValue(BaseUtils.enCrypt(cConfiguration.getChrVal()));
         else
             configuration.setChrValue(cConfiguration.getChrVal());
         configuration.setNumValue(cConfiguration.getNumVal());
         configuration.setDesc(cConfiguration.getDesc());
         configuration.setModifyBy(BaseUtils.getCurrentUser().getId());
         configuration=configurationRepository.save(configuration);

         String key=(String)(cConfiguration.getName()!="activeLanguages" && cConfiguration.getName()!="defaultLanguage"? Utils.addGlobalCacheKey(cConfiguration.getName()):cConfiguration.getName());
         cacheService.deleteCacheValue("configuration",key);

        if(cConfiguration.getName().equalsIgnoreCase("defaultLanguage"))
           ((CookieLocaleResolver)localeResolver).setDefaultLocale(this.getDefaultLocale());

         if(cConfiguration.getName().equalsIgnoreCase("defaultTimeZone"))
           this.setDefaultTimeZone();

        multiLingualService.loadMultiLingualFields(configuration);
        return configuration;
    }

    private Map<String ,ConfigurationModel> configurationModels= new LinkedMap<>();


    private void checkingConfigurationDataInModels(CConfiguration cConfiguration){
        ConfigurationModel configurationModel=configurationModels.get(cConfiguration.getName());
        if(configurationModel==null)
            throw new ResourceNotFoundException(cConfiguration.getName(),"common.configuration.id_notfoundByParam" , cConfiguration.getName());

        if(configurationModel.getType()==EConfigurationType.POSITIVE_NUMBER && (cConfiguration.getNumVal()==null || cConfiguration.getNumVal()<=0)) {
            throw new InvalidDataException(cConfiguration.getName(),"common.configuration.numberVal_requiredByParam" , cConfiguration.getName() );
        }else if(configurationModel.getType()==EConfigurationType.ZERO_POSITIVE_NUMBER && (cConfiguration.getNumVal()==null || cConfiguration.getNumVal()<0)) {
            throw new InvalidDataException(cConfiguration.getName(),"common.configuration.numberVal_requirePositiveByParam" , cConfiguration.getName() );
        //}else if(configurationModel.getType()==EConfigurationType.NEGATIVE_NUMBER && (cConfiguration.getNumVal()==null || cConfiguration.getNumVal()>0)) {
        //}else if(configurationModel.getType()==EConfigurationType.ZERO_NEGATIVE_NUMBER && (cConfiguration.getNumVal()==null || cConfiguration.getNumVal()>0)) {
        }else if(configurationModel.getType()==EConfigurationType.BOOLEAN && (cConfiguration.getNumVal()==null || (cConfiguration.getNumVal()<0 || cConfiguration.getNumVal()>1))) {
            throw new InvalidDataException(cConfiguration.getName(),"common.configuration.numberVal_01rangeByParam" , cConfiguration.getName() );
        }else if(configurationModel.getType()==EConfigurationType.NOT_NULL_STRING && Utils.isStringSafeEmpty(cConfiguration.getChrVal())) {
            throw new InvalidDataException(cConfiguration.getName(),"common.configuration.stringVal_requiredByParam" , cConfiguration.getName() );
        }else if(configurationModel.getType()==EConfigurationType.NULL_STRING && Utils.isStringSafeEmpty(cConfiguration.getChrVal())) {
            cConfiguration.setChrVal("");
        }

    }
    private void checkingConfigurationData(CConfiguration cConfiguration){
        switch (cConfiguration.getName()) {
            case "notifyType": case  "loginType": case "loginUniqueCodeType": case "uniqueCodeValidationType": case "passwordValidityDay":
            case "userConcurrentLoginCount": case "userConcurrentStrategy" : case "allowedIdleTime":
            case "maxAttemptForLockLevel1" : case "lockMinuteLevel1": case "maxAttemptForLockLevel2": case "lockMinuteLevel2": case "maxAttemptForAlwaysLock":
            case "apiCallDurationLimit": case "apiCallCountLimit": case "mciProductList_CacheTime" : case "internetPackageList_CacheTime" :
                if(cConfiguration.getNumVal()==null || cConfiguration.getNumVal()<0)
                    throw new InvalidDataException(cConfiguration.getName(),"common.configuration.numberVal_requirePositiveByParam" , cConfiguration.getName() );
                break;
            case "financialUserId": case "maxLinkBuilding": case "minRequestRefundMoney":
            case "defaultSiteTheme": case "defaultLanguage": case "defaultCurrency":
            case "calendarType" : case "verificationCodeLiveTimeSecond" :
            case "registerUserLiveTimeSecond" : case "operationRequestWaitMinuteTime": case "defaultQueryCount":
                if(cConfiguration.getNumVal()==null || cConfiguration.getNumVal()<=0)
                    throw new InvalidDataException(cConfiguration.getName(),"common.configuration.numberVal_requiredByParam" , cConfiguration.getName() );
                break;
            case "adminMessageForContactUs": case "adminMessageForError":  case "sendUserActionSms":
            case "affiliateState": case "mobileIsRequired": case "emailIsRequired":
            case "storeRegisterCommissionStatus" :
            case "force2FAuthentication": case "forceChangePasswordAfterReset": case "jwtTokenCryptStatus":
            case "tokenIpCheck": case "refreshTokenIpCheck": case "externalDepositRequestStatus":
                if(cConfiguration.getNumVal()==null || (cConfiguration.getNumVal()<0 || cConfiguration.getNumVal()>1))
                    throw new InvalidDataException(cConfiguration.getName(),"common.configuration.numberVal_01rangeByParam" , cConfiguration.getName() );
                break;
            case "robotTxt":
            case "adminMobileNumbers": case "adminEmailsAddress" : case  "myBrandName":
            case "myBrandEName": case "mySiteTitle": case  "mySiteDescription":
            case "mySiteDomain": case "samanBankTerminalId": case "samanBankPassword" : case "resourceVersion" :
            case "validActiveBankGateways" : case "mobilePattern" : case "jwtTokenKey" :
            case "inviteFriendMessage" :
                if(Utils.isStringSafeEmpty(cConfiguration.getChrVal()))
                    throw new InvalidDataException(cConfiguration.getName(),"common.configuration.stringVal_requiredByParam" , cConfiguration.getName() );
                break;
            case "shahkarPrivetKey":  case "shahkarHashAlgorithm":
            case "samatIpgBaseUrl": case "samatIpgPrivateKey" : case "samatIpgIdentity": case "samatIpgHashAlgorithm" :
            case "aboutApp":
                if(Utils.isStringSafeEmpty(cConfiguration.getChrVal()))
                    cConfiguration.setChrVal("");
                break;
            case  "logo": case "favIcon": case "registerRolesPage": case "defaultTimeZone":
            case "mandatoryFinanceDestNumExp": case "fixMetaHeaders":
                if(Utils.isStringSafeEmpty(cConfiguration.getChrVal()))
                    cConfiguration.setChrVal("");
                break;
            default:
                throw new ResourceNotFoundException(cConfiguration.getName(),"common.configuration.id_notfoundByParam" , cConfiguration.getName());
                //break;
        }
    }

    @Override
    public Long getFinancialUserId() {
        Configuration configuration=this.getConfigurationByName("financialUserId");
        if (configuration==null)
             throw new ResourceNotFoundException("financialUserId","common.configuration.financialUserId_notFound");
        return configuration.getNumValue();
    }


    @Override
    public String getRobotTxt() {
        Configuration configuration=this.getConfigurationByName("robotTxt");
        if (configuration==null)
            throw new ResourceNotFoundException("robotTxt","common.configuration.robotTxt_notFound");
        return configuration.getChrValue();
    }


    @Override
    public String getValidActiveBankGateways() {
        Configuration configuration=this.getConfigurationByName("validActiveBankGateways");
        if (configuration==null)
            throw new ResourceNotFoundException("validActiveBankGateways","common.configuration.validActiveBankGateways_notFound");
        return configuration.getChrValue();
    }

    @Override
    public List<EBank> getValidActiveBanks() {
        String validActiveBankGateways=this.getValidActiveBankGateways();
        String validActiveBankGatewaysArray[]=validActiveBankGateways.split(",");
        if (validActiveBankGatewaysArray.length<=0)
            throw new BankPaymentException("Bank List Empty", "eBank.active.notFound");
        List<EBank> banks=new ArrayList<>();
        for (int i = 0; i < validActiveBankGatewaysArray.length; i++) {
            banks.add(EBank.valueOf(validActiveBankGatewaysArray[i].toUpperCase()));
        }
        return banks;
    }

    @Override
    public String getSamanBankTerminalId() {
        Configuration configuration=this.getConfigurationByName("samanBankTerminalId");
        if (configuration==null)
            throw new ResourceNotFoundException("samanBankTerminalId","common.configuration.samanBankTerminalId_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getSamanBankPassword() {
        Configuration configuration=this.getConfigurationByName("samanBankPassword");
        if (configuration==null)
            throw new ResourceNotFoundException("samanBankPassword","common.configuration.samanBankPassword_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getAdminMobileNumbers() {
        Configuration configuration=this.getConfigurationByName("adminMobileNumbers");
        if (configuration==null)
            throw new ResourceNotFoundException("adminMobileNumbers","common.configuration.adminMobileNumbers_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getAdminEmailsAddress() {
        Configuration configuration=this.getConfigurationByName("adminEmailsAddress");
        if (configuration==null)
            throw new ResourceNotFoundException("adminEmailsAddress","common.configuration.adminEmailsAddress_notFound");
        return configuration.getChrValue();

    }

    @Override
    public Boolean getAdminMessageForContactUs() {
        Configuration configuration=this.getConfigurationByName("adminMessageForContactUs");
        if (configuration==null)
            return false;
            //throw new ResourceNotFoundException("adminMessageForContactUs","common.configuration.adminMessageForContactUs_notFound");
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public Boolean getAdminMessageForError() {
        Configuration configuration=this.getConfigurationByName("adminMessageForError");
        if (configuration==null)
            return false;
            //throw new ResourceNotFoundException("adminMessageForError","common.configuration.adminMessageForError_notFound");
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public Boolean getSendUserActionSms() {
        Configuration configuration=this.getConfigurationByName("sendUserActionSms");
        if (configuration==null)
            return false;
            //throw new ResourceNotFoundException("sendUserActionSms","common.configuration.sendUserActionSms_notFound");
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public Double getMinRequestRefundMoney() {
        Configuration configuration=this.getConfigurationByName("minRequestRefundMoney");
        if (configuration==null)
            return 1d;
            //throw new ResourceNotFoundException("minRequestRefundMoney","common.configuration.minRequestRefundMoney_notFound");
        return configuration.getNumValue().doubleValue();
    }


    @Override
    public String getMyBrandName() {
        Configuration configuration=this.getConfigurationByName("myBrandName");
        if (configuration==null)
            return "No Name";
        return configuration.getChrValue();
    }

    @Override
    public String getMyBrandEName() {
        Configuration configuration=this.getConfigurationByName("myBrandEName");
        if (configuration==null)
            return "No Name";
        return configuration.getChrValue();
    }

    @Override
    public String getMySiteTitle() {
        Configuration configuration=this.getConfigurationByName("mySiteTitle");
        if (configuration==null)
            throw new ResourceNotFoundException("mySiteTitle","common.configuration.mySiteTitle_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getMySiteDescription() {
        Configuration configuration=this.getConfigurationByName("mySiteDescription");
        if (configuration==null)
            throw new ResourceNotFoundException("mySiteDescription","common.configuration.mySiteDescription_notFound");
        return configuration.getChrValue();
    }


    @Override
    public String getMySiteDomain() {
        Configuration configuration=this.getConfigurationByName("mySiteDomain");
        if (configuration==null)
            throw new ResourceNotFoundException("mySiteDomain","common.configuration.mySiteDomain_notFound");
        return configuration.getChrValue();
    }



    @Override
    public Long getDefaultSiteTheme() {
        if(MultiLingualDataContextHolder.getMultiLingualData()!=null && MultiLingualDataContextHolder.getMultiLingualData().getCurrentThemeId()!=null && MultiLingualDataContextHolder.getMultiLingualData().getCurrentThemeId()>0)
           return MultiLingualDataContextHolder.getMultiLingualData().getCurrentThemeId();


        Configuration configuration=this.getConfigurationByName("defaultSiteTheme");
        if (configuration==null || Utils.isSafeEmpty(configuration.getNumValue()))
            throw new ResourceNotFoundException("defaultSiteTheme","common.configuration.defaultSiteTheme_notFound");
        return configuration.getNumValue();
    }

    @Override
    public String getLogoUrl() {
        Configuration configuration=this.getConfigurationByName("logo");
        if (configuration==null)
            return "No Logo";
            //throw new ResourceNotFoundException("logo","common.configuration.logo_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getResourceVersion() {
        Configuration configuration=this.getConfigurationByName("resourceVersion");
        if (configuration==null)
            return "1.0.0";
            //throw new ResourceNotFoundException("resourceVersion","common.configuration.resourceVersion_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getFavIconUrl() {
        Configuration configuration=this.getConfigurationByName("favIcon");
        if (configuration==null)
            return "/assets/favicon.ico";
            //throw new ResourceNotFoundException("favIcon","common.configuration.favIcon_notFound");
        return configuration.getChrValue();
    }

    @Override
    public Long getAffiliateState() {
        Configuration configuration=this.getConfigurationByName("affiliateState");
        if (configuration==null)
            throw new ResourceNotFoundException("affiliateState","common.configuration.affiliateState_notFound");
        return configuration.getNumValue();
    }


    @Override
    public Long getDefaultLanguage() {
        return baseLanguageId;
    }

    @Override
    public Long getDefaultCurrency() {
        Configuration configuration=this.getConfigurationByName("defaultCurrency");
        if (configuration==null)
            throw new ResourceNotFoundException("defaultCurrency","common.configuration.defaultCurrency_notFound");
        return configuration.getNumValue();
    }

    @Override
    public Long getPanelCurrency() {
        Configuration configuration=this.getConfigurationByName("panelCurrency");
        if (configuration==null)
            throw new ResourceNotFoundException("panelCurrency","common.configuration.panelCurrency_notFound");
        return configuration.getNumValue();
    }

    @Override
    public Long getCalendarType() {
        Configuration configuration=this.getConfigurationByName("calendarType");
        if (configuration==null)
            throw new ResourceNotFoundException("calendarType","common.configuration.calendarType_notFound");
        return configuration.getNumValue();
    }

    @Override
    public Long getNotifyType() {
        Configuration configuration=this.getConfigurationByName("notifyType");
        if (configuration==null)
            throw new ResourceNotFoundException("notifyType","common.configuration.notifyType_notFound");
        return configuration.getNumValue();
    }

    @Override
    public Long getLoginType() {
        Configuration configuration=this.getConfigurationByName("loginType");
        if (configuration==null)
            throw new ResourceNotFoundException("loginType","common.configuration.loginType_notFound");
        return configuration.getNumValue();
    }


    @Override
    public String getRegisterRolesPage() {
        Configuration configuration=this.getConfigurationByName("registerRolesPage");
        if (configuration==null)
            throw new ResourceNotFoundException("registerRolesPage","common.configuration.registerRolesPage_notFound");
        return configuration.getChrValue();
    }

    @Override
    public String getDefaultTimeZone() {
        Configuration configuration=this.getConfigurationByName("defaultTimeZone");
        if (configuration==null)
            return "";
        return configuration.getChrValue();
    }

    @Override
    public void setDefaultTimeZone() {
        String defaultTimeZone=this.getDefaultTimeZone();
        if(!Utils.isStringSafeEmpty(defaultTimeZone))
            calendarService.setTimeZone(com.ibm.icu.util.TimeZone.getTimeZone(defaultTimeZone));
    }

    @Override
    public String getFixMetaHeaders() {
        Configuration configuration=this.getConfigurationByName("fixMetaHeaders");
        if (configuration==null)
            return "";
        return configuration.getChrValue();
    }


    @Override
    public String getMandatoryFinanceDestNumExp() {
        Configuration configuration=this.getConfigurationByName("mandatoryFinanceDestNumExp");
        if (configuration==null)
            throw new ResourceNotFoundException("mandatoryFinanceDestNumExp","common.configuration.mandatoryFinanceDestNumExp_notFound");
        return configuration.getChrValue();
    }

    @Override
    public Boolean getMobileIsRequired() {
        Configuration configuration=this.getConfigurationByName("mobileIsRequired");
        if (configuration==null)
            throw new ResourceNotFoundException("mobileIsRequired","common.configuration.mobileIsRequired_notFound");
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public Boolean getEmailIsRequired() {
        Configuration configuration=this.getConfigurationByName("emailIsRequired");
        if (configuration==null)
            throw new ResourceNotFoundException("emailIsRequired","common.configuration.emailIsRequired_notFound");
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public String getMobilePattern() {
        Configuration configuration=this.getConfigurationByName("mobilePattern");
        if (configuration==null)
            throw new ResourceNotFoundException("mobilePattern","common.configuration.mobilePattern_notFound");
        return configuration.getChrValue();
    }



    @Override
    public Integer getVerificationCodeLiveTimeSecond() {
        Configuration configuration=this.getConfigurationByName("verificationCodeLiveTimeSecond");
        if (configuration==null)
            return 120;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getRegisterUserLiveTimeSecond() {
        Configuration configuration=this.getConfigurationByName("registerUserLiveTimeSecond");
        if (configuration==null)
            return 360;
        return configuration.getNumValue().intValue();
    }

    @Override
    public String getInviteFriendMessage() {
        Configuration configuration=this.getConfigurationByName("inviteFriendMessage");
        if (configuration==null)
            return "برای نصب جات به آدرس زیر مراجه نمایید.\\n {0} \\n کد معرف: {1}";
        return this.getConfigurationValue(configuration);
    }

    @Override
    public EUniqueCodeType loginUniqueCodeType() {
        Configuration configuration=this.getConfigurationByName("loginUniqueCodeType");
        if (configuration==null)
            return EUniqueCodeType.NONE;
        return EUniqueCodeType.valueOf(configuration.getNumValue().intValue());
    }



    @Override
    public Boolean getForce2FAuthentication() {
        Configuration configuration=this.getConfigurationByName("force2FAuthentication");
        if (configuration==null)
            return false;
        return configuration.getNumValue()>0 ? true : false;

    }


    @Override
    public Integer getUniqueCodeValidationType() {
        Configuration configuration=this.getConfigurationByName("uniqueCodeValidationType");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public String getShahkarPrivetKey() {
        Configuration configuration=this.getConfigurationByName("shahkarPrivetKey");
        if (configuration==null)
            return "";
        return this.getConfigurationValue(configuration);
    }

    @Override
    public String getShahkarHashAlgorithm() {
        Configuration configuration=this.getConfigurationByName("shahkarHashAlgorithm");
        if (configuration==null || Utils.isStringSafeEmpty(configuration.getChrValue()))
            return "SHA1withRSA";
        return this.getConfigurationValue(configuration);

    }

    //#region  Security
    @Override
    public Integer getPasswordValidityDay() {
        Configuration configuration=this.getConfigurationByName("passwordValidityDay");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Boolean getForceChangePasswordAfterReset() {
        Configuration configuration=this.getConfigurationByName("forceChangePasswordAfterReset");
        if (configuration==null)
            return false;
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public Integer getUserConcurrentLoginCount() {
        Configuration configuration=this.getConfigurationByName("userConcurrentLoginCount");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public EUserConcurrentStrategy getUserConcurrentStrategy() {
        Configuration configuration=this.getConfigurationByName("userConcurrentStrategy");
        if (configuration==null)
            return EUserConcurrentStrategy.LOGIN_DENY;
        return EUserConcurrentStrategy.valueOf(configuration.getNumValue().intValue());
    }

    @Override
    public Integer getAllowedIdleTime() {
        Configuration configuration=this.getConfigurationByName("allowedIdleTime");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public String getJwtTokenKey() {
        Configuration configuration=this.getConfigurationByName("jwtTokenKey");
        if (configuration==null)
            throw new ResourceNotFoundException("jwtTokenKey","common.configuration.jwtTokenKey_notFound");
        return this.getConfigurationValue(configuration);
    }

    @Override
    public Boolean getJwtTokenCryptStatus() {
        Configuration configuration=this.getConfigurationByName("jwtTokenCryptStatus");
        if (configuration==null)
            return false;
        return configuration.getNumValue()>0 ? true : false;

    }

    @Override
    public Integer getMaxAttemptForLockLevel1() {
        Configuration configuration=this.getConfigurationByName("maxAttemptForLockLevel1");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getLockMinuteLevel1() {
        Configuration configuration=this.getConfigurationByName("lockMinuteLevel1");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getMaxAttemptForLockLevel2() {
        Configuration configuration=this.getConfigurationByName("maxAttemptForLockLevel2");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getLockMinuteLevel2() {
        Configuration configuration=this.getConfigurationByName("lockMinuteLevel2");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getMaxAttemptForAlwaysLock() {
        Configuration configuration=this.getConfigurationByName("maxAttemptForAlwaysLock");
        if (configuration==null)
            return 10;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getApiCallDurationLimit() {
        Configuration configuration=this.getConfigurationByName("apiCallDurationLimit");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getApiCallCountLimit() {
        Configuration configuration=this.getConfigurationByName("apiCallCountLimit");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    //#endregion Security

    //#region  TopUp Config

    @Override
    public Integer getMciProductList_CacheTime() {
        Configuration configuration=this.getConfigurationByName("mciProductList_CacheTime");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Integer getInternetPackageList_CacheTime() {
        Configuration configuration=this.getConfigurationByName("internetPackageList_CacheTime");
        if (configuration==null)
            return 0;
        return configuration.getNumValue().intValue();
    }

    //#endregion TopUp Config





    //#region  Samat IPG
    @Override
    public String getSamatIpgBaseUrl() {
        Configuration configuration=this.getConfigurationByName("samatIpgBaseUrl");
        if (configuration==null)
            return "https://pay.samatco.ir:8080/api/pardakht";
        return this.getConfigurationValue(configuration);
    }

    @Override
    public String getSamatIpgIdentity() {
        Configuration configuration=this.getConfigurationByName("samatIpgIdentity");
        if (configuration==null)
            return "";
        return this.getConfigurationValue(configuration);
    }

    @Override
    public String getSamatIpgPrivateKey() {
        Configuration configuration=this.getConfigurationByName("samatIpgPrivateKey");
        if (configuration==null)
            return "";
        return this.getConfigurationValue(configuration);
    }

/*    @Override
    public String getSamatIpgTerminalId() {
        Configuration configuration=this.getConfigurationByName("samatIpgTerminalId");
        if (configuration==null)
            return "000";
        return this.getConfigurationValue(configuration);
    }

    @Override
    public String getSamatIpgMerchantId() {
        Configuration configuration=this.getConfigurationByName("samatIpgMerchantId");
        if (configuration==null)
            return "";
        return this.getConfigurationValue(configuration);
    }*/

    @Override
    public String getSamatIpgHashAlgorithm() {
        Configuration configuration=this.getConfigurationByName("samatIpgHashAlgorithm");
        if (configuration==null || Utils.isStringSafeEmpty(configuration.getChrValue()))
            return "HmacSHA512";
        return this.getConfigurationValue(configuration);
    }

    //#endregion  Samat IPG

    @Override
    public Integer getOperationRequestWaitMinuteTime() {
        Configuration configuration=this.getConfigurationByName("operationRequestWaitMinuteTime");
        if (configuration==null ||configuration.getNumValue().intValue()<5)
            return 10;
        return configuration.getNumValue().intValue();
    }

    @Override
    public String getAboutApp() {
        Configuration configuration=this.getConfigurationByName("aboutApp");
        if (configuration==null || Utils.isStringSafeEmpty(configuration.getChrValue()))
            return "";
        return this.getConfigurationValue(configuration);
    }

    @Override
    public Boolean tokenIpCheck() {
        Configuration configuration=this.getConfigurationByName("tokenIpCheck");
        if (configuration==null)
            return true;
        return configuration.getNumValue()>0 ? true : false;
    }

    @Override
    public Boolean refreshTokenIpCheck() {
        Configuration configuration=this.getConfigurationByName("refreshTokenIpCheck");
        if (configuration==null)
            return true;
        return configuration.getNumValue()>0 ? true : false;
    }


    @Override
    public Integer getDefaultQueryCount() {
        Configuration configuration=this.getConfigurationByName("defaultQueryCount");
        if (configuration==null )
            return 100;
        return configuration.getNumValue().intValue();
    }

    @Override
    public Boolean getExternalDepositRequestStatus() {
        Configuration configuration=this.getConfigurationByName("externalDepositRequestStatus");
        if (configuration==null)
            return true;
        return configuration.getNumValue()>0 ? true : false;
    }


    private String getConfigurationValue(Configuration configuration){
        try{
            if(configuration.getEncrypted())
                return BaseUtils.deCrypt(configuration.getChrValue());
            else
                return configuration.getChrValue();
        }catch (Exception e){
            return configuration.getChrValue();
        }
    }

    //#endregion


    //#region Domain, Language And Currency


    @Override
    public List<DomainWrapper> getActiveDomainWrappersByUrl(String domainUrl) {
        if(Utils.isStringSafeEmpty(domainUrl))
            return new ArrayList<>();
        List<DomainWrapper> domainWrappers;
        String cacheKey="allDomainFor_"+domainUrl;
        if (configurationCacheEnable) {
            domainWrappers = (List<DomainWrapper>) cacheService.getCacheValue("domains", cacheKey);
            if (domainWrappers != null)
                return domainWrappers;
        }
        domainWrappers =domainRepository.getActiveDomainWrappersByUrl(domainUrl);
        if (configurationCacheEnable && domainWrappers!=null)
            cacheService.putCacheValue("domains", cacheKey, domainWrappers, 30, TimeUnit.DAYS);
        return domainWrappers;
    }

    @Override
    public DomainWrapper getCurrentActiveDomainWrapperByUrl(String domainUrl) {
        if(Utils.isStringSafeEmpty(domainUrl))
            return null;
        DomainWrapper domainWrapper;
        if (configurationCacheEnable) {
            domainWrapper = (DomainWrapper) cacheService.getCacheValue("domains", domainUrl);
            if (domainWrapper != null)
                return domainWrapper;
        }
        domainWrapper =domainRepository.getTop1ActiveDomainWrapperByUrl(domainUrl);
        if (configurationCacheEnable && domainWrapper!=null)
            cacheService.putCacheValue("domains", domainUrl, domainWrapper, 30, TimeUnit.DAYS);
        return domainWrapper;
    }

    @Override
    public DomainWrapper getDefaultActiveDomainWrapper() {
        DomainWrapper domainWrapper;
        if (configurationCacheEnable) {
            domainWrapper = (DomainWrapper) cacheService.getCacheValue("domains", "defaultActiveDomain");
            if (domainWrapper != null)
                return domainWrapper;
        }
        domainWrapper =domainRepository.getTop1ActiveDomainWrapper();
        if (configurationCacheEnable && domainWrapper!=null)
            cacheService.putCacheValue("domains", "defaultActiveDomain", domainWrapper, 30, TimeUnit.DAYS);
        return domainWrapper;
    }

    @Override
    public Language getLanguageInfo(Long languageId) {
        if (languageId == null)
            throw new InvalidDataException("Invalid Data", "common.language.id_required");
        Language language =languageRepository.findByEntityId(languageId);
        if (language == null)
            throw new ResourceNotFoundException(language.toString(), "common.language.id_notfoundByParam",languageId);
        return language;
    }

    @Override
    public Language getDefaultLanguageInfo() {
        Language result;
        if (configurationCacheEnable) {
            result = (Language) cacheService.getCacheValue("languages", "defaultLanguage");
            if (result != null)
                return result;
        }
        result = this.getLanguageInfo(this.getDefaultLanguage());
        if (configurationCacheEnable && result!=null)
            cacheService.putCacheValue("languages", "defaultLanguage", result, 30, TimeUnit.DAYS);
        return result;
    }

    @Override
    public Language getLanguageInfo(String shortName) {
        if (Utils.isStringSafeEmpty(shortName))
            throw new InvalidDataException("Invalid Data", "common.language.shortName_required");
        Language language =languageRepository.findByShortName(shortName);
        if (language == null)
            throw new ResourceNotFoundException(language.toString(), "common.language.shortName_notfound",shortName);
        return language;
    }


    @Override
    public Language getActiveLanguageInfo(Long languageId) {
        if (languageId == null)
            throw new InvalidDataException("Invalid Data", "common.language.id_required");

        Language language;
        if (configurationCacheEnable) {
            language = (Language) cacheService.getCacheValue("languages", languageId);
            if (language != null)
                return language;
        }
        language =languageRepository.findActiveById(languageId);
        if (configurationCacheEnable && language!=null)
            cacheService.putCacheValue("languages", languageId, language, 30, TimeUnit.DAYS);
        return language;
    }

    @Override
    public Language getActiveLanguageInfo(String shortName) {
        if (Utils.isStringSafeEmpty(shortName))
            throw new InvalidDataException("Invalid Data", "common.language.shortName_required");

        Language language;
        if (configurationCacheEnable) {
            language = (Language) cacheService.getCacheValue("languages", shortName);
            if (language != null)
                return language;
        }
        language =languageRepository.findActiveByShortName(shortName);
        if (configurationCacheEnable && language!=null)
            cacheService.putCacheValue("languages", shortName, language, 30, TimeUnit.DAYS);
        return language;
    }

    @Override
    public List<Language> getActiveLanguages() {
        List<Language> languages;
        if (configurationCacheEnable) {
            languages = (List<Language>) cacheService.getCacheValue("languages", "activeLanguages");
            if (languages != null)
                return languages;
        }
        languages =languageRepository.findAllActive();
        if (configurationCacheEnable && languages!=null)
            cacheService.putCacheValue("languages", "activeLanguages", languages, 30, TimeUnit.DAYS);
        return languages;
    }

    @Override
    public Language getCurrentLanguageInfo() {
        Language language =  this.getActiveLanguageInfo(LocaleContextHolder.getLocale().toString());
        if(language == null)
            language =this.getActiveLanguageInfo(this.getDefaultLanguage());

        return language;
    }


    @Override
    public Locale getDefaultLocale() {
        Language defaultLang=this.getDefaultLanguageInfo();
        String[] langs= defaultLang.getShortName().split("_");
        return new Locale(langs[0],langs[1]);
    }

    @Override
    public Currency getCurrencyInfo(Long currencyId) {
        if (currencyId == null)
            throw new InvalidDataException("Invalid Data", "common.currency.id_required");
        Currency currency =currencyRepository.findByEntityId(currencyId);
        if (currency == null)
            throw new ResourceNotFoundException(currency.toString(), "common.currency.id_notfoundByParam",currencyId);
    return currency;
    }

    @Override
    public Currency getActiveCurrencyInfo(Long currencyId) {
        if (currencyId == null)
            throw new InvalidDataException("Invalid Data", "common.currency.id_required");
        Currency currency;
        if (configurationCacheEnable) {
            currency = (Currency) cacheService.getCacheValue("currencies", Utils.addGlobalCacheKey(currencyId));
            if (currency != null)
                return currency;
        }

        currency =currencyRepository.findActiveById(currencyId);
        if (configurationCacheEnable && currency!=null)
            cacheService.putCacheValue("currencies", Utils.addGlobalCacheKey(currencyId), currency, 30, TimeUnit.DAYS);

        return currency;
    }

    @Override
    public Integer getActiveCurrencyRndNumCount(Long currencyId) {
        Currency currency=this.getActiveCurrencyInfo(currencyId);
        if(currency!=null && currency.getRndNumCount()!=null)
            return currency.getRndNumCount();
        return 0;
    }

    @Override
    public String getActiveCurrencyShortName(Long currencyId) {
        Currency currency=this.getActiveCurrencyInfo(currencyId);
        if(currency!=null && !Utils.isStringSafeEmpty(currency.getShortName()))
            return currency.getShortName();
        return "";

    }

    //#endregion



    //#region Admin Notices


    @Override
    public String sendNoticeToAdmin(String subject, String smsMessage, String emailMessage, ETargetTypes eTargetTypes, Long targetId) {
        return sendNoticeToAdmin(subject,smsMessage,emailMessage,eTargetTypes,targetId,this.getAdminMobileNumbers(),this.getAdminEmailsAddress()) ;
    }

    @Override
    public String sendNoticeToAdmin(String subject,String smsMessage, String emailMessage,ETargetTypes eTargetTypes, Long targetId,String adminMobileNumbers,String adminEmailsAddress) {
        try {
            if(eTargetTypes==ETargetTypes.CONTACT_US && !this.getAdminMessageForContactUs())
                return  null;
            else if(eTargetTypes==ETargetTypes.HOME && !this.getAdminMessageForError())
                return  null;

            String[] adminMobileNumber = adminMobileNumbers.split(",");
            ENotifyType eNotifyType=ENotifyType.valueOf(this.getNotifyType());
            for (int i = 0; i < adminMobileNumber.length; i++) {
                if (eNotifyType==ENotifyType.MOBILE_EMAIL || eNotifyType==ENotifyType.MOBILE) {
                    notificationService.createSmsNotification(new Date(), adminMobileNumber[i],
                            subject, smsMessage,
                            Notification.Medium.SMS, Notification.Status.NEW, null,
                            ((Integer) eTargetTypes.value()).longValue(), targetId, null, null, null, null);
                }
            }

            String[] adminEmailAddress = adminEmailsAddress.split(",");
            for (int i = 0; i < adminEmailAddress.length; i++) {
                if (eNotifyType==ENotifyType.MOBILE_EMAIL || eNotifyType==ENotifyType.EMAIL) {
                    notificationService.createEmailNotification(new Date(), adminEmailAddress[i],
                            subject, emailMessage,
                            Notification.Medium.EMAIL, Notification.Status.NEW, null,
                            ((Integer) eTargetTypes.value()).longValue(), targetId, null, null, null, null);
                }
            }

            return Utils.getMessageResource("global.insert_info");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //#endregion



    //#region Path Limitation for fake visit and crawler

    @Override
    public List<PathLimitation> getLimitationPathConfigs() {
        List<PathLimitation> pathLimitationConfigs;
        if (configurationCacheEnable) {
            pathLimitationConfigs = (List<PathLimitation>) cacheService.getCacheValue("configuration", "pathLimitationConfig");
            if (pathLimitationConfigs != null)
                return pathLimitationConfigs;
        }
        pathLimitationConfigs= pathLimitationRepository.findAllByActiveTrueOrderByPriorityDesc();
        if (configurationCacheEnable && pathLimitationConfigs!=null /*&& pathLimitationConfigs.size()>0*/)
            cacheService.putCacheValue("configuration", "pathLimitationConfig", pathLimitationConfigs,15, TimeUnit.DAYS);
        return pathLimitationConfigs;
    }

    @Override
    public List<String> getLimitationPaths() {
        List<String> pathLimitations;
        if (configurationCacheEnable) {
            pathLimitations = (List<String>) cacheService.getCacheValue("configuration", "pathLimitations");
            if (pathLimitations != null)
                return pathLimitations;
        }
        pathLimitations= pathLimitationRepository.findAllActivePaths();
        if (configurationCacheEnable && pathLimitations!=null)
            cacheService.putCacheValue("configuration", "pathLimitations", pathLimitations,15, TimeUnit.DAYS);
        return pathLimitations;
    }

    @Override
    public List<String> getBlockedIps() {
        List<String> blockedIps;
        if (configurationCacheEnable) {
            blockedIps = (List<String>) cacheService.getCacheValue("configuration", "blockedIps");
            if (blockedIps != null)
                return blockedIps;
        }
        blockedIps= blockedIpRepository.findAllActiveBlockedIps();
        if (configurationCacheEnable && blockedIps!=null /*&& blockedIps.size()>0*/)
            cacheService.putCacheValue("configuration", "blockedIps", blockedIps,15, TimeUnit.DAYS);
        return blockedIps;
    }

    @Override
    public List<AllowedIpPath> getActiveAllowedIpPaths() {
        List<AllowedIpPath> activeAllowedIpPaths;
        if (configurationCacheEnable) {
            activeAllowedIpPaths = (List<AllowedIpPath>) cacheService.getCacheValue("configuration", "activeAllowedIpPaths");
            if (activeAllowedIpPaths != null)
                return activeAllowedIpPaths;
        }
        activeAllowedIpPaths=allowedIpPathRepository.findActiveAllowedIpPaths();
        if (configurationCacheEnable && activeAllowedIpPaths!=null )
            cacheService.putCacheValue("configuration", "activeAllowedIpPaths", activeAllowedIpPaths,15, TimeUnit.DAYS);
        return activeAllowedIpPaths;
    }

    @Override
    public List<AllowedIpPath> getAllAllowedIpPaths() {
        return allowedIpPathRepository.findAllAllowedIpPaths();
    }


    //#endregion

    //#region   City And Region

    @Override
    public List<Province> getProvinceList() {
        List<Province> result = (List<Province>) cacheService.getCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("allProvinceList"));
        if (result != null)
            return result;
        result= provinceRepository.findAll();
        cacheService.putCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("allProvinceList"), result,15,  TimeUnit.DAYS);
        return result;
    }

    @Override
    public List<Province> getActiveProvinceList() {
        List<Province> result = (List<Province>) cacheService.getCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("activeProvinceList"));
        if (result != null)
            return result;
        result= provinceRepository.findAllActive();
        cacheService.putCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("activeProvinceList"), result,15,  TimeUnit.DAYS);
        return result;
    }

    @Override
    public Province getProvinceInfo(Long provinceId) {
        Province province=provinceRepository.findByEntityId(provinceId);
        if (province == null)
            throw new ResourceNotFoundException(provinceId.toString() ,"common.province.id_notFound" , provinceId );

        return province;
    }


    @Override
    public Province getActiveProvinceInfo(Long provinceId) {
        Province province=provinceRepository.findActiveById(provinceId);
        if (province == null)
            throw new ResourceNotFoundException(provinceId.toString() ,"common.province.id_notFound" , provinceId );
        return province;
    }

    @Override
    public Province getActiveProvinceInfo(Long provinceId, Long countryId) {
        Province province=provinceRepository.findActiveByIdAndCountryId(provinceId,countryId);
        if (province == null)
            throw new ResourceNotFoundException(provinceId.toString() ,"common.province.id_notFound" , provinceId );
        return province;
    }

    @Override
    public List<City> getCityList() {
        List<City> result = (List<City>) cacheService.getCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("allCityList"));
        if (result != null)
            return result;
        result= cityRepository.findAll();
        cacheService.putCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("allCityList"), result,15,  TimeUnit.DAYS);
        return result;
    }



    @Override
    public List<City> getActiveCity(Long provinceId) {
        List<City> result = (List<City>) cacheService.getCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("provinceActiveCityList_"+ provinceId));
        if (result != null)
            return result;
        result= cityRepository.findAllActiveCity(provinceId);
        cacheService.putCacheValue("activeAndLiveCity", Utils.addGlobalCacheKey("provinceActiveCityList_"+ provinceId), result,15,  TimeUnit.DAYS);
        return result;
    }

    @Override
    public City getCityInfo(Long cityId) {
        City city=cityRepository.findByEntityId(cityId);
        if (city == null)
            throw new ResourceNotFoundException(cityId.toString() ,"common.city.id_notFound" , cityId );
        return city;
    }


    @Override
    public City getActiveCityInfo(Long cityId) {
        City city;
        if (businessCacheEnable) {
            city = (City) cacheService.getCacheValue("cityEName", Utils.addGlobalCacheKey(cityId));
            if (city != null)
                return city;
        }
        city=cityRepository.findActiveById(cityId);
        if (city == null)
            throw new ResourceNotFoundException(cityId.toString() ,"common.city.id_notFound" , cityId );
        if (businessCacheEnable && city!=null)
            cacheService.putCacheValue("cityEName", Utils.addGlobalCacheKey(cityId), city,15, TimeUnit.DAYS);
        return city;

    }

    @Override
    public City getCityById(Long cityId) {
        return cityRepository.findActiveById(cityId);
    }

    @Override
    public City getCityByEnglishName(String englishName) {
        City result;
        if (businessCacheEnable) {
            result = (City) cacheService.getCacheValue("cityEName", Utils.addGlobalCacheKey(englishName));
            if (result != null)
                return result;
        }
        result=cityRepository.getCityByEnglishName(englishName);
        if (result == null)
            throw new ResourceNotFoundException(englishName, "common.city.englishName_notFound" ,englishName);
        if (businessCacheEnable && result!=null)
            cacheService.putCacheValue("cityEName", Utils.addGlobalCacheKey(englishName), result,15, TimeUnit.DAYS);
        return result;
    }



    @Override
    public List<Region> getRegionList(Long cityId) {
        return regionRepository.findAllByCityId(cityId);
    }


    @Override
    public Region getRegionInfo(Long regionId) {
        Region result;
        result=regionRepository.findByEntityId(regionId);
        if (result == null)
            throw new ResourceNotFoundException("Resource Not Found", "common.region.id_notFound");
        return result;
    }


    @Override
    public Region getRegionByIdAndCityId(Long cityId,Long regionId ) {
        Region region = regionRepository.getRegionByIdAndCityId(cityId, regionId);
        if (region == null)
            throw new ResourceNotFoundException(cityId.toString(), "common.cityRegion.id_notFound" , regionId );
        return region;
    }

    @Override
    public List<Country> getCountries() {
        return countryRepository.findAllCountry();
    }

    @Override
    public Country getCountryInfo(Long countryId) {
        Country country=countryRepository.findByEntityId(countryId);
        if (country == null)
            throw new ResourceNotFoundException(country.toString() ,"common.country.id_notFound" , countryId);
        return country;
    }

    @Override
    public Country getActiveCountryInfo(Long countryId) {
        Country country=countryRepository.findActiveById(countryId);
        if (country == null)
            throw new ResourceNotFoundException(countryId.toString() ,"common.country.id_notFound" , countryId );

        return country;

    }



    //#endregion


    //#region  Redirect Url
    @Override
    public List<RedirectUrl> getActiveRedirectUrls(){
        List<RedirectUrl> redirectUrlList;
        if (configurationCacheEnable) {
            redirectUrlList = (List<RedirectUrl>) cacheService.getCacheValue("redirectUrls", "redirectUrls");
            if (redirectUrlList != null)
                return redirectUrlList;
        }
        redirectUrlList =redirectUrlRepository.findAllActiveRedirectUrl();
        if (configurationCacheEnable && redirectUrlList!=null)
            cacheService.putCacheValue("redirectUrls", "redirectUrls", redirectUrlList, 15, TimeUnit.DAYS);
        return redirectUrlList;
    }

    //#endregion




    //#region Message Resource


    @Override
    public List<MessageResource> getMessageResources(Integer messageResourceType) {
        return messageResourceRepository.findAllMessages(EMessageResourceType.valueOf(messageResourceType).value());
        /*String cacheKey=messageResourceListCacheKeyPerfix+messageResourceType;
        List<MessageResource>  result = (List<MessageResource>) cacheService.getCacheValue("messageResources", Utils.addGlobalCacheKey(cacheKey));
        if (result != null)
            return result;

        result= messageResourceRepository.findAllMessages(EMessageResourceType.valueOf(messageResourceType).value());
        cacheService.putCacheValue("messageResources",  Utils.addGlobalCacheKey(cacheKey), result,30,  TimeUnit.DAYS);
        return result;*/
    }

    @Override
    public MessageResource getMessageInfo(Long messageId) {
        if (messageId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        return messageResourceRepository.findByEntityId(messageId);
    }

    @Override
    public MessageResource getMessageInfo(String key) {
        return this.getMessageInfo(key,false);
    }

    private MessageResource getMessageInfo(String key,Boolean fetchDBOnly) {
        if (Utils.isStringSafeEmpty(key))
            return null;
        MessageResource result=null;
        if(!fetchDBOnly)
            result = (MessageResource) cacheService.getCacheValue("messageResources", Utils.addGlobalCacheKey(key));

        if (result != null)
            return result;
        result= messageResourceRepository.findByKey(key);

        if(!fetchDBOnly && result!=null)
           cacheService.putCacheValue("messageResources",  Utils.addGlobalCacheKey(key), result,30,  TimeUnit.DAYS);

        return result;
    }

    @Override
    public MessageResource editMessageResource(EMessageResource eMessageResource) {
        MessageResource messageResource=this.getMessageInfo(eMessageResource.getKey(),true);
        if(messageResource==null)
            throw new ResourceNotFoundException(eMessageResource.getKey(), "common.id_notFound" , eMessageResource.getKey());

        messageResource.setContent(eMessageResource.getContent());
        messageResource=messageResourceRepository.save(messageResource);
        cacheService.deleteCacheValue("messageResources",Utils.addGlobalCacheKey(messageResource.getKey()));
        cacheService.deleteCacheValue("messageResources",Utils.addGlobalCacheKey(jsonMessageResourceListCacheKeyPrefix+messageResource.getType()));
        cacheService.deleteCacheValue("messageResources",Utils.addGlobalCacheKey(jsMessageResourceListCacheKeyPrefix+messageResource.getType()));
        return messageResource;
    }

    private static String  jsonMessageResourceListCacheKeyPrefix="jsonMessageList_";
    @Override
    public Map<String, String> getJsonMessageResources() {
        String cacheKey=jsonMessageResourceListCacheKeyPrefix+EMessageResourceType.JSON.value();
        Map<String, String>  result = (Map<String, String>) cacheService.getCacheValue("messageResources", Utils.addGlobalCacheKey(cacheKey));
        if (result != null)
            return result;

        result= this.getMessageResources(EMessageResourceType.JSON.value()).stream().collect(Collectors.toMap(MessageResource::getKey,MessageResource::getContent));
        if(result!=null)
          cacheService.putCacheValue("messageResources",  Utils.addGlobalCacheKey(cacheKey), result,30,  TimeUnit.DAYS);
        return result;
    }

    private static String  jsMessageResourceListCacheKeyPrefix="jsMessageList_";
    @Override
    public String getJsMessageResources() {
        String cacheKey=jsMessageResourceListCacheKeyPrefix+EMessageResourceType.JS.value();
        String result = (String) cacheService.getCacheValue("messageResources", Utils.addGlobalCacheKey(cacheKey));
        if (result != null)
            return result;

        result="var resourceObj = "+ Utils.getAsJson(this.getMessageResources(EMessageResourceType.JS.value()).stream().collect(Collectors.toMap(MessageResource::getKey,MessageResource::getContent))) ;
        if(result!=null)
          cacheService.putCacheValue("messageResources",  Utils.addGlobalCacheKey(cacheKey), result,30,  TimeUnit.DAYS);
        return result;
    }

    //#endregion

    //#region Notify

    @Override
    public void sendNotification(Long smsBodyCodeId, Long recipientUserId, List<String> mobiles, String[] mobileMessage, List<String> emails, String[] emailMessage,Long targetTypeId, Long targetId) {
        if (this.getSendUserActionSms() ) {
            ENotifyType eNotifyType = ENotifyType.valueOf(this.getNotifyType());
            SmsBodyCode smsBodyCode=null;
            try {
                smsBodyCode = smsServiceFactory.getSmsService().getBodyCode(smsBodyCodeId);
            }catch (ResourceNotFoundException e){}
            if (smsBodyCode!=null) {
                for (String mobile : mobiles) {
                    if ((eNotifyType == ENotifyType.MOBILE_EMAIL || eNotifyType == ENotifyType.MOBILE)) {
                        notificationService.createSmsNotification(new Date(), mobile,
                                smsBodyCode.getTitle(), Arrays.stream(mobileMessage).collect(Collectors.joining(";")),
                                Notification.Medium.BASE_SMS, Notification.Status.NEW, recipientUserId,
                                targetTypeId, targetId, null, null, smsBodyCode.getCode().toString(), null);
                    }
                }
                for (String email : emails) {
                    if ((eNotifyType == ENotifyType.MOBILE_EMAIL || eNotifyType == ENotifyType.EMAIL)) {
                        notificationService.createEmailNotification(new Date(), email,
                                smsBodyCode.getTitle(), String.format(smsBodyCode.getDescFormatReplaceArg(), emailMessage),
                                Notification.Medium.EMAIL, Notification.Status.NEW, recipientUserId,
                                targetTypeId, targetId, null, null, smsBodyCode.getCode().toString(), null);
                    }
                }
            }
        }
    }

    @Override
    public void sendConfirmCode(Long smsBodyCodeId,Long userId, String[] mobileMessage, String[] emailMessage) {
        User user=userService.getUserInfo(userId);
        this.sendConfirmCodeAsync(smsBodyCodeId,user.getMobileNumber(),user.getEmail(),mobileMessage,emailMessage);
    }

    @Override
    public void sendConfirmCode(Long smsBodyCodeId,String userMobileNumber,String userEmail, String[] mobileMessage, String[] emailMessage) {
        ENotifyType eNotifyType=ENotifyType.valueOf(this.getNotifyType());
        SmsBodyCode smsBodyCode=null;
        try {
            smsBodyCode = smsServiceFactory.getSmsService().getBodyCode(smsBodyCodeId);
        }catch (ResourceNotFoundException e){}
        if (smsBodyCode!=null) {
            if (eNotifyType == ENotifyType.MOBILE_EMAIL || eNotifyType == ENotifyType.MOBILE) {
                if (Utils.isStringSafeEmpty(userMobileNumber))
                    throw new InvalidDataException("Invalid Data", "common.user.mobileNumber_required");
                SmsServiceResponse smsServiceResponse = smsServiceFactory.getSmsService().baseSend(userMobileNumber, mobileMessage[0], smsBodyCode.getCode());
            }
            if (eNotifyType == ENotifyType.MOBILE_EMAIL || eNotifyType == ENotifyType.EMAIL) {
                if (Utils.isStringSafeEmpty(userEmail))
                    throw new InvalidDataException("Invalid Data", "common.user.email_required");
                mailingService.sendEmailByBody(new String[]{userEmail}, new String[]{}, new String[]{},
                        smsBodyCode.getTitle(), String.format(smsBodyCode.getDescFormatReplaceArg(), emailMessage[0]));
                //"Activation Code","Your Activation Code:"+ emailMessage[0]);
            }
        }
    }

    @Override
    public void sendConfirmCodeAsync(Long smsBodyCodeId, String userMobileNumber,String userEmail, String[] mobileMessage, String[] emailMessage) {
        HashMap<String, Object> confirmCodeArgs = new HashMap<>();
        confirmCodeArgs.put("smsBodyCodeId", smsBodyCodeId);
        confirmCodeArgs.put("userMobileNumber", userMobileNumber);
        confirmCodeArgs.put("userEmail", userEmail);
        confirmCodeArgs.put("mobileMessage", mobileMessage);
        confirmCodeArgs.put("emailMessage", emailMessage);
        qNotifierService.notify(new QNotifyObject("commonQueue", new MyQueueData(EMyQueueOperation.SEND_CONFIRM_CODE, confirmCodeArgs)));
    }

    //#endregion


    //#region Confirm Code
    private String getConfirmCodeCacheKey(EConfirmableOperation eConfirmableOperation, String referenceKey){
        return eConfirmableOperation.name()+ "_" + referenceKey;
    }

    @Override
    public String generateAndSendConfirmCode(Long userId,EConfirmableOperation eConfirmableOperation, String referenceKey, Map<String, Object> additionalData) {
        if(additionalData==null)
            additionalData=new HashMap<>();
        if(additionalData.keySet().stream().filter(k-> k=="confirmCode").count()>0)
            throw new InvalidDataException("Invalid Data", "confirmCode and sendDateTime Reserved For Key.");

        Integer confirmCode=Utils.createUniqueRandom(5);
        additionalData.put("confirmCode",confirmCode);
        additionalData.put("sendDateTime",new Date());

        String cacheKey=this.getConfirmCodeCacheKey(eConfirmableOperation, referenceKey);

        Map<String, Object> oldSendCode= (Map<String, Object>) cacheService.getCacheValue("confirmCodes",cacheKey);
        if (oldSendCode!=null){
            Date oldSendDateTime=(Date)oldSendCode.get("sendDateTime");
            Integer remainSecond=(eConfirmableOperation.getLiveMinute()*60)-calendarService.dateDiff(oldSendDateTime,new Date(), com.core.model.enums.Calendar.SECOND);
            throw new InvalidDataException("Invalid Data", "common.confirmCode.sendDate_Error",remainSecond);
        }

        String[] message={confirmCode.toString()};
        this.sendConfirmCode(1l,userId,message,message);

        cacheService.putCacheValue("",cacheKey,additionalData,eConfirmableOperation.getLiveMinute(),TimeUnit.MINUTES);

        ENotifyType eNotifyType=ENotifyType.valueOf(this.getNotifyType());
        return Utils.getMessageResource("common.user.activationCode_sendNotify" , eNotifyType.getCaption()) ;
    }

    @Override
    public Boolean validateConfirmCodeData(Long userId, EConfirmableOperation eConfirmableOperation, String referenceKey,Integer userCode) {
        String cacheKey=this.getConfirmCodeCacheKey(eConfirmableOperation, referenceKey);
        Map<String, Object> oldSendCode= (Map<String, Object>) cacheService.getCacheValue("confirmCodes",cacheKey);
        if(oldSendCode==null)
            throw new InvalidDataException("Invalid Data", "common.user.activationCode_invalid");

        Integer sendedConfirmCode=(Integer) oldSendCode.get("confirmCode");
        if(sendedConfirmCode==null || !sendedConfirmCode.equals(userCode))
            throw new InvalidDataException("Invalid Data", "common.user.activationCode_invalid");

        return true;
    }

    //#endregion

    public abstract List<FileManagerAction> getFileManagerConfig( String targetType, Long id) ;

    protected List<FileManagerAction> getUserFileManagerActions(Long userId){
        return fileManagerActionRepository.findUserActions(userService.getCurrentUser().getId());
    };

    public FileManagerAction getFileManagerAction(Long fileManagerActionId){
        if(fileManagerActionId==null)
            throw new InvalidDataException("Invalid Data", "common.fileManagerAction.id_required");
        Optional<FileManagerAction> fileManagerAction= fileManagerActionRepository.findById(fileManagerActionId);
        if (!fileManagerAction.isPresent())
            throw new ResourceNotFoundException(fileManagerActionId.toString(), "common.fileManagerAction.id_notFound");
        return  fileManagerAction.get();
    }

    //#region Cache Service

    @Override
    public String clearCache(String cacheName) {
        cacheService.clearCacheValue(cacheName);
        return "ok";
    }

    @Override
    public String clearAllCache() {
        cacheService.clearAllCacheValue();
        return "ok";
    }

    @Override
    public Object putInCache(String cacheName, String key, String value, Integer timeToLive) {
        return cacheService.putCacheValue(cacheName.trim(),key.trim(),value.trim(),timeToLive,TimeUnit.DAYS);
    }

    @Override
    public Set<Map.Entry> getCacheValues(String cacheName, String sqlPredicateString) {
        //Sample new SqlPredicate("__key like '%ARTICLE_1260%'")

        String[] tmpQ =sqlPredicateString.split("like");
        if(tmpQ.length>1){
            sqlPredicateString=tmpQ[0] + " like %" + tmpQ[1].trim() + "%";
        }
        return cacheService.getCacheValues(cacheName.trim(),sqlPredicateString);
    }

    //#endregion Cache Service

    //#region FileManagerAction

    @Override
    public List<FileManagerAction> getFileManagerActions() {
        return fileManagerActionRepository.findFileManagerActions();
    }

    //#endregion FileManagerAction




}

