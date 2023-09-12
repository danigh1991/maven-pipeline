package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountType;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountTypeRepository extends BaseRepository<AccountType, Long> {

    @Query("select  a from AccountType a where a.id=:id and a.active=true")
    AccountType findByEntityId(@Param("id") Long id);

    @Query("select  a from AccountType a where a.name=:name and a.active=true")
    AccountType findByName(@Param("name") String name);

    @Query("select  a from AccountType a where a.active=true")
    List<AccountType> findAllActive();

}


