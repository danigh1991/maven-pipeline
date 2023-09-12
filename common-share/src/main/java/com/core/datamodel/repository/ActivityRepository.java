package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Activity;
import com.core.datamodel.model.dbmodel.Role;
import com.core.datamodel.model.wrapper.ActivityWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends BaseRepository<Activity, Long> {

    @Query("select a from Activity a left join a.permissions where a.id =:activityId")
    Optional<Activity> findByEntityId(@Param("activityId") Long activityId);

    @Query(nativeQuery = true)
    List<ActivityWrapper> findPanelMenuByRoleIds(@Param("panelType") Integer panelType, @Param("roleIds") List<Long> roleId);

    @Query(nativeQuery = true)
    List<ActivityWrapper> findChildPanelMenuByRoleIds(@Param("parentId") Long parentId, @Param("panelType") Integer panelType, @Param("roleIds") List<Long> roleId);

    @Query("select a from Activity a where a.parent is null order by a.panelType,a.order")
    List<Activity> findActivities();

}
