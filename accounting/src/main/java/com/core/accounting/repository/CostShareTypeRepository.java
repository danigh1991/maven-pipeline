package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.CostShareType;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CostShareTypeRepository extends BaseRepository<CostShareType, Long> {

    @Query("select r  from CostShareType r where r.id =:id")
    Optional<CostShareType> findByEntityId(@Param("id") Long id);

    @Query("select r  from CostShareType r where r.active =true  order by r.order ")
    List<CostShareType> findAllActiveCostShareTypes();

    @Query("select r  from CostShareType r order by r.order ")
    List<CostShareType> findAllCostShareTypes();



}
