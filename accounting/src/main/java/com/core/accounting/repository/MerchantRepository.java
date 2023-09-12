package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.Merchant;
import com.core.accounting.model.wrapper.MerchantWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MerchantRepository extends BaseRepository<Merchant, Long> {


    @Query("select d  from Merchant d where d.id =:id")
    Optional<Merchant> findByEntityId(@Param("id") Long id);

    @Query("select d  from Merchant d where d.id =:id and d.userId=:userId")
    Optional<Merchant> findByEntityId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select d  from Merchant d where d.id =:id and d.active=true")
    Optional<Merchant> findActiveByEntityId(@Param("id") Long id);

    @Query("select d  from Merchant d where d.id =:id and d.active=true and d.userId=:userId")
    Optional<Merchant> findActiveByEntityId(@Param("id") Long id, @Param("userId") Long userId);


    @Query("select d  from Merchant d where d.userId=:userId")
    Optional<Merchant> findByUserId( @Param("userId") Long userId);
    @Query("select d from Merchant d where d.userId=:userId and d.active=true")
    Optional<Merchant> findActiveByUserId( @Param("userId") Long userId);

    @Query("select count(d)  from Merchant d where d.userId=:userId")
    Integer countByUserId( @Param("userId") Long userId);

    @Query("select count(d)  from Merchant d where d.active=true and d.userId=:userId")
    Integer countActiveByUserId( @Param("userId") Long userId);

    @Query("select c.transferOperationCode from Merchant m join m.merchantCategory c where m.active=true and m.userId=:userId")
    Integer findMerchantTransferOperationTypeCode( @Param("userId") Long userId);


    @Query("select d.otherMerchantViewPolicy  from Merchant d where d.active=true and d.userId=:userId")
    Integer findOtherMerchantViewPolicyByUserId( @Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<MerchantWrapper> findMerchantWrapperInfoByUserAccountPolicyCreditDetailId(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId);

    @Query(nativeQuery = true)
    List<MerchantWrapper> findMerchantWrapperInfoByUserAccountPolicyCreditDetailIdAndUserId(@Param("userAccountPolicyCreditDetailId") Long userAccountPolicyCreditDetailId,@Param("userId") Long userId);






}
