package com.core.topup.repository;

import com.core.model.repository.BaseRepository;
import com.core.topup.model.dbmodel.TopUpRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TopUpRequestRepository extends BaseRepository<TopUpRequest, Long> {

    @Query("select e from TopUpRequest e where e.id=:topUpRequestId")
    Optional<TopUpRequest> findByEntityId(@Param("topUpRequestId") Long topUpRequestId);

    @Query("select e from TopUpRequest e where e.id=:topUpRequestId and e.userId=:userId")
    Optional<TopUpRequest> findByEntityId(@Param("topUpRequestId") Long topUpRequestId, @Param("userId") Long userId);


    @Query("select e from TopUpRequest e where  e.userId=:userId  order by  e.id")
    List<TopUpRequest> findMyByUserId(@Param("userId") Long userId);

    @Query("select e from TopUpRequest e where e.operationRequestId=:operationRequestId")
    Optional<TopUpRequest> findByOperationRequestId(@Param("operationRequestId") Long operationRequestId);

    @Query("select e from TopUpRequest e, OperationRequest o where e.operationRequestId=o.id and o.referenceNumber=:referenceNumber")
    Optional<TopUpRequest> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("select case when count(t)>0 then true else false end from TopUpRequest t, OperationRequest o where t.operationRequestId=o.id and o.referenceNumber=:referenceNumber")
    Boolean existByOperationRequestReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("select e from TopUpRequest e where e.status=:state and e.createDate<:beforeDate")
    List<TopUpRequest> findByState(@Param("state") Integer state, @Param("beforeDate") Date beforeDate, Pageable pageable);


}
