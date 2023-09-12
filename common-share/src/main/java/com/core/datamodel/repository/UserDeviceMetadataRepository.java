package com.core.datamodel.repository;


import com.core.datamodel.model.dbmodel.UserDeviceMetadata;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserDeviceMetadataRepository extends BaseRepository<UserDeviceMetadata, Long> {

    @Query("select ud  from UserDeviceMetadata ud where ud.id =:id")
    UserDeviceMetadata findByEntityId(@Param("id") Long id);

    @Query("select ud  from UserDeviceMetadata ud where ud.id =:id and ud.userId=:userId")
    UserDeviceMetadata findByEntityId(@Param("id") Long id,@Param("userId") Long userId);

    @Query("select ud  from UserDeviceMetadata ud where ud.refreshToken=:refreshToken")
    Optional<UserDeviceMetadata> findByRefreshToken(@Param("refreshToken") String refreshToken);



    @Query("select ud from UserDeviceMetadata ud where ud.userId=:userId and ud.ip=:ip and ud.deviceDetail=:deviceDetail")
    UserDeviceMetadata findByUserIdAndIpAndDetail(@Param("userId") Long userId,@Param("ip") String ip,@Param("deviceDetail")  String deviceDetail);

    @Query("select ud  from UserDeviceMetadata ud where ud.userId=:userId")
    List<UserDeviceMetadata> findAllByUserId(@Param("userId") Long userId);


    @Query("select CASE WHEN COUNT(ud) > 0 THEN true ELSE false END  from UserDeviceMetadata ud where ud.userId=:userId and ud.ip=:ip and ud.deviceDetail=:deviceDetail")
    Boolean existByUserIdAndIpAndDetail(@Param("userId") Long userId,@Param("ip") String ip,@Param("deviceDetail")  String deviceDetail);

    @Query("select CASE WHEN COUNT(ud) > 0 THEN true ELSE false END  from UserDeviceMetadata ud where ud.id=:userDeviceMetadataId and ud.expireDate>=:dt")
    Boolean existByUserDeviceMetadataId(@Param("userDeviceMetadataId") Long userDeviceMetadataId,@Param("dt") Date dt);

    @Query("select  COUNT(ud) from UserDeviceMetadata ud where ud.userId=:userId and (ud.lastRequestDate<:allowedIdleDate or ud.expireDate<:currentDate)")
    Integer countDeleteCandidate(@Param("userId") Long userId,@Param("allowedIdleDate") Date allowedIdleDate,@Param("currentDate") Date currentDate);

    @Query("select  COUNT(ud) from UserDeviceMetadata ud where ud.userId=:userId and ud.expireDate<:currentDate")
    Integer countDeleteCandidate(@Param("userId") Long userId,@Param("currentDate") Date currentDate);

    @Transactional
    @Modifying
    @Query("delete from UserDeviceMetadata ud where ud.userId=:userId and (ud.lastRequestDate<:allowedIdleDate or ud.expireDate<:currentDate)")
    Integer deleteExpireAndIdleUserDevice(@Param("userId") Long userId,@Param("allowedIdleDate") Date allowedIdleDate,@Param("currentDate") Date currentDate);

    @Transactional
    @Modifying
    @Query("delete from UserDeviceMetadata ud where ud.userId=:userId and ud.expireDate<:currentDate")
    Integer deleteExpireUserDevice(@Param("userId") Long userId,@Param("currentDate") Date currentDate);


    @Transactional
    @Modifying
    @Query("update UserDeviceMetadata set lastRequestDate=:dt where id=:userDeviceMetadataId")
    Integer updateLastRequestDateByUserDeviceMetadataId(@Param("userDeviceMetadataId") Long userDeviceMetadataId,@Param("dt") Date dt);

    @Query("select count(ud) from UserDeviceMetadata ud where ud.userId=:userId")
    Integer findUserDeviceCount(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserDeviceMetadata ud where ud.userId=:userId and ud.id=(select min(d.id) from UserDeviceMetadata d where d.userId=:userId)")
    Integer deleteFirstUserDevice(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserDeviceMetadata ud where ud.userId=:userId and ud.id=(select max(d.id) from UserDeviceMetadata d where d.userId=:userId)")
    Integer deleteLastUserDevice(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserDeviceMetadata ud where ud.userId=:userId")
    Integer deleteAllUserDevice(@Param("userId") Long userId);


}
