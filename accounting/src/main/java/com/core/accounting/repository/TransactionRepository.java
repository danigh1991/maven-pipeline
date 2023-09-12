package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.Transaction;
import com.core.accounting.model.wrapper.RequestTransactionWrapper;
import com.core.accounting.model.wrapper.TransactionWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends BaseRepository<Transaction, Long> {

    @Query("select b  from Transaction b where b.id =?1")
    Transaction findByEntityId(Long transactionId);

    List<Transaction> findByReferenceId(Long refrenceId);

    List<Transaction> getAllByAccountIdOrderByCreateDateDescIdDesc(Long accountId, Pageable pageable);


    @Query("select COALESCE(sum(t.debit),0)  from Transaction t inner join t.account a  inner join t.operationType o where o.code in :accountTypeCodes and  a.userId=:userId and  t.createDate between :fromDate and :toDate")
    Double sumDebitByAccountTypeIdsAndUserId(@Param("accountTypeCodes") List<Integer> accountTypeCodes, @Param("userId") Long userId,@Param("fromDate") Date fromDate,@Param("toDate") Date toDate);

    @Query("select COALESCE(sum(t.debit),0)  from Transaction t inner join t.account a  where a.id=:accountId and t.createDate between :fromDate and :toDate")
    Double sumDebitByAccountId(@Param("accountId") Long accountId, @Param("fromDate") Date fromDate,@Param("toDate") Date toDate);

    @Query(nativeQuery = true)
    TransactionWrapper getTransactionWrapperById(@Param("transactionId") Long transactionId);
    @Query(nativeQuery = true)
    List<TransactionWrapper> getTransactionWrappersByReferenceId(@Param("referenceId") Long referenceId);
    @Query(nativeQuery = true)
    List<TransactionWrapper> getAllTransactionWrappersByAccId(@Param("accountId") Long accountId, @Param("userCreditId") Long userCreditId, Pageable pageable);

    @Query(nativeQuery = true)
    List<TransactionWrapper> getAllTransactionWrappersByAccIdAndUserId(@Param("accountId") Long accountId, @Param("userCreditId") Long userCreditId, @Param("userId") Long userId, Pageable pageable);

    @Query(nativeQuery = true)
    List<TransactionWrapper> getTransactionWrappersByOrderId(@Param("orderId") Long orderId);

    @Query(nativeQuery = true)
    List<TransactionWrapper> getTransactionWrappersByOperationRequestId(@Param("operationRequestId") Long operationRequestId);

    @Query(nativeQuery = true)
    List<RequestTransactionWrapper> getRequestTransactionWrappersByOperationRequestId(@Param("operationRequestId") Long operationRequestId);

    @Query(nativeQuery = true)
    RequestTransactionWrapper getRequestTransactionWrappersByTransactionId(@Param("transactionId") Long transactionId);

    @Query(nativeQuery = true)
    RequestTransactionWrapper getRequestTransactionWrappersByTransactionIdAndUserId(@Param("transactionId") Long transactionId,@Param("userId") Long userId);


}
