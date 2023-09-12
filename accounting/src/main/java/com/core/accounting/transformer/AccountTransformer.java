package com.core.accounting.transformer;

import com.core.accounting.model.contextmodel.AccountDto;
import com.core.accounting.model.dbmodel.Account;
import com.core.accounting.repository.AccountTypeRepository;
import com.core.common.transformer.Transformer;
import org.springframework.stereotype.Component;

@Component
public class AccountTransformer implements Transformer<AccountDto, Account> {

    private AccountTypeRepository accountTypeRepository;


    @Override
    public Account transform(AccountDto source, Account destination) {
        return null;
    }
}
