package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.CostShareRequestDetail;
import com.core.accounting.model.dbmodel.DepositRequestDetail;
import com.core.accounting.model.wrapper.CostShareRequestDetailWrapper;
import com.core.accounting.model.wrapper.DepositRequestDetailWrapper;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostShareRequestDetailRepository extends BaseRepository<CostShareRequestDetail, Long> {

    @Query("select d  from CostShareRequestDetail d where d.id =:id")
    Optional<CostShareRequestDetail> findByEntityId(@Param("id") Long id);

    @Query("select d  from CostShareRequestDetail d join d.costShareRequest cr  where d.id =:id and cr.userId=:userId")
    Optional<CostShareRequestDetail> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select d  from CostShareRequestDetail d where d.id =:id and d.userId=:receiverId")
    Optional<CostShareRequestDetail> findByEntityIdAndReceiverId(@Param("id") Long id, @Param("receiverId") Long receiverId);


    @Query(nativeQuery = true)
    Optional<CostShareRequestDetailWrapper> findWrapperById(@Param("costShareRequestDetailId") Long costShareRequestDetailId);

    @Query(nativeQuery = true)
    Optional<CostShareRequestDetailWrapper> findWrapperByIdAndUserId(@Param("costShareRequestDetailId") Long costShareRequestDetailId, @Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<CostShareRequestDetailWrapper> findWrappersByCostShareRequestId(@Param("costShareRequestId") Long costShareRequestId);

    @Query(nativeQuery = true)
    Optional<MessageBoxWrapper> findMessageBoxWrappersByCostShareRequestDetailId(@Param("costShareRequestDetailId") Long costShareRequestDetailId);

    @Query(nativeQuery = true)
    Optional<MessageBoxWrapper> findMessageBoxWrappersByCostShareRequestDetailIdAndUserId(@Param("costShareRequestDetailId") Long costShareRequestDetailId, @Param("userId") Long userId);


}
