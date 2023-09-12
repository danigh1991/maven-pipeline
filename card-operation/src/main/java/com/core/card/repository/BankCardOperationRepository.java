package com.core.card.repository;

import com.core.card.model.dbmodel.BankCardOperation;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankCardOperationRepository extends BaseRepository<BankCardOperation, Long> {

    @Query("select e from BankCardOperation e where e.id=:bankCardOperationId")
    Optional<BankCardOperation> findByEntityId(@Param("bankCardOperationId") Long bankCardOperationId);

    @Query("select e from BankCardOperation e where e.id=:bankCardOperationId and e.userId=:userId")
    Optional<BankCardOperation> findByEntityId(@Param("bankCardOperationId") Long bankCardOperationId, @Param("userId") Long userId);

    @Query("select e from BankCardOperation e where  e.userId=:userId  order by e.id")
    List<BankCardOperation> findMyByUserId(@Param("userId") Long userId);


}
