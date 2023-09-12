package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.MerchantCustomer;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MerchantCustomerRepository extends BaseRepository<MerchantCustomer, Long> {

    @Query("select d  from MerchantCustomer d where d.id =:id")
    Optional<MerchantCustomer> findByEntityId(@Param("id") Long id);

    @Query("select d  from MerchantCustomer d join d.merchant m where d.id =:id and m.userId=:userId")
    Optional<MerchantCustomer> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);


    @Query("select distinct count(d) from MerchantCustomer d join d.merchant m where m.userId=:userId")
    Integer countByUserId(@Param("userId") Long userId);

    @Query("select d  from MerchantCustomer d join d.merchant m where d.userId=:userId and m.userId=:merchantUserId")
    Optional<MerchantCustomer> findByUserIdAndMerchantUserId(@Param("userId") Long userId, @Param("merchantUserId") Long merchantUserId);

}
