package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.FinanceDestNumber;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FinanceDestNumberRepository extends BaseRepository<FinanceDestNumber, Long> {
    @Query("select b  from FinanceDestNumber b where b.id =?1")
    FinanceDestNumber findByEntityId(Long id);
    List<FinanceDestNumber> findAllByActiveTrue();
    @Query("select f from FinanceDestNumber f where f.id=:id")
    FinanceDestNumber findActiveById(@Param("id") Long id);
    @Query(value = "select f.* from sc_finance_dest_number f where REPLACE(f.fdn_name,' ','')=REPLACE(:nname,' ','')",nativeQuery = true)
    FinanceDestNumber findActiveByName(@Param("nname") String nname);
}


