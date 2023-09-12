package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.UserGroup;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends BaseRepository<UserGroup, Long> {

    @Query("select s  from UserGroup s where s.id =:id")
    Optional<UserGroup> findByEntityId(@Param("id") Long id);

    @Query("select s  from UserGroup s where s.id =:id and (s.targetTypeId=31 and s.targetId=:userId)")
    Optional<UserGroup> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select s  from UserGroup s where s.id =:id and s.active=true")
    Optional<UserGroup> findActiveByEntityId(@Param("id") Long id);

    @Query("select s  from UserGroup s where s.id =:id and s.active=true and (s.targetTypeId=31 and s.targetId=:userId)")
    Optional<UserGroup> findActiveByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select s  from UserGroup s where (s.targetTypeId=31 and s.targetId=:userId) order by s.id desc")
    List<UserGroup> findAllUserGroup(@Param("userId") Long userId);

    @Query("select s  from UserGroup s where s.active=true and (s.targetTypeId=31 and s.targetId=:userId) order by s.id desc")
    List<UserGroup> findAllActiveUserGroup(@Param("userId") Long userId);

    @Query(value = "select count(s) from UserGroup s where (s.targetTypeId=31 and s.targetId=:userId)  and FUNCTION('REPLACE', s.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countByName(@Param("userId") Long userId, @Param("name") String name);

    @Query(value = "select count(s) from UserGroup s where s.id<>:userGroupId and (s.targetTypeId=31 and s.targetId=:userId)  and FUNCTION('REPLACE', s.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countByName(@Param("userGroupId") Long userGroupId,@Param("userId") Long userId, @Param("name") String name);


    @Query(value = "select CASE WHEN count(*)>0 THEN 'true' ELSE 'false' END from sc_user_group g\n" +
                   " INNER JOIN sc_account_merchant_limit aml ON (aml.aml_type=1 AND aml.aml_target_id=g.usg_id)\n" +
                   " WHERE g.usg_id=:groupId",nativeQuery = true)
    Boolean hasUsedByGroupId(@Param("groupId") Long groupId);

}
