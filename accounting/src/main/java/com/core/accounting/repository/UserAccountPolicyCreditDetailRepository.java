package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountCreditDetail;
import com.core.accounting.model.dbmodel.UserAccountPolicyCreditDetail;
import com.core.accounting.model.wrapper.UserAccountPolicyCreditDetailWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserAccountPolicyCreditDetailRepository extends BaseRepository<UserAccountPolicyCreditDetail, Long> {

    @Query("select u from UserAccountPolicyCreditDetail u join u.accountCreditDetail c where u.id=:userAccountPolicyCreditDetailId")
    Optional<UserAccountPolicyCreditDetail> findByEntityId(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId);

    @Query("select u from UserAccountPolicyCreditDetail u join u.accountCreditDetail c join u.userAccountPolicyProfile p where u.id=:userAccountPolicyCreditDetailId and p.userId=:userId")
    Optional<UserAccountPolicyCreditDetail> findByEntityId(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId, @Param("userId") Long userId);

    @Query("select u from UserAccountPolicyCreditDetail u join u.accountCreditDetail c join u.userAccountPolicyProfile p where c.id=:accountCreditDetailId and p.userId=:userId")
    Optional<UserAccountPolicyCreditDetail> findByAccountCreditDetailIdAndUserId(@Param("accountCreditDetailId") Long accountCreditDetailId, @Param("userId") Long userId);


    @Query(value = "select count(a) from UserAccountPolicyCreditDetail a where a.accountCreditDetail.id=:accountCreditDetailId")
    Integer countByAccountCreditDetailId(@Param("accountCreditDetailId") Long accountCreditDetailId);


    @Query(nativeQuery =true)
    UserAccountPolicyCreditDetailWrapper findUserAccountPolicyCreditDetailWrappersById(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId);
    @Query(nativeQuery =true)
    UserAccountPolicyCreditDetailWrapper findUserAccountPolicyCreditDetailWrappersByIdAndUserId(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId,@Param("userId") Long userId);

    @Query(nativeQuery =true)
    List<UserAccountPolicyCreditDetailWrapper> findUserAccountPolicyCreditDetailWrappersByAccountCreditDetailId(@Param("accountCreditDetailId") Long accountCreditDetailId);
    @Query(nativeQuery =true)
    List<UserAccountPolicyCreditDetailWrapper> findUserAccountPolicyCreditDetailWrappersByAccountCreditDetailIdAndUserId(@Param("accountCreditDetailId") Long accountCreditDetailId,@Param("userId") Long userId);

    @Query("select max(u.usedAmount) from UserAccountPolicyCreditDetail u where u.accountCreditDetail.id=:accountCreditDetailId")
    Double maxUserAccountCreditUsed(@Param("accountCreditDetailId") Long accountCreditDetailId);

    @Query("select count(u) from UserAccountPolicyCreditDetail u where u.accountCreditDetail.id=:accountCreditDetailId ")
    Integer countUserAccountCredit(@Param("accountCreditDetailId") Long accountCreditDetailId);


    @Query("select (u.accountCreditDetail.creditAmountPerUser-u.usedAmount)-u.block from UserAccountPolicyCreditDetail u where u.id=:userAccountPolicyCreditDetailId")
    Double findUserAccountCreditBalance(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId);

}


