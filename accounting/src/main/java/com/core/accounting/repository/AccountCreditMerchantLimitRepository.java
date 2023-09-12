package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountCreditMerchantLimit;
import com.core.accounting.model.wrapper.MerchantLimitWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AccountCreditMerchantLimitRepository extends BaseRepository<AccountCreditMerchantLimit, Long> {

    @Query("select  a from AccountCreditMerchantLimit a where a.id=:accountCreditMerchantLimitId")
    AccountCreditMerchantLimit findByEntityId(@Param("accountCreditMerchantLimitId") Long accountCreditMerchantLimitId);
    @Query("select  a from AccountCreditMerchantLimit a join a.accountCreditDetail c where c.account.userId=:ownerId and a.id=:accountCreditMerchantLimitId")
    AccountCreditMerchantLimit findByEntityId(@Param("accountCreditMerchantLimitId") Long accountCreditMerchantLimitId, @Param("ownerId") Long ownerId);

    @Query("select  a from AccountCreditMerchantLimit a where a.type=:type and a.accountCreditDetail.id=:accountCreditId and  a.targetId=:targetId")
    AccountCreditMerchantLimit findByAccountCreditIdAndTargetId(@Param("accountCreditId") Long accountCreditId, @Param("type") Integer type, @Param("targetId") Long targetId);
    @Query("select  a from AccountCreditMerchantLimit a join a.accountCreditDetail c where c.account.userId=:ownerId and a.type=:type and c.id=:accountCreditId and  a.targetId=:targetId")
    AccountCreditMerchantLimit findByAccountCreditIdAndTargetId(@Param("accountCreditId") Long accountCreditId, @Param("type") Integer type, @Param("targetId") Long targetId, @Param("ownerId") Long ownerId);


    @Query(nativeQuery = true)
    MerchantLimitWrapper findAccountCreditMerchantLimitWrappersById(@Param("accountCreditMerchantLimitId") Long accountCreditMerchantLimitId);
    @Query(nativeQuery = true)
    MerchantLimitWrapper findAccountCreditMerchantLimitWrappersByIdAndUserId(@Param("accountCreditMerchantLimitId") Long accountCreditMerchantLimitId,@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<MerchantLimitWrapper> findAccountCreditMerchantLimitWrappersByAccountCreditDetailId(@Param("accountCreditDetailId") Long accountCreditDetailId);
    @Query(nativeQuery = true)
    List<MerchantLimitWrapper> findAccountCreditMerchantLimitWrappersByAccountCreditDetailIdAndUserId(@Param("accountCreditDetailId") Long accountCreditDetailId,@Param("userId") Long userId);

}


