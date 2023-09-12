package com.core.accounting.model.view;

import com.core.datamodel.model.view.MyJsonView;

public class AccountJsonView  extends MyJsonView {



    ///////////////AccountType //////////////////////////////////
    public interface AccountTypeList extends ResponseView{}
    public interface AccountTypeDetails extends AccountTypeList{}


    ///////////////Account //////////////////////////////////
    public interface AccountShortList extends IdVersionView,ResponseView{}
    public interface AccountList extends BankList,AccountTypeList,AccountShortList{}
    public interface AccountDetails extends AccountList,AccountTypeDetails{}


    ///////////////Transaction //////////////////////////////////
    public interface TransactionList extends OperationTypeList,OrderDetails,ResponseView{}
    public interface TransactionDetails extends TransactionList{}
    ///////////////OperationType //////////////////////////////////
    public interface OperationTypeList extends ResponseView{}
    public interface OperationTypeDetails extends OperationTypeList {}


    //#region AvailableAccountForOperationTypeView------------------------------------------------------------------------------
    public interface AvailableAccountForOperationTypeView extends AccountList{}
    //#endregion AvailableAccountForOperationTypeView


    // #region OperationRequestResult------------------------------------------------------------------------------
    public interface OperationRequestShortList extends ResponseView{}
    public interface OperationRequestList extends OperationRequestShortList,GeneralKeyValue {}
    public interface OperationRequestDetails extends OperationRequestList,TransactionList {}
    //#endregion OperationRequestResult

    //#region OperationRequestResult------------------------------------------------------------------------------
    public interface OperationRequestResultView extends ResponseView /*GeneralBankObject*/{}

    //#endregion OperationRequestResult


    //#region DepositRequestWrapper------------------------------------------------------------------------------
    public interface DepositRequestWrapperShortList extends ResponseView{}
    public interface DepositRequestWrapperList extends DepositRequestWrapperShortList {}
    public interface DepositRequestWrapperDetail extends DepositRequestWrapperList,DepositRequestDetailWrapperDetail{}
    //#endregion DepositRequestWrapper

    //#region DepositRequestDetailWrapper------------------------------------------------------------------------------
    public interface DepositRequestDetailWrapperList extends ResponseView{}
    public interface DepositRequestDetailWrapperDetail extends DepositRequestDetailWrapperList{}
    //#endregion DepositRequestWrapper

    //#region BankAccount------------------------------------------------------------------------------
    public interface BankAccountList extends BankList,ResponseView{}
    public interface BankAccountDetails extends BankAccountList{}



    //#region DepositRequestDetailWrapper------------------------------------------------------------------------------

    ///////////////////RequestRefundMoney////////////////////
    public interface RequestRefundMoneyList extends BankList,ResponseView{}
    public interface RequestRefundMoneyDetails extends RequestRefundMoneyList,TypeWrapperList,ResponseView{}
    public interface RequestRefundMoneyDetailsForAdmin extends RequestRefundMoneyDetails,AccountDetails,TypeWrapperList,ResponseView{}

    ///////////////////FinanceDestNumber////////////////////
    public interface FinanceDestNumberList extends ResponseView{}
    public interface FinanceDestNumberDetails extends FinanceDestNumberList {}


    ///////////////////Credit////////////////////
    public interface AccountCreditDetailList extends ResponseView{}
    public interface AccountCreditDetailDetails extends AccountShortList, AccountCreditDetailList {}

    ///////////////////MerchantLimit////////////////////
    public interface MerchantLimitView extends ResponseView{}

    ///////////////////UserAccountPolicyCreditDetail////////////////////
    public interface UserAccountPolicyCreditDetailView extends MerchantShortList,ResponseView{}

    ///////////////////AccountPolicyProfile////////////////////
    public interface AccountPolicyProfileShortList extends ResponseView{}
    public interface AccountPolicyProfileList extends AccountTypeList,AccountPolicyProfileShortList{}
    public interface AccountPolicyProfileDetail extends AccountTypeList, AccountPolicyProfileList,AccountPolicyProfileOperationTypeList{}


    ///////////////////AccountPolicyProfileOperationType////////////////////
    public interface AccountPolicyProfileOperationTypeList extends ResponseView{}



    //#region CostShareRequest------------------------------------------------------------------------------
    public interface CostShareRequestShortList extends ResponseView{}
    public interface CostShareRequestList extends CostShareRequestShortList {}
    public interface CostShareRequestDetail extends CostShareRequestList, CostShareRequestDetailDetail {}
    //#endregion CostShareRequest

    //#region CostShareRequestDetail------------------------------------------------------------------------------
    public interface CostShareRequestDetailList extends ResponseView{}
    public interface CostShareRequestDetailDetail extends CostShareRequestDetailList {}
    //#endregion CostShareRequestDetail


    //#region CostShareType------------------------------------------------------------------------------
    public interface CostShareTypeList extends ResponseView{}
    public interface CostShareTypeDetail extends CostShareTypeList{}
    //#endregion CostShareType

    //#region CostDetail------------------------------------------------------------------------------
    public interface CostDetailList extends ResponseView{}
    public interface CostDetailDetail extends CostDetailList {}
    //#endregion CostDetail

    //#region DepositRequestWrapper------------------------------------------------------------------------------
    public interface ManualTransactionRequestWrapperList extends ResponseView{}
    public interface ManualTransactionRequestWrapperDetail extends ManualTransactionRequestWrapperList{}
    //#endregion DepositRequestWrapper

    //#region Merchant------------------------------------------------------------------------------
    public interface MerchantShortList extends ResponseView{}
    public interface MerchantList extends MerchantShortList{}
    public interface MerchantDetail extends MerchantList {}
    //#endregion Merchant

    //#region MerchantCategory------------------------------------------------------------------------------
    public interface MerchantCategoryList extends ResponseView{}
    public interface MerchantCategoryDetail extends MerchantCategoryList {}
    //#endregion Merchant


    //#region MerchantCustomer------------------------------------------------------------------------------
    public interface MerchantCustomerList extends ResponseView{}
    public interface MerchantCustomerDetail extends MerchantCustomerList {}
    //#endregion MerchantCustomer

    //#region AccountingDashboard------------------------------------------------------------------------------
    public interface AccountingDashboardView extends ResponseView{}
    //#endregion AccountingDashboard


}
