package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.CostDetail;
import com.core.accounting.model.dbmodel.CostShareRequestDetail;
import com.core.accounting.model.wrapper.CostDetailWrapper;
import com.core.accounting.model.wrapper.CostShareRequestDetailWrapper;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostDetailRepository extends BaseRepository<CostDetail, Long> {

    @Query("select d  from CostDetail d where d.id =:id")
    Optional<CostDetail> findByEntityId(@Param("id") Long id);

    @Query("select d  from CostDetail d join d.costShareRequest cr  where d.id =:id and cr.userId=:userId")
    Optional<CostDetail> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query(nativeQuery = true)
    Optional<CostDetailWrapper> findWrapperById(@Param("costDetailId") Long costDetailId);

    @Query(nativeQuery = true)
    Optional<CostDetailWrapper> findWrapperByIdAndUserId(@Param("costDetailId") Long costDetailId, @Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<CostDetailWrapper> findWrapperByCostShareRequestId(@Param("costShareRequestId") Long costShareRequestId);

    @Query(nativeQuery = true)
    List<CostDetailWrapper> findWrapperByCostShareRequestIdAndUserId(@Param("costShareRequestId") Long costShareRequestId, @Param("userId") Long userId);




}
