package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.OperationRequest;
import com.core.accounting.model.wrapper.OperationRequestWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OperationRequestRepository extends BaseRepository<OperationRequest, Long> {

    @Query("select  o from OperationRequest o where o.id=:id")
    Optional<OperationRequest> findByEntityId(@Param("id") Long id);

    @Query("select  o from OperationRequest o where o.id=:id and o.userId=:userId")
    Optional<OperationRequest> findByEntityIdAndUserId(@Param("id") Long id,@Param("userId") Long userId);

    @Query("select  o from OperationRequest o where o.id=:id and o.referenceNumber=:referenceNumber")
    Optional<OperationRequest> findByIdAndReferenceNumber(@Param("id") Long id,@Param("referenceNumber") String referenceNumber);

    @Query("select  o from OperationRequest o where o.id=:id and o.referenceNumber=:referenceNumber and o.userId=:userId")
    Optional<OperationRequest> findByIdAndReferenceNumberAndUserId(@Param("id") Long id,@Param("referenceNumber") String referenceNumber,@Param("userId") Long userId);

    @Query("select  o from OperationRequest o ,BankPayment bp where o.id=bp.operationRequestId and o.referenceNumber=:referenceNumber")
    Optional<OperationRequest> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("select  o from OperationRequest o ,BankPayment bp where bp.operationRequestId=o.id and o.referenceNumber=:referenceNumber and o.userId=:userId")
    Optional<OperationRequest> findByReferenceNumberAndUserId(@Param("referenceNumber") String referenceNumber,@Param("userId") Long userId);

    @Query("select o.operationType.id from OperationRequest o where o.id=:id")
    Integer findOperationTypeId(@Param("id") Long id);

    @Query(nativeQuery = true)
    Optional<OperationRequestWrapper> findOperationRequestWrapperById(@Param("operationRequestId") Long operationRequestId);

    @Query(nativeQuery = true)
    Optional<OperationRequestWrapper> findOperationRequestWrapperByIdAndUserId(@Param("operationRequestId") Long operationRequestId,@Param("userId") Long userId);

    @Query(nativeQuery = true)
    Optional<OperationRequestWrapper> findOperationRequestWrapperByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query(nativeQuery = true)
    Optional<OperationRequestWrapper> findOperationRequestWrapperByReferenceNumberAndUserId(@Param("referenceNumber") String referenceNumber,@Param("userId") Long userId);


    @Query(nativeQuery = true)
    List<OperationRequestWrapper> findOperationRequestWrapperByUserId(@Param("userId") Long userId, Pageable pageable);

}


