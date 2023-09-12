package com.core.common.services.impl;

import com.core.common.model.enums.EUserConcurrentStrategy;
import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.UserDeviceMetadata;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.repository.UserDeviceMetadataRepository;
import com.core.services.CalendarService;
import com.core.exception.ResourceNotFoundException;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class DeviceService {
    private static final String UNKNOWN = "UNKNOWN";
    private UserDeviceMetadataRepository userDeviceMetadataRepository;
    private DatabaseReader databaseReader;
    private CommonService commonService;
    private Parser parser;

    @Value("#{'${app.main_ips}'.split(',')}")
    List<String> mainIps;


    @Autowired
    public DeviceService(UserDeviceMetadataRepository userDeviceMetadataRepository,Parser parser,CommonService commonService/*, DatabaseReader databaseReader*/) {
        this.userDeviceMetadataRepository = userDeviceMetadataRepository;
        this.parser = parser;
        this.commonService = commonService;

        //this.databaseReader = databaseReader;
    }

    public Boolean verifyDevice(Long userId, HttpServletRequest request,String tokenDeviceMetadata,Long tokenDeviceMetadataId,String tokenIp,Boolean ipCheck){
        String requestIp = Utils.getClientFirstIp(request);

        //For server Ips (check file manager access Control)
        if(request.getRequestURI().startsWith("/api/admin/getFileManagerConfig")  && mainIps.contains(requestIp))
            return true;

        String requestDeviceMetadata = this.getDeviceDetails(request.getHeader("user-agent"));
        if((ipCheck && !requestIp.equalsIgnoreCase(tokenIp)) || !requestDeviceMetadata.equalsIgnoreCase(tokenDeviceMetadata))
            return false;

        this.removeIdleAndExpireClient(userId);

        //return this.existByUserIdAndIpAndDetail(userId, tokenIp, tokenDeviceMetadata);
        return this.existByUserDeviceMetadataId(tokenDeviceMetadataId);
    }

    public void removeIdleAndExpireClient(Long userId){
        Integer allowedIdleTime=commonService.getAllowedIdleTime();
        Date currentDate=new Date();
        if(allowedIdleTime>0) {
            Date allowedIdleDate=Utils.addSecondToDate(currentDate,-allowedIdleTime);
            if(userDeviceMetadataRepository.countDeleteCandidate(userId,allowedIdleDate,currentDate)>0)
                userDeviceMetadataRepository.deleteExpireAndIdleUserDevice(userId,allowedIdleDate,currentDate);
        }else {
            if(userDeviceMetadataRepository.countDeleteCandidate(userId,currentDate)>0)
                userDeviceMetadataRepository.deleteExpireUserDevice(userId,currentDate);
        }
    }


    public String getDeviceDetails(String userAgentString) {
        String deviceMetadata = UNKNOWN;
        StringBuilder deviceMetadataBuilder=new StringBuilder();

        Client client = parser.parse(userAgentString);
        if (Objects.nonNull(client)) {
            deviceMetadataBuilder.append(client.userAgent.family + " ");
            deviceMetadataBuilder.append(!Utils.isStringSafeEmpty(client.userAgent.major)? client.userAgent.major: "");
            deviceMetadataBuilder.append(!Utils.isStringSafeEmpty(client.userAgent.minor)? "." + client.userAgent.minor: "");
            deviceMetadataBuilder.append(!Utils.isStringSafeEmpty(client.userAgent.patch)? "." + client.userAgent.patch: "");
            deviceMetadataBuilder.append(!Utils.equalsStrings(client.userAgent.family,client.os.family)? " - " + client.os.family + " ": "");
            deviceMetadataBuilder.append(!Utils.equalsStrings(client.userAgent.family,client.os.family)? (!Utils.isStringSafeEmpty(client.os.major)? client.os.major: ""): "");
            deviceMetadataBuilder.append(!Utils.equalsStrings(client.userAgent.family,client.os.family)? (!Utils.isStringSafeEmpty(client.os.minor)? "."+client.os.minor: ""): "");
            deviceMetadataBuilder.append(!Utils.isStringSafeEmpty(client.device.family)? " - "+client.device.family: "");
            deviceMetadata=deviceMetadataBuilder.toString();
        }

/*        UserAgent userAgent=UserAgent.parseUserAgentString(userAgentString);
        if (Objects.nonNull(userAgent))
            deviceMetadata=(userAgent.getOperatingSystem()!=null? userAgent.getOperatingSystem().toString():"noneOperatingSystem")+" "+ (userAgent.getBrowser()!=null ?userAgent.getBrowser().toString():"noneBrowser")+"."+(userAgent.getBrowserVersion()!=null?userAgent.getBrowserVersion().toString():"noneBrowserVersion");*/
        return deviceMetadata;
    }

    @Transactional
    public UserDeviceMetadata setRefreshTokenForDevice(Long deviceMetadataId,String refreshToken) {
        UserDeviceMetadata userDeviceMetadata=userDeviceMetadataRepository.findByEntityId(deviceMetadataId);
        userDeviceMetadata.setRefreshToken(refreshToken);
        userDeviceMetadata=userDeviceMetadataRepository.save(userDeviceMetadata);
        return userDeviceMetadata;
    }

        @Transactional
    public UserDeviceMetadata registerDevice(Long userId,Date expireDate, HttpServletRequest request)  {
        String ip = Utils.getClientFirstIp(request);
        String location = getIpLocation(ip);
        String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));
        //UserDeviceMetadata userDeviceMetadata = this.findDeviceInfo(userId, ip, deviceDetails);
        //if (Objects.isNull(userDeviceMetadata)) {
        if(commonService.getUserConcurrentLoginCount()>0 && this.getUserDeviceCount(userId)>=commonService.getUserConcurrentLoginCount()) {
            if(commonService.getUserConcurrentStrategy()== EUserConcurrentStrategy.LOGIN_DENY)
               throw new BadCredentialsException(Utils.getMessageResource("global.user_concurrent_login_hint"));
            else if(commonService.getUserConcurrentStrategy()== EUserConcurrentStrategy.EJECT_ALL)
                this.deleteAllUserDevice(userId);
            else if(commonService.getUserConcurrentStrategy()== EUserConcurrentStrategy.EJECT_FIRST)
                this.deleteFirstUserDevice(userId);
            else if(commonService.getUserConcurrentStrategy()== EUserConcurrentStrategy.EJECT_LAST)
                this.deleteLastUserDevice(userId);
        }
        unknownDeviceNotification(userId, deviceDetails, location, ip);
        UserDeviceMetadata userDeviceMetadata = new UserDeviceMetadata();
        userDeviceMetadata.setUserId(userId);
        userDeviceMetadata.setIp(ip);
        userDeviceMetadata.setLocation(location);
        userDeviceMetadata.setDeviceDetail(deviceDetails);
        Date currentDate=new Date();
        userDeviceMetadata.setLastLoggedDate(currentDate);
        userDeviceMetadata.setLastRequestDate(currentDate);
        userDeviceMetadata.setExpireDate(expireDate);
        userDeviceMetadata=userDeviceMetadataRepository.save(userDeviceMetadata);
        /*} else {
            userDeviceMetadata.setLastLoggedDate(new Date());
            userDeviceMetadata=userDeviceMetadataRepository.save(userDeviceMetadata);
        }*/
        //return deviceDetails;
        return userDeviceMetadata;
    }

    @Transactional
    public void removeDeviceByUserId(Long userId, HttpServletRequest request) {
        String ip = Utils.getClientFirstIp(request);
        String location = getIpLocation(ip);
        String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));
        UserDeviceMetadata userDeviceMetadata = this.findDeviceInfo(userId, ip, deviceDetails);
        if (!Objects.isNull(userDeviceMetadata)) {
            userDeviceMetadataRepository.delete(userDeviceMetadata);
        }
    }

    @Transactional
    public void removeDeviceById(Long deviceMetadataId) {
        //userDeviceMetadataRepository.deleteById(deviceMetadataId);
        UserDeviceMetadata userDeviceMetadata=userDeviceMetadataRepository.findByEntityId(deviceMetadataId);
        if (!Objects.isNull(userDeviceMetadata)) {
            userDeviceMetadataRepository.delete(userDeviceMetadata);
        }
    }

    private void removeUserDeviceMetadata(UserDeviceMetadata userDeviceMetadata) {
        if (!Objects.isNull(userDeviceMetadata)) {
            userDeviceMetadataRepository.delete(userDeviceMetadata);
        }
    }

    @Transactional
    public String removeDevice(Long deviceMetadataId) {
        UserDeviceMetadata userDeviceMetadata=this.findDeviceInfo(deviceMetadataId);
        this.removeUserDeviceMetadata(userDeviceMetadata);
        return Utils.getMessageResource("global.delete_info");
    }

    public UserDeviceMetadata findDeviceInfo(String refreshToken) {
        if(Utils.isStringSafeEmpty(refreshToken))
            throw new ResourceNotFoundException("Invalid Data"  , "common.refreshToken_required");
        Optional<UserDeviceMetadata> result=userDeviceMetadataRepository.findByRefreshToken(refreshToken);

        if(!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data"  , "common.refreshToken_notFound");
        return result.get();
    }

    public UserDeviceMetadata findDeviceInfo(Long deviceMetadataId) {
        if(deviceMetadataId==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_required");
        UserDeviceMetadata userDeviceMetadata=null;
        if(Utils.hasRoleType(ERoleType.ADMIN))
            userDeviceMetadata=userDeviceMetadataRepository.findByEntityId(deviceMetadataId);
        else
            userDeviceMetadata=userDeviceMetadataRepository.findByEntityId(deviceMetadataId,Utils.getCurrentUserId());

        if(userDeviceMetadata==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");
        return userDeviceMetadata;
    }

    public List<UserDeviceMetadata> getAllDeviceByUserId(Long userId) {
        if(Utils.hasRoleType(ERoleType.ADMIN))
           return userDeviceMetadataRepository.findAllByUserId(userId);
        else
           return userDeviceMetadataRepository.findAllByUserId(Utils.getCurrentUserId());
    }



    public Integer getUserDeviceCount(Long userId) {
        return userDeviceMetadataRepository.findUserDeviceCount(userId);
    }
    @Transactional
    public void deleteFirstUserDevice(Long userId) {
         userDeviceMetadataRepository.deleteFirstUserDevice(userId);
    }
    @Transactional
    public void deleteLastUserDevice(Long userId) {
         userDeviceMetadataRepository.deleteLastUserDevice(userId);
    }
    @Transactional
    public void deleteAllUserDevice(Long userId) {
         userDeviceMetadataRepository.deleteAllUserDevice(userId);
    }

    public UserDeviceMetadata findDeviceInfo(Long userId, String ip, String deviceDetails) {
       return userDeviceMetadataRepository.findByUserIdAndIpAndDetail(userId,ip,deviceDetails);
    }

    public Boolean existByUserIdAndIpAndDetail(Long userId ,String ip, String deviceDetails) {
       return userDeviceMetadataRepository.existByUserIdAndIpAndDetail(userId,ip, deviceDetails);
    }

    public Boolean existByUserDeviceMetadataId(Long userDeviceMetadataIdId) {
       Date currentDate=new Date();
       Boolean existDeviceMetaData= userDeviceMetadataRepository.existByUserDeviceMetadataId(userDeviceMetadataIdId,currentDate);
       if(existDeviceMetaData)
           userDeviceMetadataRepository.updateLastRequestDateByUserDeviceMetadataId(userDeviceMetadataIdId,new Date());

       return existDeviceMetaData;
    }

    private String getIpLocation(String ip)  {
        //try{
            String location = UNKNOWN;
/*
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse cityResponse = databaseReader.city(ipAddress);
            if (Objects.nonNull(cityResponse) &&
                    Objects.nonNull(cityResponse.getCity()) &&
                    !Strings.isNullOrEmpty(cityResponse.getCity().getName())) {

                location = cityResponse.getCity().getName();
            }
*/
            return location;
        /*}catch (IOException ie){
            ie.printStackTrace();
            throw new InvalidDataException("Invalid Data", "global.exception");
        }catch ( GeoIp2Exception ge){
            ge.printStackTrace();
            throw new InvalidDataException("Invalid Data", "global.exception");
        }*/
    }

    private void unknownDeviceNotification(Long userId,String deviceDetails, String location, String ip) {

        //todo send new Login notify to User
    }


}
