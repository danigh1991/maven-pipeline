package com.core.common.services.impl;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.Currency;
import com.core.services.impl.AbstractModelUtilityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("modelUtilityServiceImpl")
public class ModelUtilityServiceImpl extends AbstractModelUtilityServiceImpl {
    @Autowired
    private CommonService commonService;

    @Override
    public Integer getPanelCurrencyRndNumCount() {
        return commonService.getActiveCurrencyRndNumCount(commonService.getPanelCurrency());
    }

    @Override
    public Integer getCurrencyRndNumCount(Long currencyId) {
        return commonService.getActiveCurrencyRndNumCount(currencyId);
    }

    @Override
    public Long getCalendarTypeId() {
        return commonService.getCalendarType();
    }


    @Override
    public String getPanelCurrencyShortName() {
        return commonService.getActiveCurrencyShortName(commonService.getPanelCurrency());
    }

    @Override
    public String getCurrencyShortName() {
        return commonService.getActiveCurrencyShortName(commonService.getDefaultCurrency());
    }

    @Override
    public Object getActiveCurrencyInfo(Long currencyId) {
        return commonService.getActiveCurrencyInfo(currencyId);
    }

    @Override
    public String getMySiteDomain() {
        return commonService.getMySiteDomain();
    }

    @Override
    public String getMyBrandName() {
        return commonService.getMyBrandName();
    }

}
