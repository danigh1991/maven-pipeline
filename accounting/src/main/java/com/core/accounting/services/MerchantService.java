package com.core.accounting.services;

import com.core.accounting.model.contextmodel.MerchantDto;
import com.core.accounting.model.dbmodel.AccountCreditDetail;
import com.core.accounting.model.dbmodel.Merchant;
import com.core.accounting.model.dbmodel.MerchantCategory;
import com.core.accounting.model.dbmodel.MerchantCustomer;
import com.core.accounting.model.enums.EMerchantViewPolicy;
import com.core.accounting.model.wrapper.MerchantCustomerWrapper;
import com.core.accounting.model.wrapper.MerchantWrapper;
import com.core.common.services.Service;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;

import java.util.List;
import java.util.Map;

public interface MerchantService extends Service {

    //region  MerchantCategory
    MerchantCategory getMerchantCategoryInfo(Long merchantCategoryId);
    List<MerchantCategory> getAllMerchantCategories();
    List<MerchantCategory> getAllActiveMerchantCategories();
    //endregion MerchantCategory


    //#region Merchant
    MerchantWrapper getMerchantWrapperInfo(String userName);
    ResultListPageable<MerchantWrapper> getMerchantWrappersByCategoryCode(Integer categoryCode);
    ResultListPageable<MerchantWrapper> getAllMerchants(Map<String, Object> requestParams);
    Merchant getMerchantInfo(Long merchantId);
    Merchant getActiveMerchantInfo(Long merchantId);

    Merchant getMerchantInfoByUserId(Long userId);
    Merchant getActiveMerchantInfoByUserId(Long userId);

    Merchant addEditMerchant(MerchantDto merchantDto);
    Boolean changeMerchantState(Long merchantId);

    Boolean isMerchant(Long userId);
    Boolean isActiveMerchant(Long userId);
    Integer getMerchantTransferOperationTypeCode(Long userId);



    EMerchantViewPolicy getOtherMerchantViewPolicyByUserId(Long userId);
    List<TypeWrapper> getMerchantViewPolicies();

    MerchantCustomer addMerchantCustomer(Long userId,Long merchantUserId);
    ResultListPageable<MerchantCustomerWrapper> getMerchantCustomerWrappers(Map<String, Object> requestParams);
    //#endregion Merchant

}
