package com.core.accounting.repository.factory;

import com.core.accounting.repository.*;
import com.core.exception.ResourceNotFoundException;
import com.core.model.enums.ERepository;
import org.springframework.data.repository.Repository;

public  class AccountingRepositoryFactory {

    private static OperationTypeRepository operationTypeRepository;
    private static TransactionRepository transactionRepository;
    private  static AccountRepository accountRepository;
    private  static DepositRequestDetailRepository depositRequestDetailRepository;
    private  static CostShareRequestDetailRepository costShareRequestDetailRepository;
    private  static MerchantRepository merchantRepository;



    public static void setOperationTypeRepository(OperationTypeRepository operationTypeRepository) {
        AccountingRepositoryFactory.operationTypeRepository = operationTypeRepository;
    }

    public static void setTransactionRepository(TransactionRepository transactionRepository) {
        AccountingRepositoryFactory.transactionRepository = transactionRepository;
    }

    public static void setAccountRepository(AccountRepository accountRepository) {
        AccountingRepositoryFactory.accountRepository = accountRepository;
    }

    public static void setDepositRequestDetailRepository(DepositRequestDetailRepository depositRequestDetailRepository) {
        AccountingRepositoryFactory.depositRequestDetailRepository = depositRequestDetailRepository;
    }

    public static void setCostShareRequestDetailRepository(CostShareRequestDetailRepository costShareRequestDetailRepository) {
        AccountingRepositoryFactory.costShareRequestDetailRepository = costShareRequestDetailRepository;
    }

    public static void setMerchantRepository(MerchantRepository merchantRepository) {
        AccountingRepositoryFactory.merchantRepository = merchantRepository;
    }

    public static Repository getRepository(ERepository eRepository){
        if (eRepository==ERepository.TRANSACTION)
            return transactionRepository;
        if (eRepository==ERepository.OPERATION_TYPE)
            return operationTypeRepository;
        else if (eRepository==ERepository.ACCOUNT)
            return accountRepository;
        else if (eRepository==ERepository.DEPOSIT_REQUEST_DETAIL)
            return depositRequestDetailRepository;
        else if (eRepository==ERepository.COST_SHARE_REQUEST_DETAIL)
            return costShareRequestDetailRepository;
        else if (eRepository==ERepository.MERCHANT)
            return merchantRepository;
        else
            throw new ResourceNotFoundException("","global.repositoryNotFound");
    }

}


