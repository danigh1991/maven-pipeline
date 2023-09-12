package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.Account;
import com.core.accounting.model.wrapper.AccountWrapper;
import com.core.accounting.model.wrapper.AccountingDashboardDetailWrapper;
import com.core.accounting.model.wrapper.AccountingDashboardWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

public interface AccountRepository extends BaseRepository<Account, Long> {
    @Query("select a  from Account a where a.id =:accountId")
    Account findByEntityId(@Param("accountId") Long accountId);

    @Query("select a  from Account a where a.id =:accountId and a.userId=:userId")
    Account findByEntityIdAndUser(@Param("accountId") Long accountId,@Param("userId") Long userId);

    @Query("select a.name from Account a where a.id =:accountId")
    String findAccountName(@Param("accountId") Long accountId);

    Account findFirstByUserIdAndAccountTypeIdAndMainIsTrue(Long userId,Long accountTypeId);

    @Query("select a.id from Account a where a.userId =:userId and a.accountType.id=:accountTypeId and a.main=true order by a.createDate")
    List<Long> findFirstIdByUserIdAndAccountTypeIdAndMainIsTrue(Long userId, Long accountTypeId, Pageable pageable);

    Account findFirstByUserIdAndAccountTypeIdOrderByCreateDate(Long userId,Long accountTypeId);

    @Query("select a.id from Account a where a.userId =:userId and a.accountType.id=:accountTypeId order by a.createDate")
    List<Long> findFirstIdByUserIdAndAccountTypeIdOrderByCreateDate(Long userId,Long accountTypeId, Pageable pageable);


    @Query("select a from Account a where a.userId=:userId and a.accountType.id=:accountTypeId")
    Account findMainPersonalAccountByUserId(@Param("userId") Long userId,@Param("accountTypeId") Long accountTypeId);

    @Query("select a from Account a where a.userId=:userId and a.accountType.id=:accountTypeId ")
    Account findByUserIdAndAccountTypeId(@Param("userId") Long userId,@Param("accountTypeId") Long accountTypeId);

    @Query("select a from Account a where a.userId=:userId order by a.accountType.nature desc, a.id")
    List<Account> findByUserId(@Param("userId") Long userId);

    @Query("select CASE WHEN COUNT(a) > 0 THEN true ELSE false END  from Account a where a.id=:accountId and a.userId=:userId")
    Boolean  hasOwner(@Param("accountId") Long accountId,@Param("userId") Long userId);

    @Query("select CASE WHEN COUNT(a) > 0 THEN true ELSE false END  from Account a where a.id=:accountId and a.accountType.id=:accountTypeId")
    Boolean  hasAccountType(@Param("accountId") Long accountId,@Param("accountTypeId") Long accountTypeId);

    @Query(nativeQuery = true)
    AccountWrapper getFirstAccountWrapperByUsrIdAndTypeId(@Param("userId") Long userId,@Param("accountTypeId") Long accountTypeId);


    @Query(nativeQuery = true)
    AccountWrapper getAccountWrapperById(@Param("accountId") Long accountId);

    @Query(nativeQuery = true)
    AccountWrapper getAccountWrapperByIdAndUserId(@Param("accountId") Long accountId,@Param("userId") Long userId);



    @Query(nativeQuery = true)
    List<AccountWrapper> getAccountWrappersOnlyByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<AccountWrapper> getAccountWrappersOnlyByUserIdAndTypeId(@Param("userId") Long userId,@Param("accountTypeId") Long accountTypeId);


    @Query(nativeQuery = true)
    List<AccountWrapper> getAccountWrappersByUserId(@Param("userId") Long userId,@Param("forceOwner") Boolean forceOwner);

    @Query(nativeQuery = true)
    List<AccountWrapper> getAccountWrappersByUserIdAndTypeId(@Param("userId") Long userId,@Param("accountTypeId") Long accountTypeId,@Param("forceOwner") Boolean forceOwner);

    @Query(nativeQuery = true)
    List<AccountWrapper> getAvailableAccountWrappersByOperationTypeCode(@Param("userId") Long userId,@Param("targetUserId") Long targetUserId,@Param("operationTypeCode") Integer operationTypeCode,@Param("amount") Double amount);

    @Query(nativeQuery = true)
    List<AccountWrapper> getAvailableAccountWrappersByOperationTypeCodeAndIds(@Param("userId") Long userId,@Param("targetUserId") Long targetUserId,@Param("operationTypeCode") Integer operationTypeCode,@Param("amount") Double amount,@Param("accountIds") List<Long> accountIds);


    @Query(value = "select count(a) from Account a where a.status=1 and a.accountType.id=:accountTypeId and a.userId=:userId and FUNCTION('REPLACE', a.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countAccountByName(@Param("accountTypeId") Long accountTypeId,@Param("name") String name,@Param("userId") Long userId);

    @Query(value = "select count(a) from Account a where a.status=1 and a.id<>:accountId and a.userId=:userId and a.accountType.id=:accountTypeId and FUNCTION('REPLACE', a.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countAccountByName(@Param("accountId") Long accountId,@Param("accountTypeId") Long accountTypeId,@Param("name") String name,@Param("userId") Long userId);

    @Query(value = "select count(a) from Account a where a.status=1 and a.accountType.id=:accountTypeId and a.userId=:userId")
    Integer countAccountByType(@Param("accountTypeId") Long accountTypeId,@Param("userId") Long userId);


    @Query(value = "select a.accountType.id from Account a where a.id=:accountId")
    Long findAccountTypeByAccountId(@Param("accountId") Long accountId);


    @Query(value = "select a.userId from Account a where a.id=:accountId")
    Long findAccountUserIdById(@Param("accountId") Long accountId);

    @Query(nativeQuery = true)
    AccountingDashboardWrapper findDashboardWrapperSummery();

    @Query(nativeQuery = true)
    List<AccountingDashboardDetailWrapper> findDashboardDetailWrapperSummery();
}


