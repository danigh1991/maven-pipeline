package com.core.accounting.services;

import com.core.accounting.model.contextmodel.AccountCreditDetailDto;
import com.core.accounting.model.contextmodel.MerchantLimitDto;
import com.core.accounting.model.contextmodel.UserAccountPolicyCreditDetailDto;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.wrapper.MerchantLimitWrapper;
import com.core.accounting.model.wrapper.UserAccountPolicyCreditDetailWrapper;
import com.core.common.services.Service;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditService  extends Service {

    //#region AccountCredit Limit
    List<MerchantLimitWrapper> getAccountMerchantLimitWrappers(Long accountId);
    MerchantLimitWrapper getAccountMerchantLimitWrapperInfo(Long accountMerchantLimitId);
    MerchantLimitWrapper addAccountMerchantLimit(MerchantLimitDto merchantLimitDto);
    String deleteAccountMerchantLimit(Long merchantLimitId);

    List<MerchantLimitWrapper> getAccountCreditMerchantLimitWrappers(Long accountCreditDetailId);
    MerchantLimitWrapper getAccountCreditMerchantLimitWrapperInfo(Long accountCreditMerchantLimitId);
    MerchantLimitWrapper addAccountCreditMerchantLimit(MerchantLimitDto merchantLimitDto);
    String deleteAccountCreditMerchantLimit(Long merchantLimitId);
    //#endregion

    //#region AccountCreditDetail
    List<AccountCreditDetail> getAccountCreditDetails(Long accountId);
    AccountCreditDetail getAccountCreditDetailInfo(Long accountCreditDetailId);
    AccountCreditDetail getActiveAccountCreditDetailInfo(Long accountCreditDetailId);
    AccountCreditDetail createAccountCreditDetail(AccountCreditDetailDto accountCreditDetailDto);
    String editAccountCreditDetail(AccountCreditDetailDto accountCreditDetailDto);
    Boolean changeStateAccountCreditDetail(Long accountCreditDetailId);
    //#endregion

    //#region Credit assign to User
    List<UserAccountPolicyCreditDetailWrapper> getUserAccountPolicyCreditDetailWrappers(Long accountCreditDetailId);
    UserAccountPolicyCreditDetailWrapper getUserAccountPolicyCreditDetailWrapperInfo(Long userAccountPolicyCreditDetailId);
    UserAccountPolicyCreditDetailWrapper getUserAccountPolicyCreditDetailWrapperInfo(Long userAccountPolicyCreditDetailId,Boolean ownerCheck);


    UserAccountPolicyCreditDetail getUserAccountPolicyCreditDetailInfo(Long userAccountPolicyCreditDetailId);
    UserAccountPolicyCreditDetail getUserAccountPolicyCreditDetailInfo(Long userAccountPolicyCreditDetailId,Boolean ownerCheck);
    String addUserAccountPolicyCreditDetail(UserAccountPolicyCreditDetailDto userAccountPolicyCreditDetailDto);
    String deleteUserAccountPolicyCreditDetail(Long accountCreditDetailId,Long userId);

    UserAccountPolicyCreditDetail withdrawUserAccountCredit(Long userAccountPolicyCreditDetailId, Double amount, Boolean ownerCheck);

    //#endregion
}
