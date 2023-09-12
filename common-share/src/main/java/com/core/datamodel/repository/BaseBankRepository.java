package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Bank;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BaseBankRepository extends BaseRepository<Bank, Long> {

    @Query("select b  from Bank b where b.status=1 order by b.name")
    List<Bank> findAllBanks();


    @Query("select b  from Bank b where b.status=1 and b.allowAccountIntro=true order by b.name")
    List<Bank> findAllAllowAccountIntro();

    @Query("select b  from Bank b where b.id =?1")
    Bank findByEntityId(Long id);

    @Query("select b from Bank b where b.eName=?1")
    Bank findByEName(String eName);


}
