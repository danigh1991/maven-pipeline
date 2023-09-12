package com.core.accounting.init;

import com.core.common.model.enums.EConfigurationResultType;
import com.core.common.model.enums.EConfigurationType;
import com.core.common.services.CommonService;
import com.core.init.StaticInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component("accountingStaticInitializer")
public class AccountingStaticInitializer implements StaticInitializer {

    private CommonService commonService;

    @Autowired
    public AccountingStaticInitializer(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostConstruct
    @Override
    public void init() {
        //Wage Account Id
        this.commonService.registerConfigModel("wageAccountId", EConfigurationType.ZERO_POSITIVE_NUMBER, EConfigurationResultType.LONG,null);


    }
}
