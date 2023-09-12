package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountMerchantLimit;
import com.core.accounting.model.wrapper.MerchantLimitWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AccountMerchantLimitRepository extends BaseRepository<AccountMerchantLimit, Long> {

    @Query("select  a from AccountMerchantLimit a where a.id=:accountMerchantLimitId")
    AccountMerchantLimit findByEntityId(@Param("accountMerchantLimitId") Long accountMerchantLimitId);
    @Query("select  a from AccountMerchantLimit a where a.account.userId=:ownerId and a.id=:accountMerchantLimitId")
    AccountMerchantLimit findByEntityId(@Param("accountMerchantLimitId") Long accountMerchantLimitId,@Param("ownerId") Long ownerId);

    @Query("select  a from AccountMerchantLimit a where a.type=:type and a.account.id=:accountId and  a.targetId=:targetId")
    AccountMerchantLimit findByAccountIdAndTargetId(@Param("accountId") Long accountId,@Param("type") Integer type,@Param("targetId") Long targetId);
    @Query("select  a from AccountMerchantLimit a where a.account.userId=:ownerId and a.type=:type and a.account.id=:accountId and  a.targetId=:targetId")
    AccountMerchantLimit findByAccountIdAndTargetId(@Param("accountId") Long accountId,@Param("type") Integer type,@Param("targetId") Long targetId,@Param("ownerId") Long ownerId);



    @Query(nativeQuery = true)
    MerchantLimitWrapper findAccountMerchantLimitWrappersById(@Param("accountMerchantLimitId") Long accountMerchantLimitId);
    @Query(nativeQuery = true)
    MerchantLimitWrapper findAccountMerchantLimitWrappersByIdAndUserId(@Param("accountMerchantLimitId") Long accountMerchantLimitId,@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<MerchantLimitWrapper> findAccountMerchantLimitWrappersByAccountId(@Param("accountId") Long accountId);
    @Query(nativeQuery = true)
    List<MerchantLimitWrapper> findAccountMerchantLimitWrappersByAccountIdAndUserId(@Param("accountId") Long accountId,@Param("userId") Long userId);
}


