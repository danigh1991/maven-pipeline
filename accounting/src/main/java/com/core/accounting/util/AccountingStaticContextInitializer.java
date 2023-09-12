package com.core.accounting.util;

import com.core.accounting.repository.*;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("accountingStaticContextInitializer")
public class AccountingStaticContextInitializer {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private DepositRequestDetailRepository depositRequestDetailRepository;
    @Autowired
    private CostShareRequestDetailRepository costShareRequestDetailRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @PostConstruct
    public void init() {

        AccountingRepositoryFactory.setOperationTypeRepository(operationTypeRepository);
        AccountingRepositoryFactory.setTransactionRepository(transactionRepository);
        AccountingRepositoryFactory.setAccountRepository(accountRepository);
        AccountingRepositoryFactory.setDepositRequestDetailRepository(depositRequestDetailRepository);
        AccountingRepositoryFactory.setCostShareRequestDetailRepository(costShareRequestDetailRepository);
        AccountingRepositoryFactory.setMerchantRepository(merchantRepository);

    }
}
