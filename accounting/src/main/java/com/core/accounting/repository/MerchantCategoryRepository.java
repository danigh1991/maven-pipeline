package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.MerchantCategory;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;


public interface MerchantCategoryRepository extends BaseRepository<MerchantCategory, Long> {

    @Query("select r  from MerchantCategory r where r.id =:id")
    Optional<MerchantCategory> findByEntityId(@Param("id") Long id);

    @Query("select r  from MerchantCategory r where r.active =true  order by r.name ")
    List<MerchantCategory> findAllActiveMerchantCategories();

    @Query("select r  from MerchantCategory r order by r.name ")
    List<MerchantCategory> findAllMerchantCategories();

}
