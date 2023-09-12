package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.TransactionType;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface TransactionTypeRepository extends BaseRepository<TransactionType, Long> {

    @Query("select t from TransactionType t where t.id =?1 and t.active=true")
    Optional<TransactionType> findByEntityId(Long transactionTypeId);

    //Optional<TransactionType> findByNameAndActive(String name);


}
