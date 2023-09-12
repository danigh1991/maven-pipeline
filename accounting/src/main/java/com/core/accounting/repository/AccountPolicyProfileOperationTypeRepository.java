package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountPolicyProfile;
import com.core.accounting.model.dbmodel.AccountPolicyProfileOperationType;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountPolicyProfileOperationTypeRepository extends BaseRepository<AccountPolicyProfileOperationType, Long> {
    @Query("select a  from AccountPolicyProfileOperationType a where a.id=:accountPolicyProfileOperationTypeId")
    Optional<AccountPolicyProfileOperationType> findByEntityId(@Param("accountPolicyProfileOperationTypeId") Long accountPolicyProfileOperationTypeId);


    @Query("select a  from AccountPolicyProfileOperationType a where a.accountPolicyProfile.id=:accountPolicyProfileId")
    List<AccountPolicyProfileOperationType> findByAccountPolicyProfileId(@Param("accountPolicyProfileId") Long accountPolicyProfileId);


}
