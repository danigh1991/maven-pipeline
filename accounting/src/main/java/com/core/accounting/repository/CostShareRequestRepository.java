package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.CostShareRequest;
import com.core.accounting.model.wrapper.CostShareRequestWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostShareRequestRepository extends BaseRepository<CostShareRequest, Long> {

    @Query("select r  from CostShareRequest r where r.id =:id")
    Optional<CostShareRequest> findByEntityId(@Param("id") Long id);

    @Query("select r from CostShareRequest r where r.id =:id and r.userId=:userId")
    Optional<CostShareRequest> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query(nativeQuery = true)
    Optional<CostShareRequestWrapper> findWrapperById(@Param("costShareRequestId") Long costShareRequestId);

    @Query(nativeQuery = true)
    Optional<CostShareRequestWrapper> findWrapperByIdAndUserId(@Param("costShareRequestId") Long costShareRequestId, @Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<CostShareRequestWrapper> findWrapperByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(nativeQuery = true)
    List<CostShareRequestWrapper> findWrapperByUserIdAndTypeId(@Param("userId") Long userId,@Param("typeId") Long typeId, Pageable pageable);

}
