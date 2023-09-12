package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.ManualTransactionRequest;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManualTransactionRequestRepository extends BaseRepository<ManualTransactionRequest, Long> {

    @Query("select m  from ManualTransactionRequest m where m.id =?1")
    Optional<ManualTransactionRequest> findByEntityId(@Param("id") Long id);

    @Query("select m  from ManualTransactionRequest m join m.account a where  m.id =:id and  a.userId=:userId")
    Optional<ManualTransactionRequest> findByEntityId(@Param("id") Long id,@Param("userId") Long userId);

}
