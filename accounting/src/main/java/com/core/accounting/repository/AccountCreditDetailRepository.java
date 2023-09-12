package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.AccountCreditDetail;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AccountCreditDetailRepository extends BaseRepository<AccountCreditDetail, Long> {

    @Query("select  c from AccountCreditDetail c where c.id=:accountCreditDetailId")
    Optional<AccountCreditDetail> findByEntityId(@Param("accountCreditDetailId") Long accountCreditDetailId);
    @Query("select  c from AccountCreditDetail c join c.account a where c.id=:accountCreditDetailId and a.userId=:userId")
    Optional<AccountCreditDetail> findByEntityId(@Param("accountCreditDetailId") Long accountCreditDetailId,@Param("userId") Long userId);


    @Query("select  c from AccountCreditDetail c where c.active=true and c.id=:accountCreditDetailId")
    Optional<AccountCreditDetail> findActiveByEntityId(@Param("accountCreditDetailId") Long accountCreditDetailId);
    @Query("select  c from AccountCreditDetail c join c.account a where c.active=true and c.id=:accountCreditDetailId and a.userId=:userId")
    Optional<AccountCreditDetail> findActiveByEntityId(@Param("accountCreditDetailId") Long accountCreditDetailId,@Param("userId") Long userId);


    @Query(value = "select count(c) from AccountCreditDetail c where c.account.id=:accountId and  FUNCTION('REPLACE', c.title,' ','')=FUNCTION('REPLACE', :title,' ','')" )
    Integer countAccountCreditDetailByTitle(@Param("title") String title,@Param("accountId") Long accountId);

    @Query(value = "select count(c) from AccountCreditDetail c where c.id<>:accountCreditDetailId and c.account.id=:accountId and  FUNCTION('REPLACE', c.title,' ','')=FUNCTION('REPLACE', :title,' ','')" )
    Integer countAccountCreditDetailByTitle(@Param("accountCreditDetailId") Long accountCreditDetailId,@Param("title") String title,@Param("accountId") Long accountId);

    @Query("select  c from AccountCreditDetail c join c.account a  where a.id=:accountId order by c.id")
    List<AccountCreditDetail> findAllByAccountId(@Param("accountId") Long accountId);
    @Query("select  c from AccountCreditDetail c join c.account a where a.id=:accountId and a.userId=:userId  order by c.id")
    List<AccountCreditDetail> findAllByAccountId(@Param("accountId") Long accountId,@Param("userId") Long userId);

}


