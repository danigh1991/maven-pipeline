package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountPolicyProfile;
import com.core.accounting.model.dbmodel.UserAccountPolicyProfile;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAccountPolicyProfileRepository extends BaseRepository<UserAccountPolicyProfile, Long> {
    @Query("select u  from UserAccountPolicyProfile u where u.id=:userAccountPolicyProfileId")
    Optional<UserAccountPolicyProfile> findByEntityId(@Param("userAccountPolicyProfileId") Long userAccountPolicyProfileId);

    @Query("select u  from UserAccountPolicyProfile u join u.account a where u.id=:userAccountPolicyProfileId and a.userId=:userId")
    Optional<UserAccountPolicyProfile> findByEntityId(@Param("userAccountPolicyProfileId") Long userAccountPolicyProfileId, @Param("userId") Long userId);

    @Query("select u  from UserAccountPolicyProfile u  join u.account a join u.accountPolicyProfile p where a.id=:accountId and p.id=:accountPolicyProfileId and u.userId=:userId")
    Optional<UserAccountPolicyProfile> findByAccountIdAndAccountPolicyProfileIdAndUserId(@Param("accountId") Long accountId, @Param("accountPolicyProfileId") Long accountPolicyProfileId,@Param("userId") Long userId);

    @Query("select u  from UserAccountPolicyProfile u  join u.account a join u.accountPolicyProfile p where a.id=:accountId and u.userId=:userId")
    Optional<UserAccountPolicyProfile> findByAccountIdAndUserId(@Param("accountId") Long accountId, @Param("userId") Long userId);

}
