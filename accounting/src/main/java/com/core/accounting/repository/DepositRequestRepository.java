package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.DepositRequest;
import com.core.accounting.model.wrapper.DepositRequestWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepositRequestRepository extends BaseRepository<DepositRequest, Long> {

    @Query("select dr  from DepositRequest dr where dr.id =:id")
    Optional<DepositRequest> findByEntityId(@Param("id") Long id);

    @Query("select dr  from DepositRequest dr where dr.id =:id and dr.userId=:userId")
    Optional<DepositRequest> findByEntityId(@Param("id") Long id,@Param("userId") Long userId);

    @Query(nativeQuery = true)
    Optional<DepositRequestWrapper> findWrapperById(@Param("depositRequestId") Long depositRequestId);

    @Query(nativeQuery = true)
    Optional<DepositRequestWrapper> findWrapperByIdAndUserId(@Param("depositRequestId") Long depositRequestId,@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<DepositRequestWrapper> findWrapperByUserId(@Param("userId") Long userId, Pageable pageable);


}
