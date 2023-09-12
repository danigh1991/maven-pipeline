package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Notification;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

public interface NotificationRepository extends BaseRepository<Notification, Long> {

    List<Notification> findByStatus(String status,  Pageable pageable);

    @Query("select n from Notification n  where n.status='NEW' and (n.medium='EMAIL' or n.medium='SMS'  or n.medium='BASE_SMS') order by n.createDate asc")
    List<Notification> findNewEmailAndSmsNotification(Pageable pageable);


    @Query("select distinct n.recipientUserId from Notification n  where n.status='NEW' and n.medium='POPUP' order by n.createDate asc")
    List<Long> findNewPopupNotification(Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Notification n set n.status = :status , n.notifyDate= :notifyDate  where n.id in :notifyIds")
    Integer editNotificationByIds(@Param("status") String status, @Param("notifyDate") Date notifyDate, @Param("notifyIds") List<Long> notifyIds);

    @Query("select count(n) from Notification n where n.status='NEW' and n.medium='POPUP' and n.recipientUserId=:userId")
    Integer countNewPopup(@Param("userId") Long userId);


    @Query("update Notification n set n.status='DISPLAYED' where n.medium='POPUP' and n.recipientUserId=:userId and n.status='NEW'")
    Integer clearPopup(@Param("userId") Long userId);


    @Query("select n from Notification n  where n.status='NEW' and n.medium='POPUP' and n.recipientUserId=:userId order by n.createDate desc")
    List<Notification> findNewPopupNotification(@Param("userId") Long userId);
}
