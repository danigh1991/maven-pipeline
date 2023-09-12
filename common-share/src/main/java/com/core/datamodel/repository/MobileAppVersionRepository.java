package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.MobileAppVersion;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MobileAppVersionRepository extends BaseRepository<MobileAppVersion, Long> {

    @Query("select t from MobileAppVersion t where t.id=:id ")
    MobileAppVersion findByEntityId(@Param("id") Long id);

    @Query("select t from MobileAppVersion t where t.id=:id  and t.active=true")
    MobileAppVersion findActiveById(@Param("id") Long id);

   /* @Query("select max(cast(FUNCTION('REPLACE', t.appVersion,'.','') as int)) from MobileAppVersion t  where cast(FUNCTION('REPLACE', t.appVersion,'.','') as int) >=:userVersion")
    String findLastActiveAppVersion();*/


    @Query("select t from MobileAppVersion t order by t.id")
    List<MobileAppVersion> findAllMobileAppVersions();

    @Query("select t from MobileAppVersion t where t.active=true order by t.id")
    List<MobileAppVersion> findAllActiveMobileAppVersions();


    @Query("select t from MobileAppVersion t where CAST(FUNCTION('REPLACE', t.appVersion,'.','') as integer)=(select max(CAST(FUNCTION('REPLACE', t1.appVersion,'.','') as integer)) from MobileAppVersion t1 where t1.active=true )")
    Optional<MobileAppVersion> findLastActiveMobileAppVersion();

}
