package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.BankAccount;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankAccountRepository extends BaseRepository<BankAccount, Long> {
    @Query("select b  from BankAccount b where b.id =?1")
    BankAccount findByEntityId(Long id);

    @Query("select b  from BankAccount b where b.id =:id and b.userId=:userId")
    BankAccount findByEntityIdAndUserId(@Param("id") Long id,@Param("userId") Long userId);

    @Query("select b from BankAccount b where b.userId=:userId order by b.id asc")
    List<BankAccount> findByUserId(@Param("userId") Long userId);
}


