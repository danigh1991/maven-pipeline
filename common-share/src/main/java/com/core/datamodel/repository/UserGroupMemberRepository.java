package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.UserGroupMember;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGroupMemberRepository extends BaseRepository<UserGroupMember, Long> {

    @Query("select s  from UserGroupMember s where s.id =:id")
    Optional<UserGroupMember> findByEntityId(@Param("id") Long id);

    @Query("select s  from UserGroupMember s where s.userGroup.id =:groupId and s.userId=:userId ")
    Optional<UserGroupMember> findByGroupIdAndUserId(@Param("groupId") Long groupId,@Param("userId") Long userId);

    @Query("select CASE WHEN count(m)>0 THEN true ELSE false END from UserGroupMember m where m.userGroup.id=:groupId")
    Boolean existsByGroupId(@Param("groupId") Long groupId);


    @Query(value = "select m.userId from UserGroupMember m  where m.userGroup.id=:groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId, Pageable pageable);


    @Query(value = "select u.username from UserGroupMember m join m.userGroup g ,User u where u.id=m.userId and g.id=:groupId")
    List<String> findUserNamesByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    @Query(value = "select u.username from UserGroupMember m join m.userGroup g ,User u where u.id=m.userId and g.id=:groupId and (g.targetTypeId=31 and g.targetId=:userId) ")
    List<String> findUserNamesByGroupId(@Param("groupId") Long groupId,@Param("userId") Long userId, Pageable pageable);

}
