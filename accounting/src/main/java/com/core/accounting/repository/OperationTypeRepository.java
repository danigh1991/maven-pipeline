package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.OperationType;
import com.core.accounting.model.wrapper.OperationTypeSummeryWrapper;
import com.core.accounting.model.wrapper.OperationTypeWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OperationTypeRepository extends BaseRepository<OperationType, Long> {

    @Query("select b  from OperationType b where b.id =?1")
    Optional<OperationType> findByEntityId(Long operationTypeId);

    Optional<OperationType> findByCode(Integer code);

    @Query(nativeQuery = true)
    List<OperationTypeWrapper> findOperationTypeWrappersByAccountId(@Param("accountId") Long accountId, @Param("userId") Long userId, @Param("transactionSourceType") Integer transactionSourceType);

    @Query(nativeQuery = true)
    List<OperationTypeWrapper> findOperationTypeWrappersBySourceTypeId(@Param("operationSourceType") Integer operationSourceType);

    @Query(nativeQuery = true)
    Optional<OperationTypeWrapper> findActiveOperationTypeWrappersByName(@Param("operationTypeName") String operationTypeName);

    @Query(nativeQuery = true)
    Optional<OperationTypeWrapper> findActiveOperationTypeWrappersByCode(@Param("code") Integer code);


    @Query(nativeQuery = true)
    List<OperationTypeWrapper> findAllOperationTypeWrappers();

    @Query(nativeQuery = true)
    List<OperationTypeWrapper> findAllOperationTypeWrappersBySourceTypeIds(@Param("operationSourceTypes") List<Integer> operationSourceType);



    @Query(value = "select distinct nvl(apt.apt_notify,opt.opt_notify) \n" +
            "    from sc_operation_type opt\n" +
            "   inner join sc_account_policy_profile_operation_type apt on (opt.opt_id=apt.apt_opt_id)\n" +
            "   inner join sc_account_policy_profile apl on (apl.apl_id=apt.apt_apl_id)\n" +
            "   inner join sc_user_account_policy_profile  uap on (apl.apl_id=uap.uap_apl_id)\n" +
            "   where uap.uap_acc_id=:accountId \n" +
            "      and uap.uap_usr_id=:userId and opt.opt_code=:code \n" , nativeQuery = true)
    Boolean findOperationTypeSendNotifyStatusForAccountByCode(@Param("userId") Long accountId,@Param("userId") Long userId,@Param("code") Integer code);

    @Query("select o.notify from OperationType o where o.code=:code ")
    Boolean findOperationTypeSendNotifyStatusByCode(@Param("code") Integer code);

    @Query(nativeQuery = true)
    List<OperationTypeSummeryWrapper> findOperationTypeSummery();

}
