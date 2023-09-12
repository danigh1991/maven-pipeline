package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.DepositRequest;
import com.core.accounting.model.dbmodel.DepositRequestDetail;
import com.core.accounting.model.wrapper.DepositRequestDetailWrapper;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DepositRequestDetailRepository extends BaseRepository<DepositRequestDetail, Long> {

    @Query("select dd  from DepositRequestDetail dd where dd.id =:id")
    Optional<DepositRequestDetail> findByEntityId(@Param("id") Long id);

    @Query("select dd  from DepositRequestDetail dd join dd.depositRequest dr  where dd.id =:id and dr.userId=:userId")
    Optional<DepositRequestDetail> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select dd  from DepositRequestDetail dd where dd.id =:id and dd.userId=:receiverId")
    Optional<DepositRequestDetail> findByEntityIdAndReceiverId(@Param("id") Long id, @Param("receiverId") Long receiverId);


    @Query(nativeQuery = true)
    Optional<DepositRequestDetailWrapper> findWrapperById(@Param("depositRequestDetailId") Long depositRequestDetailId);

    @Query(nativeQuery = true)
    Optional<DepositRequestDetailWrapper> findWrapperByIdAndUserId(@Param("depositRequestDetailId") Long depositRequestDetailId, @Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<DepositRequestDetailWrapper> findWrappersByDepositRequestId(@Param("depositRequestId") Long depositRequestId);

    @Query(nativeQuery = true)
    Optional<MessageBoxWrapper> findMessageBoxWrappersByDepositRequestDetailId(@Param("depositRequestDetailId") Long depositRequestDetailId);

    @Query(nativeQuery = true)
    Optional<MessageBoxWrapper> findMessageBoxWrappersByDepositRequestDetailIdAndUserId(@Param("depositRequestDetailId") Long depositRequestDetailId,@Param("userId") Long userId);



    @Modifying
    @Query("update DepositRequestDetail set  userId=:userId , targetUser=null where  userId is null and targetUser=:userName")
    Integer linkMyDepositRequest(@Param("userName") String userName ,@Param("userId") Long userId);



}
