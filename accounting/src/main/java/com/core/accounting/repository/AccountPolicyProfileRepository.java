package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.Account;
import com.core.accounting.model.dbmodel.AccountPolicyProfile;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountPolicyProfileRepository extends BaseRepository<AccountPolicyProfile, Long> {
    @Query("select a  from AccountPolicyProfile a where a.id=:accountPolicyProfileId")
    Optional<AccountPolicyProfile> findByEntityId(@Param("accountPolicyProfileId") Long accountPolicyProfileId);

    @Query("select a  from AccountPolicyProfile a where a.id=:accountPolicyProfileId and a.userId=:userId")
    Optional<AccountPolicyProfile> findByEntityId(@Param("accountPolicyProfileId") Long accountPolicyProfileId,@Param("userId") Long userId);

    @Query("select a  from AccountPolicyProfile a where a.id=:accountPolicyProfileId and ( a.userId is null or a.userId=:userId)")
    Optional<AccountPolicyProfile> findByEntityIdForUsed(@Param("accountPolicyProfileId") Long accountPolicyProfileId,@Param("userId") Long userId);


    @Query("select a  from AccountPolicyProfile a where (a.userId is null or a.userId=:userId) and ( a.accountType.id=:accountTypeId or -1=:accountTypeId)")
    List<AccountPolicyProfile> findByUserIdAccountTypeId(@Param("accountTypeId") Long accountTypeId,@Param("userId") Long userId);


    @Query(value = "select case when count(au)>0 then true else false end from AccountPolicyProfile a join a.userAccountPolicyProfile au where a.id=:accountPolicyProfileId")
    Boolean UsedAccountPolicyProfileForUser(@Param("accountPolicyProfileId") Long accountPolicyProfileId);

    @Query(value = "select case when count(ac)>0 then true else false end from AccountPolicyProfile a join a.accountCreditDetails ac where a.id=:accountPolicyProfileId")
    Boolean UsedAccountPolicyProfileForCredit(@Param("accountPolicyProfileId") Long accountPolicyProfileId);


    @Query(value = "select case when count(at)>0 then true else false end from AccountPolicyProfile a join a.accountTypes at where a.id=:accountPolicyProfileId")
    Boolean UsedAccountPolicyProfileForAccountType(@Param("accountPolicyProfileId") Long accountPolicyProfileId);

    @Query(value = "select count(a) from AccountPolicyProfile a where (a.userId is null or a.userId=:userId) and  FUNCTION('REPLACE', a.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countAccountPolicyProfileByName(@Param("name") String title,@Param("userId") Long userId);

    @Query(value = "select count(a) from AccountPolicyProfile a where  a.id<>:accountPolicyProfileId and (a.userId is null or a.userId=:userId) and  FUNCTION('REPLACE', a.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countAccountPolicyProfileByName(@Param("accountPolicyProfileId") Long accountPolicyProfileId,@Param("name") String title,@Param("userId") Long userId);

}
