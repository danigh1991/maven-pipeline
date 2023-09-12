package com.core.common.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import com.core.common.model.contextmodel.*;
import com.core.common.model.enums.EUniqueCodeValidationType;
import com.core.common.model.shahkar.ShahkarServiceRequest;
import com.core.common.model.shahkar.ShahkarServiceResponse;
import com.core.common.services.*;
import com.core.codegenerator.QRCodeGenerator;
import com.core.datamodel.model.enums.*;
import com.core.datamodel.services.CacheService;
import com.core.datamodel.model.securitymodel.UserPrincipalDetails;
import com.core.model.wrapper.QRCodeDataWrapper;
import com.core.services.CalendarService;
import com.core.exception.InvalidDataException;
import com.core.datamodel.model.dbmodel.*;
import com.core.model.enums.EMyQueueOperation;
import com.core.model.qmodel.MyQueueData;
import com.core.model.qmodel.QNotifyObject;
import com.core.model.repository.IDynamicQueryRepository;
import com.core.model.wrapper.ResultListPageable;
import com.core.queue.service.QNotifierService;
import com.core.datamodel.repository.*;
import com.core.common.security.AuthenticationHelper;
import com.core.common.security.TokenHelper;
import com.core.common.security.UserTokenState;
import com.core.common.util.Utils;
import com.core.util.BaseUtils;
import com.core.util.search.SearchCriteria;
import com.core.util.search.SearchCriteriaParser;
import com.google.zxing.WriterException;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.core.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service("userServiceImpl")
@CacheConfig(cacheNames = "users")
public class UserServiceImpl extends AbstractService implements UserService {
    @Value("${jwt.max_attempts}")
    private Long MAX_ATTEMPTS;
    @Value("${app.file_upload_userprofiles}")
    private String userProfileRoot;

    @Value("${app.support.email}")
    private String supportMail;

    @Value("${app.domain_url}")
    private String domainURL;

    @Value("${jwt.cookie}")
    private String TOKEN_COOKIE;

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;

    @Value("${app.user_affiliate_prefix}")
    private String USER_AFFILIATE_PREFIX ;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private BaseCityRepository cityRepository;

    @Autowired
    private UserAttemptsRepository userAttemptsRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private QNotifierService qNotifierService;

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private UserActionsRepository userActionsRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ShahkarServiceImpl shahkarService;

    @Autowired
    private SecureCodeGenerator secureCodeGenerator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupMemberRepository  userGroupMemberRepository;

    @Autowired
    private IDynamicQueryRepository userDynamicQueryRepository;
    @Autowired
    private SearchCriteriaParser searchCriteriaParser;
    @Value("#{${user.search.params}}")
    private HashMap<String, List<String>> USER_SEARCH_MAP_PARAMS;



    @Override
    public List<User> findAllUsers() {
        // TODO Auto-generated method stub
        return (List<User>) userRepository.findAll();
    }

    private String validateUserName(CUser cUser){
        String validateMessage = "";
        if(Utils.getUserNameField().equals("mobile")) {
            if(!Utils.isValidByPattern(cUser.getUsername(), commonService.getMobilePattern())/*Mobile Pattern Check*/)
                validateMessage += Utils.getMessageResource("common.user.mobileNumber_len");
        }else if(Utils.getUserNameField().equals("email")) {
            if(!Utils.isValidEmail(cUser.getUsername()))/*Email Pattern Check*/
                validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.email_check");
        }

        if(commonService.loginUniqueCodeType()==EUniqueCodeType.NATIONAL_CODE) {
            if(!Utils.isValidUniqueCode(cUser.getUniqueCode(),"*"))/*Email Pattern Check*/
                validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.uniqueCode_invalid");
        }


        if(!Utils.getUserNameField().equals("email") &&commonService.getEmailIsRequired()) {
            if (Utils.isStringSafeEmpty(cUser.getEmail()))
                validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.email_required");
            else if (!Utils.isValidEmail(cUser.getEmail())/*Email Pattern Check*/)
                validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.email_check");
        }

        if(!Utils.getUserNameField().equals("mobile") && commonService.getMobileIsRequired()) {
            if(Utils.isStringSafeEmpty(cUser.getMobile()))
                validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.mobileNumber_required");
            else if(!Utils.isValidByPattern(cUser.getMobile(), commonService.getMobilePattern())/*Mobile Pattern Check*/)
                validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.mobileNumber_len");
        }

        return validateMessage;
    }

    @Override
    @Transactional
    public String registerUserAndSendActivationCode(@Valid CUser cUser, Long createBy) {
        Boolean isVerify=false;
        String verifyResponse=null;

        if(Utils.getUserNameField().equals("email"))
            cUser.setEmail(cUser.getUsername());
        if(Utils.getUserNameField().equals("mobile"))
            cUser.setMobile(cUser.getUsername());

        String validateResult = this.validateUserName(cUser);
        if(validateResult.length() > 0)
            throw new InvalidDataException("InvalidData", validateResult);

        if (commonService.loginUniqueCodeType()==EUniqueCodeType.NATIONAL_CODE){
            if(Utils.isStringSafeEmpty(cUser.getUniqueCode()))
                throw new InvalidDataException("InvalidData", "common.user.uniqueCode_invalid");
            if(!Utils.isValidUniqueCode(cUser.getUniqueCode(),"*"))/* UniqueCode Pattern Check*/
                throw new InvalidDataException("InvalidData", "common.user.uniqueCode_invalid");
            cUser.setUniqueCode(cUser.getUniqueCode());

            ENotifyType eNotifyType=ENotifyType.valueOf(commonService.getNotifyType());
            if ((eNotifyType==ENotifyType.MOBILE || eNotifyType==ENotifyType.MOBILE_EMAIL) ) {
                //todo Call Shahkar Service
                if(EUniqueCodeValidationType.valueOf(commonService.getUniqueCodeValidationType())==EUniqueCodeValidationType.SHAHKAR){
                    ShahkarServiceResponse shahkarServiceResponse=shahkarService.callService(ShahkarServiceRequest.builder().serviceType(2).serviceNumber(cUser.getMobile()).identificationType(0).identificationNo(cUser.getUniqueCode()).build());
                    isVerify=true;
                    verifyResponse=shahkarServiceResponse.toString();
                }
            }
        }

        User user = this.createRegisterUser(cUser, createBy,isVerify,verifyResponse);

        if (Utils.isStringSafeEmpty(user.getSecret()))
            user=this.generateUserSecret(user.getId());
        String activationCode=secureCodeGenerator.nowCode(user.getSecret(),commonService.getVerificationCodeLiveTimeSecond());
        String[] message={activationCode};
        user.setActivationCode(Integer.valueOf(activationCode));

        /*Integer activationCode = Utils.createUniqueRandom(5);
        user.getActivationCode().add(activationCode);
        String[] message={activationCode.toString()};*/

        commonService.sendConfirmCodeAsync(1l,user.getMobileNumber(),user.getEmail(),message,message);
        ENotifyType eNotifyType=ENotifyType.valueOf(commonService.getNotifyType());

        cacheService.putCacheValue("userRegister", user.getUsername(), user, commonService.getRegisterUserLiveTimeSecond(), TimeUnit.SECONDS);
        return Utils.getMessageResource("common.user.activationCode_sendNotify" , eNotifyType.getCaption()) ;
    }

    public User createRegisterUser( CUser cUser, Long createBy,Boolean isVerify,String verifyResponse) {

        User findUser = userRepository.findByUsername(cUser.getUsername());
        if (findUser != null)
            throw new InvalidDataException(cUser.getUsername(), "common.user.exist");

        if (Utils.getAffiliateState() == 1 && !Utils.isStringSafeEmpty(cUser.getAffiliateReagent())) {
            User findAffiliateUser = this.getByAffiliateCode(cUser.getAffiliateReagent());
        }

        User user = mapToDbUser(cUser, createBy);
        user.setVerified(isVerify);
        user.setVerifyResponse(verifyResponse);
        return user;
    }



    @Transactional
    @Override
    public UserTokenState commitRegisterUser(String userName, Integer activeCode, HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {
        try {
            User user = this.commitRegisterUser(userName, activeCode);
            //#endregion
            if (request != null && response != null)
                return authenticationHelper.authenticationByUserNameAndPassword(request, response, user.getUsername(), user.getTmpPass());
            return null;
        }catch (BadCredentialsException ex){
            throw new InvalidDataException("Invalid Data",ex.getMessage());
        }
    }

    @Transactional
    @Override
    public User commitRegisterUser(String userName, Integer verificationCode) {
        User user = userRepository.findByUsername(userName);
        if (user != null)
            throw new BadCredentialsException(Utils.getMessageResource("common.user.exist"));

        user = this.getNewUserForCommitRegister(userName);
        if (user == null)
            throw new BadCredentialsException(Utils.getMessageResource("common.user.tempRegister_notFound"));

        if (!user.getActivationCode().equals(verificationCode) && !secureCodeGenerator.verifyCode(verificationCode.toString(), user.getSecret(),commonService.getVerificationCodeLiveTimeSecond()))
            throw new BadCredentialsException(Utils.getMessageResource("common.user.activationCode_invalid"));
        userRepository.save(user);
        user.setAffiliateCode(USER_AFFILIATE_PREFIX + user.getId().toString());
        user=userRepository.save(user);

        cacheService.deleteCacheValue("userRegister", userName);
        return user;

    }

    @Override
    public User getNewUserForCommitRegister(String userName) {
        User user = (User) cacheService.getCacheValue("userRegister", userName);
        return user;
    }

    @Override
    public String reSendActivationCode(String userName) {
        User user = this.getNewUserForCommitRegister(userName);
        if (user == null)
            throw new ResourceNotFoundException("Not Found", "common.user.tempRegister_notFound");

        if (Utils.isStringSafeEmpty(user.getSecret()))
            user=this.generateUserSecret(user.getId());
        String activationCode=secureCodeGenerator.nowCode(user.getSecret(),commonService.getVerificationCodeLiveTimeSecond());
        String[] message={activationCode};
        user.setActivationCode(Integer.valueOf(activationCode));


        user.setActivationCode(Integer.valueOf(activationCode));
        commonService.sendConfirmCodeAsync(1l,user.getMobileNumber(),user.getEmail(),message,message);
        ENotifyType eNotifyType=ENotifyType.valueOf(commonService.getNotifyType());

        cacheService.putCacheValue("userRegister", user.getUsername(), user, commonService.getRegisterUserLiveTimeSecond(), TimeUnit.SECONDS);
        return Utils.getMessageResource("common.user.activationCode_sendNotify" , eNotifyType.getCaption()) ;
    }

    @Override
    @Transactional
    public Integer updateFailAttemptsAndLockResult(String username) {
        User findUser = userRepository.findByUsername(username);
        if (findUser != null) {
            UserAttempts findUserAttempts = getUserAttempts(findUser.getId());
            if (findUserAttempts == null) {
                findUserAttempts = new UserAttempts();
                findUserAttempts.setId(findUser.getId());
                findUserAttempts.setAttempts(1L);
                findUserAttempts.setLastmodified(new Date(System.currentTimeMillis()));
                userAttemptsRepository.saveAndFlush(findUserAttempts);
            } else {
                findUserAttempts.setAttempts(findUserAttempts.getAttempts() + 1);
                findUserAttempts.setLastmodified(new Date(System.currentTimeMillis()));
                userAttemptsRepository.saveAndFlush(findUserAttempts);

                if (commonService.getMaxAttemptForLockLevel1() >0 &&  findUserAttempts.getAttempts() >= commonService.getMaxAttemptForLockLevel1() && findUserAttempts.getAttempts()< commonService.getMaxAttemptForLockLevel2()) {
                    findUser.setAccountLockedTo(Utils.addMinuteToDate(Utils.getCurrentDateTime(), commonService.getLockMinuteLevel1()));
                    userRepository.saveAndFlush(findUser);
                    this.deleteUserDataCache(findUser.getId(),findUser.getUsername());
                    return  1;
                }
                if (commonService.getMaxAttemptForLockLevel2()>0 &&  findUserAttempts.getAttempts() >= commonService.getMaxAttemptForLockLevel2() && findUserAttempts.getAttempts()< commonService.getMaxAttemptForAlwaysLock()) {
                    findUser.setAccountLockedTo(Utils.addMinuteToDate(Utils.getCurrentDateTime(), commonService.getLockMinuteLevel2()));
                    userRepository.saveAndFlush(findUser);
                    this.deleteUserDataCache(findUser.getId(),findUser.getUsername());
                    return  2;
                }
                if (findUserAttempts.getAttempts() >= commonService.getMaxAttemptForAlwaysLock()) {
                    findUser.setAccountNonLocked(false);
                    userRepository.saveAndFlush(findUser);
                    this.deleteUserDataCache(findUser.getId(),findUser.getUsername());
                    return  3;
                }
            }
        }
        return 0;
    }

    @Transactional
    @Override
    public String lockUser(Long userId) {
        User user=this.getUserInfoForEdit(userId);
        if(user.isAccountNonLocked()) {
            userRepository.save(this.lockUser(user));
            this.deleteUserDataCache(user.getId(), user.getUsername());
            return Utils.getMessageResource("global.operation.success_info");
        }
        return "";
    }
    private User lockUser(User user) {
        user.setAccountNonLocked(false);
        return user;
    }

    @Transactional
    @Override
    public String unlockUser(Long userId) {
        User user=this.getUserInfoForEdit(userId);
        if(!user.isAccountNonLocked()) {
            userRepository.save(this.unlockUser(user));
            this.deleteUserDataCache(user.getId(), user.getUsername());
            return Utils.getMessageResource("global.operation.success_info");
        }
        return "";
    }

    private User unlockUser(User user) {
        if(!user.isAccountNonLocked())
           this.resetFailAttempts(user);
        user.setAccountNonLocked(true);
        return user;
    }

    @Transactional
    @Override
    public String resetUserPassword(Long userId) {
        User user=this.getUserInfoForEdit(userId);
        return this.resetUserPassword(user);
    }

    private String resetUserPassword(User user) {
        ELoginType eLoginType;
        if(user.getLoginType()!=null)
            eLoginType=ELoginType.valueOf(user.getLoginType());
        else
            eLoginType=ELoginType.valueOf(commonService.getLoginType());

        if(ELoginType.ONLY_PASSWORD==eLoginType || ELoginType.OTP_PASSWORD==eLoginType){
            String generatedPassword=Utils.createRandomPassword(6);
            user.setPassword(passwordEncoder.encode(generatedPassword));
            user.setCredentialChangeDate(new Date());
            if(commonService.getForceChangePasswordAfterReset())
                user.setCredentialsNonExpired(false);
            userRepository.save(user);
            this.deleteUserDataCache(user.getId(), user.getUsername());
            String[] message={generatedPassword};
            commonService.sendConfirmCodeAsync(17l,user.getMobileNumber(),user.getEmail(),message,message);
            return Utils.getMessageResource("global.operation.success_info");
        }
        return "";
    }


    @Transactional
    @Override
    public void resetFailAttempts(String username) {
        try {
            this.resetFailAttempts(this.getUserInfoForEdit(username));
        }catch (ResourceNotFoundException ex){
        }
    }
    private void resetFailAttempts(User user) {
        if (user != null) {
            UserAttempts findUserAttempts = getUserAttempts(user.getId());
            if (findUserAttempts != null) {
                findUserAttempts.setAttempts(0L);
                findUserAttempts.setLastmodified(new Date(System.currentTimeMillis()));
                userAttemptsRepository.saveAndFlush(findUserAttempts);
            }
        }
    }

    @Override
    public UserAttempts getUserAttempts(Long id) {
        try {
            UserAttempts findUserAttempts = userAttemptsRepository.findByEntityId(id);
            return findUserAttempts;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public UserAttempts getUserAttempts(String username) {
        try {
            User findUser = userRepository.findByUsername(username);
            if (findUser != null) {
                UserAttempts findUserAttempts = getUserAttempts(findUser.getId());
                return findUserAttempts;
            } else {
                return null;
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private boolean isUserExists(String username) {
        boolean result = false;
        User findUser = userRepository.findByUsername(username);
        if (findUser != null) {
            UserAttempts findUserAttempts = userAttemptsRepository.findByEntityId(findUser.getId());
            if (findUserAttempts != null)
                result = true;
        }
        return result;
    }

    @Override
    public String uploadProfileImage(Long id, MultipartFile image) {
        User user =this.getUserInfoForEdit(id);
        user.setModifyDate(new Date());
        userRepository.save(user);
        user.setAvatar(fileStorageService.store(userProfileRoot, id.toString(), image, 200, 200));
        return user.getLogoPath();
    }


    private User mapToDbUser(CUser cUser, Long createBy) {
        //City city = businessService.getCityById(cUser.getCityId());
        User user = new User();
        user.setUsername(cUser.getUsername());
        user.setPassword(passwordEncoder.encode(cUser.getPassword()));
        user.setTmpPass(cUser.getPassword());
        user.setState(1);
        if (cUser.getCityId() != null && cUser.getCityId() > 0) {
            City city = cityRepository.findActiveById(cUser.getCityId());
            if (city == null)
                user.setCity(city);
        }
        user.setCreateBy(createBy);
        user.setMobileNumber(cUser.getMobile());
        user.setEmail(cUser.getEmail());
        user.setUniqueCode(cUser.getUniqueCode());
        List<Role> roles = roleRepository.findDefaultRole();
        user.setRoles(roles);
        user.setGender(cUser.getGender());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCredentialChangeDate(new Date());
        user.setEnabled(true);
        user.setAffiliateReagent(cUser.getAffiliateReagent());
        return user;
    }

    @Override
    public UserPrincipalDetails getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken))
            return (UserPrincipalDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

        return null;
    }

    @Override
    public User getCurrentUserInfo() {
        UserPrincipalDetails currentUser = getCurrentUser();
        if (currentUser != null) {
            // User user = getUserInfo(currentUser.getId());
            return currentUser.getUser();
        }
        return null;
    }

    @Override
    public User getUserInfo(Long userId) {
        if(userId==null)
            throw new InvalidDataException("Invalid Data", "common.user.id_required");

        User result = (User) cacheService.getCacheValue("usersById", userId);
        if (result != null)
            return result;
        result = userRepository.findByEntityId(userId);
        if (result == null)
            throw new ResourceNotFoundException(userId.toString(), "common.user.id_notFound" , userId);


        cacheService.putCacheValue("usersById", userId, result, EXPIRES_IN, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public User getUserInfo(String userName) {
        if(Utils.isStringSafeEmpty(userName))
            throw new InvalidDataException("Invalid Data", "common.user.username_required");

        User result = (User) cacheService.getCacheValue("usersByUserName", userName);
        if (result != null)
            return result;
        result = userRepository.findByUsername(userName);
        if (result == null)
            throw new ResourceNotFoundException(userName, "common.user.username_notFound" , userName);
        cacheService.putCacheValue("usersByUserName", userName, result,EXPIRES_IN, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public User getByAffiliateCode(String affiliateCode) {
        User user = userRepository.findByAffiliateCode(affiliateCode);
        if (user == null)
            throw new ResourceNotFoundException(affiliateCode, "common.user.affiliateCode_notFound" , affiliateCode );

        return user;
    }

    @Override
    public List<User> getAffiliateUsers() {
        List<User> affiliateUsers=new ArrayList<>();
        if(hasRole("affiliateManager"))
           affiliateUsers=userRepository.findAllAffiliateUser();
        else if(hasRole("affiliateHeader")) {
            affiliateUsers = userRepository.findAffiliateUserByHeaderId(this.getCurrentUser().getId());
            affiliateUsers.add(this.getCurrentUser().getUser());
        }
        else
            affiliateUsers.add(this.getCurrentUser().getUser());

        return affiliateUsers;
    }

    @Override
    public Boolean existUserByUserName(String userName) {
        return userRepository.existsByUsername(userName);
    }

    @Override
    public InputStream getMyQRCode(Double amount) throws WriterException, IOException {
        String qrCodeText=Utils.enCrypt(this.getCurrentUser().getUsername() + this.getUserQrCodeDataSeparator() + amount);
        return new ByteArrayInputStream(QRCodeGenerator.getQRCodeImageByteArray(qrCodeText, 250, 250));
    }

    private String getUserQrCodeDataSeparator(){
       return "<,>";
    }

    @Override
    public QRCodeDataWrapper getQRCodeData(String qrCodeText) {
        if(Utils.isStringSafeEmpty(qrCodeText))
            return null;
        String qrCodeData="";
        try {
            qrCodeData = Utils.deCrypt(qrCodeText);
        }catch (IllegalStateException e){
            throw new InvalidDataException("Invalid Data", "global.qrcode_inValid");
        }
        String[] qrCodeDatas= qrCodeData.split(this.getUserQrCodeDataSeparator());
        if(qrCodeDatas.length==0 || qrCodeDatas.length>2)
            throw new InvalidDataException("Invalid data", "common.user.qeCode_invalid");

        QRCodeDataWrapper result=new QRCodeDataWrapper();
        result.setUserName(qrCodeDatas[0]);
        if(qrCodeDatas.length>1)
            result.setAmount(Utils.parsDouble(qrCodeDatas[1]));
        return result;
    }

    @Override
    public User getUserByUserNameOrEmail(String param) {
        User user = userRepository.findByUsername(param);
        if (user == null) {
            List<User> users = userRepository.findByEmail(param);
            if (users!=null && users.size()>0)
               user=users.get(0);
        }
        if (user == null)
            throw new UsernameNotFoundException(String.format("common.user.userPass_invalid"));

        return user;
    }

    @Override
    public User getUserInfoForEdit(Long userId) {
        if(userId==null )
            throw new InvalidDataException(userId.toString(), "common.user.id_required");
        User user;

        if(hasRoleType(ERoleType.ADMIN) )
            user = userRepository.findByEntityId(userId);
        else
            user = userRepository.findByEntityId(Utils.getCurrentUserId());
        if (user == null)
            throw new ResourceNotFoundException(userId.toString(), "common.user.id_notFound" , userId);

        return user;
    }

    @Override
    public User getUserInfoForEdit(String userName) {
        User result = userRepository.findByUsername(userName);
        if (result == null)
            throw new ResourceNotFoundException(userName, "common.user.username_notFound" , userName);
        return result;
    }

    private String editUser(User user , EUser eUser) {
        String validateMessage = "";
        if(!Utils.isStringSafeEmpty(eUser.getMobileNumber()) && !Utils.isValidByPattern(eUser.getMobileNumber(), commonService.getMobilePattern())/*Mobile Pattern Check*/)
            validateMessage = Utils.getMessageResource("common.user.mobileNumber_len");

        if(!Utils.isStringSafeEmpty(eUser.getEmail()) && !Utils.isValidEmail(eUser.getEmail())/*Email Pattern Check*/)
            validateMessage += (validateMessage.length() > 0 ? "<br>" : "") + Utils.getMessageResource("common.user.email_check");

        if(validateMessage.length() > 0)
            throw new InvalidDataException("InvalidData", validateMessage);

        user.setFirstName(eUser.getFirstName());
        user.setLastName(eUser.getLastName());
        user.setAddress(eUser.getAddress());

        if(!Utils.getUserNameField().equals("mobile"))
            user.setMobileNumber(eUser.getMobileNumber());

        if(!Utils.getUserNameField().equals("email"))
            user.setEmail(eUser.getEmail());

        user.setGender(eUser.getGender());
        if (eUser.getCityId() != null && eUser.getCityId() > 0) {
            City city = cityRepository.findActiveById(eUser.getCityId());
            if (city == null)
                user.setCity(city);
        }
        //	User user = mapToDbUser(eUser, 0);
        if(eUser.getSendNotify()!=null)
            user.setSendNotify(eUser.getSendNotify());
        if(eUser.getSendSms()!=null)
            user.setSendSms(eUser.getSendSms());
        if(eUser.getSendEmail()!=null)
            user.setSendEmail(eUser.getSendEmail());

        user.setAliasName(eUser.getAliasName());
        if(Utils.getAffiliateState() == 1 && Utils.isStringSafeEmpty(user.getAffiliateReagent()) && !Utils.isStringSafeEmpty(eUser.getAffiliateReagent())){//امکان ویرایش کد معرف وجود ندارد
            if(eUser.getAffiliateReagent().equalsIgnoreCase(user.getAffiliateCode()))
                throw new InvalidDataException(user.getAffiliateCode(), "common.user.affiliateCode_invalid");

            User findAffiliateUser = this.getByAffiliateCode(eUser.getAffiliateReagent());//Check Exists AffiliateCode

            user.setAffiliateReagent(eUser.getAffiliateReagent());
        }
        user=userRepository.save(user);

        this.deleteUserDataCache(user.getId(),user.getUsername());

        return Utils.getMessageResource("global.operation.success_info");
    }

    private void deleteUserDataCache(Long userId,String userName){
        cacheService.deleteCacheValue("usersById", userId);
        cacheService.deleteCacheValue("usersByUserName", userName);
        cacheService.deleteCacheValue("userDetailsByUserName", Utils.addGlobalCacheKey(userName));
    }
    private void clearAllUserCacheValues(){
        cacheService.clearCacheValue("usersById");
        cacheService.clearCacheValue("usersByUserName");
        cacheService.clearCacheValue("userDetailsByUserName");
    }

    @Transactional
    @Override
    public String editUser(EUser eUser) {
        return this.editUser(this.getUserInfoForEdit(eUser.getId()),eUser);
    }

    @Transactional
    @Override
    public String fullEditUser(EFUser eFUser) {
        User user =this.getUserInfoForEdit(eFUser.getId());
        if(!eFUser.getId().equals(Utils.getCurrentUserId())) {
            user.setEnabled(eFUser.getEnable());
            if(!Utils.isStringSafeEmpty(eFUser.getExpireDate())) {
                user.setExpireDate(calendarService.getDateTimeAt24(calendarService.getDateFromString(eFUser.getExpireDate())));
            }else{
                user.setExpireDate(null);
            }
        }

        if(eFUser.getLoginType()!=null)
            user.setLoginType(ELoginType.valueOf(eFUser.getLoginType()).getId());
        else
            user.setLoginType(null);

        if (eFUser.getLimitIpList()!=null && eFUser.getLimitIpList().size()>0)
            user.setLimitIp(eFUser.getLimitIpList().stream().map(limitIp->BaseUtils.getIfValidIp(BaseUtils.cleanArabicAndHtmlAndXss(limitIp))).collect(Collectors.joining("|")));
        else
            user.setLimitIp(null);

        this.updateUserRoles(user,eFUser.getRoles());
        return this.editUser(user,eFUser);
    }

    private void updateUserRoles(User user, List<Long> roleIds) {
        if (roleIds == null || roleIds.size() <= 0) {
            user.getRoles().clear();
            return;
        }
        List<Role> forDelete=new ArrayList<>();
        if(user.getRoles()!=null) {
            for (Role role : user.getRoles()) {
                if (roleIds.stream().filter(p -> (role.getId().equals(p))).count() > 0)
                    roleIds = roleIds.stream().filter(p -> (!role.getId().equals(p))).collect(Collectors.toList());
                else {
                    forDelete.add(role);
                }
            }
        }

        if (forDelete.size()>0){
            user.getRoles().removeAll(forDelete);
        }
        for (Long newRoleId : roleIds) {
            Role role= this.getRoleInfo(newRoleId);
            user.getRoles().add(role);
        }
    }


    @Override
    public boolean isAuthUser() {
        return (this.getCurrentUser() != null);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String password) {
        if (Utils.isStringSafeEmpty(user.getPassword()) && Utils.isStringSafeEmpty(password)) return true;
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public String changeUserPassword(ChangePassword changePassword) {
        if (this.isAuthUser()) {
            if (!changePassword.getNewPassword().equals(changePassword.getConfirmNewPassword()))
                throw new InvalidDataException("Invalid Data", "common.user.password_check1");

            final User user = getCurrentUser().getUser();
            if (!this.checkIfValidOldPassword(user, changePassword.getOldPassword())) {
                throw new InvalidDataException(user.getUsername(), "common.user.password_check2");
            }

            if (this.passwordEncoder.matches(changePassword.getNewPassword(), user.getPassword()))
                throw new InvalidDataException(user.getUsername(), "common.user.oldPassword_hint");

            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            user.setCredentialsNonExpired(true);
            user.setCredentialChangeDate(new Date());
            userRepository.save(user);
            this.deleteUserDataCache(user.getId(),user.getUsername());
            return Utils.getMessageResource("global.operation.success_info");
        } else {
            throw new InvalidDataException("Not Found User", "common.user.notFound");
        }
    }

    @Override
    public void goToChangePasswordByEmail(HttpServletResponse response, String token) {
        if (token != null) {
            String username = tokenHelper.getUsernameFromToken(token);
            if (username == null)
                throw new InvalidDataException("Invalid Data", "common.user.resetPasswordToken_invalid");
            Cookie authCookie = Utils.createCookie(TOKEN_COOKIE, token, "/", EXPIRES_IN, true);
            // Add cookie to response
            response.addCookie(authCookie);
            //response.sendRedirect();
            try {
                response.sendRedirect(domainURL + "/panel/index.html#/changepassword/1");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //return "redirect:"+domainURL +"/panel/index.html#/changepassword";
        } else {
            throw new InvalidDataException("Invalid Data", "common.user.resetPasswordToken_required");
        }
    }

    @Override
    public String goToChangePasswordByActiveCode(String userName, Integer activeCode, HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {
        User user1 = this.getUserInfo(userName);
        User user = this.getNewUserForCommitRegister(user1.getUsername());
        if (user == null)
            throw new ResourceNotFoundException("Activation Code Not Found", "common.user.activationCode_notFound");

        if (!user.getActivationCode().equals(activeCode) && !secureCodeGenerator.verifyCode(activeCode.toString(), user.getSecret(),commonService.getVerificationCodeLiveTimeSecond()))
            throw new InvalidDataException(user.getUsername(), "common.user.activationCode_invalid");

        this.resetUserPassword(user1);
        return Utils.getMessageResource("common.user.newPassword_send");

    }

    @Transactional
    public User generateUserSecret(Long userId){
        User user = userRepository.findByEntityId(userId);
        if (user == null)
            throw new ResourceNotFoundException(userId.toString(), "common.user.id_notFound" , userId);
        user.setSecret(Base32.random());
        user =userRepository.save(user);
        this.deleteUserDataCache(user.getId(),user.getUsername());
        return user;
    }

    @Transactional
    @Override
    public Map<String,String> generateAndSend2faCode(C2faRequest c2faRequest) {
        Map<String,String> result=new HashMap<>() ;
        Boolean isVerify=false;
        String verifyResponse=null;

        User user;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(c2faRequest.getUserName());
            user =((UserPrincipalDetails)userDetails).getUser();

            if(!userDetails.isAccountNonLocked() || !userDetails.isEnabled() || !userDetails.isAccountNonExpired())
                throw  new InvalidDataException("loginDisabled","global.wrongUserData");

        }catch (UsernameNotFoundException ex){
            user=null;
        }

        if(user==null)
          Utils.userNameValidate(c2faRequest.getUserName());

        ELoginType eLoginType =(user!=null && user.getLoginType()!=null) ? ELoginType.valueOf(user.getLoginType()): ELoginType.valueOf(commonService.getLoginType());
        if(eLoginType==ELoginType.ONLY_PASSWORD) {
            result.put("status","password");
            result.put("message",Utils.getMessageResource("common.user.userPass_only_hint"));
            return  result;
        }

        if(eLoginType==ELoginType.OTP_PASSWORD && Utils.isStringSafeEmpty(c2faRequest.getPassword())) {
            result.put("status","otpPassword");
            result.put("message",Utils.getMessageResource("common.user.userPass_only_hint"));
            return  result;
        }

        if(user==null &&  (eLoginType==ELoginType.OTP_PASSWORD || eLoginType==ELoginType.ONLY_PASSWORD))
            throw new ResourceNotFoundException(c2faRequest.getUserName(), "common.user.userPass_invalid" );

        if ((eLoginType==ELoginType.OTP_PASSWORD) ){
            if (!this.checkIfValidOldPassword(user, c2faRequest.getPassword()))
               throw new InvalidDataException(user.getUsername(), "common.user.userPass_invalid");
        }

        if(user==null && eLoginType==ELoginType.ONLY_OTP) {
            if (commonService.loginUniqueCodeType()==EUniqueCodeType.NATIONAL_CODE){
                if(Utils.isStringSafeEmpty(c2faRequest.getUniqueCode())) {
                    result.put("status", "uniqueCode");
                    result.put("message", Utils.getMessageResource("common.user.uniqueCode_required"));
                    return result;
                }
                if(!Utils.isValidUniqueCode(c2faRequest.getUniqueCode(),"*"))/*Email Pattern Check*/
                    throw new InvalidDataException("InvalidData", "common.user.uniqueCode_invalid");

                ENotifyType eNotifyType=ENotifyType.valueOf(commonService.getNotifyType());
                if((eNotifyType==ENotifyType.MOBILE || eNotifyType==ENotifyType.MOBILE_EMAIL) ) {
                    if(!Utils.isValidByPattern(c2faRequest.getUserName(), commonService.getMobilePattern()))
                        throw new InvalidDataException("InvalidData", "common.user.mobileNumber_len");

                    //todo Call Shahkar Service
                    if(EUniqueCodeValidationType.valueOf(commonService.getUniqueCodeValidationType())==EUniqueCodeValidationType.SHAHKAR){
                        ShahkarServiceResponse shahkarServiceResponse=shahkarService.callService(ShahkarServiceRequest.builder().serviceType(2).serviceNumber(c2faRequest.getUserName()).identificationType(0).identificationNo(c2faRequest.getUniqueCode()).build());
                        isVerify=true;
                        verifyResponse=shahkarServiceResponse.toString();
                    }
                }
                if (eNotifyType==ENotifyType.EMAIL && !Utils.isValidEmail(c2faRequest.getUserName())) {
                       throw new InvalidDataException("InvalidData", "common.user.email_check");
                }
            }
            user = this.createNewOtpOnlyUser(c2faRequest,isVerify,verifyResponse);
        }

        result= this.generateAndSend2faCode(user);
        if(user!=null && user.getId()==null)
           cacheService.putCacheValue("userRegister", user.getUsername(), user, commonService.getRegisterUserLiveTimeSecond(), TimeUnit.SECONDS);
        return result;
    }

    private User createNewOtpOnlyUser(C2faRequest c2faRequest , Boolean isVerify,String verifyResponse) {
        //if(Utils.getUserNameField().equals("mobile") || Utils.getUserNameField().equals("email")){
        CUser cUser= new CUser();
        cUser.setUsername(c2faRequest.getUserName());
        cUser.setPassword(Utils.createUniqueRandom(8).toString());
        cUser.setGender(1);
        cUser.setAffiliateReagent(c2faRequest.getAffiliateReagent());
        if(Utils.getUserNameField().toLowerCase().equals("mobile"))
            cUser.setMobile(c2faRequest.getUserName());
        if(Utils.getUserNameField().toLowerCase().equals("email"))
            cUser.setEmail(c2faRequest.getUserName());

        if (commonService.loginUniqueCodeType()==EUniqueCodeType.NATIONAL_CODE){
            cUser.setUniqueCode(c2faRequest.getUniqueCode());
        }

        User user = this.createRegisterUser(cUser, 0l,isVerify,verifyResponse);

        return user;
    }



    @Transactional
    @Override
    public Map<String,String> generateAndSend2faCode(User user) {
        Map<String,String> result =new HashMap<>();
        if (user!=null ) {
            ENotifyType eNotifyType=ENotifyType.valueOf(commonService.getNotifyType());

            Date oldSendDate=(Date)cacheService.getCacheValue("2faCodes", user.getUsername());
            if(oldSendDate!=null){
                result.put("status","SendVerificationCode");
                Integer remainTime =commonService.getVerificationCodeLiveTimeSecond() - calendarService.dateDiff(oldSendDate,new Date(), com.core.model.enums.Calendar.SECOND);
                if(remainTime>0) {
                    result.put("validityTime", commonService.getVerificationCodeLiveTimeSecond().toString());
                    result.put("remainTime", remainTime.toString() );
                    result.put("message", Utils.getMessageResource("common.user.activationCode.time_limited", remainTime));
                    return result;
                }
            }

            if (Utils.isStringSafeEmpty(user.getSecret()))
                user=this.generateUserSecret(user.getId());
            String code=secureCodeGenerator.nowCode(user.getSecret(),commonService.getVerificationCodeLiveTimeSecond());

            user.setActivationCode(Integer.valueOf(code));
            String[] message={code};

            if(!Utils.getIsDevMode())
              commonService.sendConfirmCodeAsync(1l,user.getMobileNumber(),user.getEmail(),message,message);

            cacheService.putCacheValue("2faCodes", user.getUsername(), new Date(), commonService.getVerificationCodeLiveTimeSecond(), TimeUnit.SECONDS);
            result.put("status","SendVerificationCode");
            if(Utils.getIsDevMode()) {
                result.put("code", code);
            }
            result.put("remainTime",commonService.getVerificationCodeLiveTimeSecond().toString());
            result.put("message",Utils.getMessageResource("common.user.activationCode_sendNotify" , eNotifyType.getCaption()));
            return  result;
        }
        result.put("status","Error");
        result.put("message",Utils.getMessageResource("common.user.userPass_invalid"));
        return  result;
    }

    @Transactional
    @Override
    public Map<String,String> generateAndSend2faCode(Long userId) {
         return this.generateAndSend2faCode(this.getUserInfo(userId));
    }

    @Override
    public Integer getUserCount() {
        return userRepository.countAll();
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }


    @Override
    public Boolean addBusinessRoleToUser(Long useId) {
        User user = getUserInfo(useId);
        if (user == null)
            throw new ResourceNotFoundException(useId.toString(), "common.user.id_notFound" , useId );

        Role role = roleRepository.findByRoleName("supperbuser");
        if (role == null)
            throw new ResourceNotFoundException("User Role Not Found", "common.userRole.supperbuser_notFound");
        if (!(user.getRoles().contains(role))) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
        this.deleteUserDataCache(user.getId(),user.getUsername());
        return true;
    }

    @Override
    public Boolean changeToNormalUserRole(Long useId) {
        cacheService.deleteCacheValue("usersById", useId);

        User user = getUserInfo(useId);
        if (user == null)
            throw new ResourceNotFoundException(useId.toString(), "common.user.id_notFound" , useId );

        Role normalRole = roleRepository.findByRoleName("normalbuser");
        if (normalRole == null)
            throw new ResourceNotFoundException("User Role Not Found", "common.userRole.normalUser_notFound");
        Role newRole = roleRepository.findByRoleName("newbuser");
        if (newRole == null)
            throw new ResourceNotFoundException("User Role Not Found", "common.userRole.newBUser_notFound");

        user.getRoles().removeIf(r -> r.getId().equals(newRole.getId()));
        if (!(user.getRoles().contains(normalRole))) {
            user.getRoles().add(normalRole);
        }
        userRepository.save(user);
        this.deleteUserDataCache(user.getId(),user.getUsername());
        return true;
    }

    @Override
    public boolean hasRoleType(ERoleType eRoleType) {
        if (!this.isAuthUser())
            return false;
        if(this.getCurrentUser().getRoleType().equals(eRoleType.getId()))
                return true;
        return false;
    }

    @Override
    public boolean hasExistRole(String role) {
        if (!this.isAuthUser())
            return false;
        for (String r : this.getCurrentUser().getRoles()) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasExistRole(Long userId, String role) {
        if (roleRepository.userHasRole(userId, role) > 0)
            return true;
        return false;
    }

    @Override
    public boolean hasExistPermission(String permission) {
        if (!this.isAuthUser())
            return false;
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) this.getCurrentUser().getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (permission.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public UserActions getUserActionInfo(String userActionName) {
        if(Utils.isStringSafeEmpty(userActionName))
            throw new ResourceNotFoundException("entity.userAction_required");

        UserActions userActions =null;
        String cacheKey="ua_"+userActionName;

        userActions = (UserActions) cacheService.getCacheValue("enums", cacheKey);
        if(userActions!=null)
            return userActions;

        userActions = userActionsRepository.findByName(userActionName);
        if (userActions == null)
            throw new ResourceNotFoundException(userActionName, "entity.userAction_notFoundByParam" , userActionName );
        cacheService.putCacheValue("enums", cacheKey, userActions, 15, TimeUnit.DAYS);

        return userActions;
    }

    @Override
    public void createUserLogQueue(EUserActions eUserAction, String ip, String agent, String machineId) {
        HashMap<String, Object> userLogArgs = new HashMap<>();

        UserPrincipalDetails upd = this.getCurrentUser();
        if (upd == null)
            throw new ResourceNotFoundException("User Not Found", "common.user.current_notFound");
        userLogArgs.put("userId", upd.getId());

        userLogArgs.put("eUserActions", eUserAction);
        userLogArgs.put("ip", ip);
        userLogArgs.put("agent", agent);
        userLogArgs.put("machineId", machineId);
        qNotifierService.notify(new QNotifyObject("commonQueue", new MyQueueData(EMyQueueOperation.USER_LOG, userLogArgs)));
    }

    @Override
    @Transactional
    public Boolean createUserLog(Long userId, EUserActions eUserAction, String ip, String agent, String machineId) {
        //User user = this.getUserInfo(userId);
        if (eUserAction == EUserActions.LOGIN) {
            userRepository.modifyLastLoginDate(userId,new Date());
        }

        UserActions userAction = this.getUserActionInfo(eUserAction.toString());

        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setUserAction(userAction);
        userLog.setIp(ip);
        userLog.setAgent(agent);
        userLog.setMachineId(machineId);
        //userLog.setLogDate(logdate);
        userLogRepository.save(userLog);

        return true;
    }


    //#region Role

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAllRoles();
    }

    @Override
    public Role getRoleInfo(Long roleId) {
        if(roleId==null)
          throw new InvalidDataException("Invalid Data", "common.id_required");
        Optional<Role> role= roleRepository.findByEntityId(roleId);
        if (!role.isPresent())
            throw new ResourceNotFoundException(roleId.toString(), "common.id_notFound");
        return  role.get();

    }

    @Override
    public Long createRole(CRole cRole) {
        Role role=new Role();
        this.mapRoleToDb(role,cRole);
        role=roleRepository.save(role);
        return role.getId();

    }
    private Role mapRoleToDb(Role role, CRole cRole){
        if(role.getId()==null){
            if (roleRepository.countByName(cRole.getName()) > 0)
                throw new InvalidDataException("Invalid Data", "common.role.name_exist");
        }else{
            if (roleRepository.countByName(role.getId(),cRole.getName()) > 0)
                throw new InvalidDataException("Invalid Data", "common.role.name_exist");
        }
        ERoleType eRoleType=ERoleType.valueOf(cRole.getRoleType());
        if(cRole.getDefaultRole() && eRoleType==ERoleType.ADMIN)
           throw new InvalidDataException("Invalid Data", "common.role.defaultRole_adminHint");
        role.setRoleName(cRole.getName());
        role.setDescription(cRole.getDescription());
        role.setActive(cRole.getActive());
        role.setDefaultRole(cRole.getDefaultRole());
        role.setRoleType(eRoleType.getId());
        this.updateRoleActivities(role,cRole.getActivities());
        this.updateRoleFileManagerActions(role,cRole.getFileManagerActions());
        return role;
    }

    private void updateRoleActivities(Role role, List<Long> activityIds) {
        if (activityIds == null || activityIds.size() <= 0) {
            role.getActivities().clear();
            return;
        }
        List<Activity> forDelete=new ArrayList<>();
        if(role.getActivities()!=null) {
           for (Activity activity : role.getActivities()) {
               if (activityIds.stream().filter(p -> (activity.getId().equals(p))).count() > 0)
                   activityIds = activityIds.stream().filter(p -> (!activity.getId().equals(p))).collect(Collectors.toList());
               else {
                   forDelete.add(activity);
               }
           }
        }

        if (forDelete.size()>0){
            role.getActivities().removeAll(forDelete);
        }
        for (Long newActivityId : activityIds) {
            Activity activity= this.getActivityInfo(newActivityId);
            //activity.getRoles().add(role);
            role.getActivities().add(activity);
        }
    }

    private void updateRoleFileManagerActions(Role role, List<Long> fileManagerActionIds) {
        if (fileManagerActionIds == null || fileManagerActionIds.size() <= 0) {
            role.getFileManagerActions().clear();
            return;
        }

        List<FileManagerAction> forDelete=new ArrayList<>();
        if(role.getFileManagerActions()!=null) {
            for (FileManagerAction fileManagerAction : role.getFileManagerActions()) {
                if (fileManagerActionIds.stream().filter(p -> (fileManagerAction.getId().equals(p))).count() > 0)
                    fileManagerActionIds = fileManagerActionIds.stream().filter(p -> (!fileManagerAction.getId().equals(p))).collect(Collectors.toList());
                else {
                    forDelete.add(fileManagerAction);
                }
            }
        }

        if (forDelete.size()>0){
            role.getFileManagerActions().removeAll(forDelete);
            //contractBranchBusinessTypeRepository.deleteAll(forDeleteCsbbs);
        }
        for (Long newFileManagerActionId : fileManagerActionIds) {
            FileManagerAction fileManagerAction= commonService.getFileManagerAction(newFileManagerActionId);
            role.getFileManagerActions().add(fileManagerAction);
        }
    }




    @Override
    public String editRole(ERole eRole) {
        Role role=this.getRoleInfo(eRole.getId());
        this.mapRoleToDb(role,eRole);
        role=roleRepository.save(role);

        this.clearAllUserCacheValues();
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Override
    public String deleteRole(Long roleId) {
        Role role=this.getRoleInfo(roleId);
        if (role.getUsers()!=null && role.getUsers().size()>0)
            throw new InvalidDataException("Invalid Data", "common.role.delete_user_related");
        if ((role.getActivities()!=null && role.getActivities().size()>0) || (role.getFileManagerActions()!=null && role.getFileManagerActions().size()>0))
            throw new InvalidDataException("Invalid Data", "common.role.delete_activity_related");
        roleRepository.delete(role);
        return Utils.getMessageResource("global.operation.success_info");
    }


    //#endregion



    //#region Activity

    @Override
    public Activity getActivityInfo(Long activityId) {
        if(activityId==null)
            throw new InvalidDataException("Invalid Data", "common.activity.id_required");
        Optional<Activity> activity= activityRepository.findByEntityId(activityId);
        if (!activity.isPresent())
            throw new ResourceNotFoundException(activityId.toString(), "common.activity.id_notFound");
        return  activity.get();
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findActivities();
    }

    @Transactional
    @Override
    public String editActivity(EActivity eActivity) {
        Activity activity=this.getActivityInfo(eActivity.getId());
        this.mapActivityToDb(activity,eActivity);
        activity=activityRepository.save(activity);
        return Utils.getMessageResource("global.operation.success_info");

    }

    private Activity mapActivityToDb(Activity activity, CActivity cActivity){

        activity.setTitle(cActivity.getTitle());
        activity.setDescription(cActivity.getDescription());
        activity.setIcon(cActivity.getIcon());
        activity.setBlank(cActivity.getBlank());
        activity.setModal(cActivity.getModal());
        activity.setActive(cActivity.getActive());

        this.updateActivityPermissions(activity,cActivity.getPermissions());
        return activity;
    }

    private void updateActivityPermissions(Activity activity, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.size() <= 0) {
            activity.getPermissions().clear();
            return;
        }
        List<Permission> forDelete=new ArrayList<>();
        if(activity.getPermissions()!=null) {
            for (Permission permission: activity.getPermissions()) {
                if (permissionIds.stream().filter(p -> (permission.getId().equals(p))).count() > 0)
                    permissionIds = permissionIds.stream().filter(p -> (!permission.getId().equals(p))).collect(Collectors.toList());
                else {
                    forDelete.add(permission);
                }
            }
        }

        if (forDelete.size()>0){
            activity.getPermissions().removeAll(forDelete);
        }
        for (Long newPermissionId : permissionIds) {
            Permission permission= this.getPermissionInfo(newPermissionId);
            activity.getPermissions().add(permission);
        }
    }

    //#endregion

    //#region Permission

    @Override
    public Permission getPermissionInfo(Long permissionId) {
        if(permissionId==null)
            throw new InvalidDataException("Invalid Data", "common.permission.id_required");
        Optional<Permission> permission= permissionRepository.findByEntityId(permissionId);
        if (!permission.isPresent())
            throw new ResourceNotFoundException(permission.toString(), "common.permission.id_notFound");
        return  permission.get();
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllPermissions();
    }


    //#endregion


    //#region User Group

    @Override
    public List<UserGroup> getAllUserGroups(Long userId) {
        if(hasRoleType(ERoleType.ADMIN))
            return userGroupRepository.findAllUserGroup(userId);
        else
            return  userGroupRepository.findAllUserGroup(Utils.getCurrentUserId());
    }

    @Override
    public List<UserGroup> getAllActiveUserGroups(Long userId) {
        if(userId==null)
            userId=Utils.getCurrentUserId();
        if(hasRoleType(ERoleType.ADMIN))
            return userGroupRepository.findAllActiveUserGroup(userId);
        else
            return userGroupRepository.findAllActiveUserGroup(Utils.getCurrentUserId());
    }

    @Override
    public UserGroup getUserGroupInfo(Long userGroupId/*,Long branchId*/) {
        if(userGroupId == null)
            throw new InvalidDataException("Invalid Data", "common.userGroup.id_required");
        Optional<UserGroup> result=null;
        if(hasRoleType(ERoleType.ADMIN) )
            result= userGroupRepository.findByEntityId(userGroupId);
        else
            result= userGroupRepository.findByEntityId(userGroupId,Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException(userGroupId.toString(), "common.userGroup.id_notFound" , userGroupId );

        /*if(!hasRoleType(ERoleType.ADMIN)) {
            if (result.get().getTargetTypeId() == ((Integer) ETargetTypes.STORE_BRANCH.value()).longValue() &&
                    !commonService.checkExistTarget(ETargetTypes.STORE_BRANCH, result.get().getTargetId(), true, null))
                throw new ResourceNotFoundException(userGroupId.toString(), "common.userGroup.id_notFound", userGroupId);
            else if (result.get().getTargetTypeId() == ((Integer) ETargetTypes.USER.value()).longValue() && !result.get().getTargetId().equals(Utils.getCurrentUserId()))
                throw new ResourceNotFoundException(userGroupId.toString(), "common.userGroup.id_notFound", userGroupId);
        }*/
        return result.get();
    }

    @Override
    public UserGroup getActiveUserGroupInfo(Long userGroupId/*, Long branchId*/) {
        if(userGroupId == null)
            throw new InvalidDataException("Invalid Data", "common.userGroup.id_required");
        Optional<UserGroup> result=null;
        if(hasRoleType(ERoleType.ADMIN) )
            result= userGroupRepository.findActiveByEntityId(userGroupId);
        else
            result= userGroupRepository.findActiveByEntityId(userGroupId,Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException(userGroupId.toString(), "common.userGroup.id_notFound" , userGroupId );
        return result.get();
    }

    @Transactional
    @Override
    public Long createUserGroup(UserGroupDto userGroupDto) {
        UserGroup userGroup= new UserGroup();
        userGroup.setTargetTypeId(((Integer)ETargetTypes.USER.value()).longValue());
        if(hasRoleType(ERoleType.ADMIN))
            userGroup.setTargetId(this.getUserInfo(userGroupDto.getUserId()).getId());
        else
            userGroup.setTargetId(Utils.getCurrentUserId());
        //}
        userGroup =this.mapUserGroupToDb(userGroup,userGroupDto);
        userGroup=userGroupRepository.save(userGroup);
        return userGroup.getId();
    }

    @Transactional
    @Override
    public String editUserGroup(UserGroupDto userGroupDto) {
        UserGroup userGroup= this.getUserGroupInfo(userGroupDto.getId());
        userGroup =this.mapUserGroupToDb(userGroup,userGroupDto);
        userGroup=userGroupRepository.save(userGroup);
        return Utils.getMessageResource("global.operation.success_info");
    }

    private UserGroup mapUserGroupToDb(UserGroup userGroup,UserGroupDto userGroupDto){
        if (userGroup.getId() != null && userGroupRepository.countByName(userGroup.getId(),userGroup.getTargetId(),userGroupDto.getName()) > 0){
           throw new InvalidDataException("Invalid Data", "common.userGroup.name_exist");
        }else if (userGroup.getId() == null && userGroupRepository.countByName(userGroup.getTargetId(),userGroupDto.getName()) > 0){
           throw new InvalidDataException("Invalid Data", "common.userGroup.name_exist");
        }
        userGroup.setName(userGroupDto.getName());
        userGroup.setDescription(userGroupDto.getDescription());
        userGroup.setActive(userGroupDto.getActive());
        return userGroup;
    }

    @Transactional
    @Override
    public String deleteUserGroup(Long userGroupId) {
        UserGroup userGroup= this.getUserGroupInfo(userGroupId);
        if(userGroupMemberRepository.existsByGroupId(userGroup.getId()))
           throw new InvalidDataException("Invalid Data", "common.userGroup.delete_userExist");
        if(userGroupRepository.hasUsedByGroupId(userGroup.getId()))
            throw new InvalidDataException("Invalid Data", "common.userGroup.delete_usedHint");
        userGroupRepository.delete(userGroup);
        return Utils.getMessageResource("global.delete_info");

    }

    @Override
    public ResultListPageable<Long> getUserIdsByGroupId(Long userGroupId,Integer start, Integer count) {
        List<Long> result=userGroupMemberRepository.findUserIdsByGroupId(userGroupId,gotoPage(start,count+1));
        Boolean hasNext=false;
        if  (result.size() > count) {
            hasNext = true;
            result.remove(result.size()-1);
        }
        ResultListPageable<Long> resultListPageable =new ResultListPageable<Long>(result,hasNext);

        return resultListPageable;
    }

    @Override
    public List<String> getUserNamesByGroupId(Long userGroupId, Integer start, Integer count) {
        if(hasRoleType(ERoleType.ADMIN))
            return userGroupMemberRepository.findUserNamesByGroupId(userGroupId,gotoPage(start,count));
        else
            return userGroupMemberRepository.findUserNamesByGroupId(userGroupId,Utils.getCurrentUserId(),gotoPage(start,count));
    }

    @Override
    public UserGroupMember getUserGroupMemberInfo(Long userGroupId, Long userId) {
        if (userGroupId==null || userId==null)
            throw new InvalidDataException("Invalid Data", "common.userGroupMember.userGroupIdAndUserId_required");
        Optional<UserGroupMember> result= userGroupMemberRepository.findByGroupIdAndUserId(userGroupId,userId);
        if (!result.isPresent())
            return null;
            //throw new ResourceNotFoundException(userGroupId.toString(), "common.userGroup.id_notFound" , userGroupId );
        return result.get();
    }

    @Transactional
    @Override
    public Long addGroupMember(Long userGroupId, Long userId) {
        UserGroup userGroup= this.getUserGroupInfo(userGroupId);
        User user=this.getUserInfo(userId);
        return this.addGroupMember(userGroup,user.getId());
    }

    private Long addGroupMember(UserGroup userGroup, Long userId) {
        UserGroupMember userGroupMember= this.getUserGroupMemberInfo(userGroup.getId(),userId);
        if(userGroupMember!=null)
            throw new InvalidDataException("Invalid Data", "common.userGroupMember.userGroupIdAndUserId_exist");
        userGroupMember=new UserGroupMember();
        userGroupMember.setUserGroup(userGroup);
        userGroupMember.setUserId(userId);
        userGroupMember=userGroupMemberRepository.save(userGroupMember);
        return userGroupMember.getId();
    }

    @Transactional
    @Override
    public Long addGroupMember(Long userGroupId, String userName) {
        UserGroup userGroup= this.getUserGroupInfo(userGroupId);
        User user=this.getUserInfo(userName);
        return this.addGroupMember(userGroup,user.getId());
    }

    @Transactional
    @Override
    public String deleteGroupMember(Long userGroupId, Long userId) {
        UserGroup userGroup= this.getUserGroupInfo(userGroupId);
        User user=this.getUserInfo(userId);
        return  this.deleteGroupMember(userGroup,user.getId());
    }

    @Transactional
    @Override
    public String deleteGroupMember(Long userGroupId, String userName) {
        UserGroup userGroup= this.getUserGroupInfo(userGroupId);
        User user=this.getUserInfo(userName);
        return  this.deleteGroupMember(userGroup,user.getId());
    }

    private String deleteGroupMember(UserGroup userGroup, Long userId) {
        UserGroupMember userGroupMember= this.getUserGroupMemberInfo(userGroup.getId(),userId);
        if(userGroupMember==null)
            throw new InvalidDataException("Invalid Data", "common.userGroupMember.notFound");
        userGroupMemberRepository.delete(userGroupMember);
        return Utils.getMessageResource("global.delete_info");
    }

    //#endregion





    //#region User Dynamic Query
    @Override
    public ResultListPageable<User> getUsers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =searchCriteriaParser.parseSortParams(USER_SEARCH_MAP_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);
        ResultListPageable<User> userResultListPageable=this.getUsersGeneral(requestParams);
        return userResultListPageable;
    }


    private ResultListPageable<User> getUsersGeneral(Map<String, Object> requestParams) {
        ResultListPageable<User> result=null;
        if(!hasRoleType(ERoleType.ADMIN))
            return result;

        requestParams.put("nativeQuery",false);

        List<SearchCriteria> whereClauseParams = searchCriteriaParser.parseParams(USER_SEARCH_MAP_PARAMS, requestParams);
        String queryString = "select u from User u  \n";
        String countQueryString ="";

        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString="select count(u) from User u \n";

        List<String> sortParams = (List) requestParams.get("sortParams");
        List<String> groupParams =null;

        Integer start = Utils.getAsIntegerFromMap(requestParams, "start", false,0);
        Integer count = Utils.getAsIntegerFromMap(requestParams, "count", false,commonService.getDefaultQueryCount());

        result = userDynamicQueryRepository.runDynamicQueryWithPaging(queryString,countQueryString, whereClauseParams, start, count, sortParams,false,"",groupParams,User.class);
        return result;
    }

    //#endregion User Dynamic Query

}

