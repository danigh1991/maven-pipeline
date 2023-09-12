package com.core.accounting.services;

import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.contextmodel.*;
import com.core.accounting.model.wrapper.*;
import com.core.common.services.Service;
import com.core.datamodel.model.dbmodel.*;
import com.core.accounting.model.enums.EAccountType;
import com.core.datamodel.model.enums.ERefundType;
import com.core.model.wrapper.TypeWrapper;
import com.core.model.wrapper.ResultListPageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AccountingService extends Service {

    TransactionType getTransactionTypeInfo(Long transactionTypeId);

    Map<String,Object>  getAccountsSummery();
    List<OperationTypeWrapper>  getAccountOperationTypeWrapper(Long accountId);
    List<AccountWrapper> getAvailableAccountWrappersByOperationTypeCode(Integer operationTypeCode, Long userId, Long targetUserId,Double amount);
    List<AccountWrapper> getAvailableAccountWrappersByOperationTypeCodeAndIds(Integer operationTypeCode, Long userId, Long targetUserId,Double amount, List<Long> accountIds);

    AccountWrapper  createAccount(AccountDto accountDTO);
    Account  createAccount(Long userId, AccountDto accountDTO);
    AccountWrapper  editAccount(AccountDto accountDTO);
    Boolean existAccount(Long accountId);
    Boolean  disableAccount(Long accountId);

    String getAccountName(Long accountId);

    Boolean hasOwner(Long accountId);
    Boolean hasOwner(Long accountId,Long userId);
    Boolean hasAccountType(Long accountId, Long accountTypeId);

    void checkEnoughBalanceFor(Double amount,Long userId);
    void checkAccountEnoughBalanceFor(Double amount,Long accountId,Boolean ownerCheck);
    Boolean hasEnoughBalanceFor(Long userId,Double amount);
    Boolean hasAccountEnoughBalanceFor(Long accountId,Double amount,Boolean ownerCheck);

    Double getMyBalance();
    Double getUserBalance(Long userId);
    Double getAccountBalance(Long accountId,Boolean ownerCheck);

    List<Bank> getBankListForAllowAccountIntro();

    Bank getBankInfo(Long bankId);

    List<TypeWrapper> getAllActiveAccountTypesAsTypeWrapper();
    AccountType getAccountTypeInfo(Long accountTypeId);
    AccountType getAccountTypeInfo(String accountTypeName);

    AccountWrapper getMainAccountWrapperByUsrId(Long userId);
    AccountWrapper getFirstAccountWrapperByUsrIdAndType(Long userId, EAccountType eAccountType);

    AccountWrapper getAccountWrapperInfo(Long accountId);
    Account getAccountInfoById(Long accountId);
    Account getAccountInfoById(Long accountId,Boolean ownerCheck);

    Account getMyAccountInfo();
    Account getAccountInfoByUserId(Long userId);

    //Account getAccountInfoByUserId(Long userId,Boolean ownerCheck);
    Account getMainPersonalAccountInfoByUserId(Long userId);
    Long getMainPersonalAccountIdByUserId(Long userId);
    //Account getMainPersonalAccountInfoByUserId(Long userId,Boolean ownerCheck);

    Account getAccountInfoByUserIdAndType(Long userId, EAccountType eAccountType);
    Long getAccountIdByUserIdAndType(Long userId, EAccountType eAccountType);
    Long getAccountUserIdById(Long accountId);


    /*Long getMainPersonalAccountIdByUserId(Long userId);
    Long getAccountIdByUserIdAndType(Long userId, EAccountType eAccountType);*/


    List<Account> getMyAccounts();
    List<Account> getAccounts(Long userId);

    List<AccountWrapper> getMyAccountWrappers();
    List<AccountWrapper>  getAccountWrappers(String userName);
    List<AccountWrapper>  getAccountWrappers(Long userId);
    List<AccountWrapper>  getAccountWrappersByOwnerIdAndTypeId(String userName, Long accountTypeId);
    List<AccountWrapper>  getAccountWrappersByOwnerIdAndTypeId(Long userId, Long accountTypeId);


    List<BankAccount> getMyBankAccounts();
    List<BankAccount> getBankAccounts(Long userId);
    BankAccount getBankAccountInfo(Long bankAccountId);
    //todo temprory
    BankAccount getMyBankAccountInfo();
    BankAccount getBankAccountInfoByUserId(Long userId);
    String createBankAccountInfo(CBankAccount cBankAccount);
    String editMyBankAccountInfo(EBankAccount eBankAccount);
    String editBankAccountInfo (Long userId, EBankAccount eBankAccount);

    void checkBankAccountInfo (Long userId);


    // String transferAmount()
    Account blockMyAccount(Double amount);
    Account blockUserAccount(Long userId,Double amount);
    Account unBlockUserAccount(Long userId,Double amount);

    Account blockAccount(Long accountId,Double amount);
    Account blockAccount(Account account,Double amount);
    Account unBlockAccount(Long accountId,Double amount);
    Account unBlockAccount(Account account,Double amount);
    /*Transaction transferMoney(EOperation eOperation, Long fromUser, Long toUser, Double amount, String refrenceId, String desc);
    Transaction transferMoney(EOperation eOperation, Long fromUser, Long toUser, Double amount, String refrenceId, String desc, Map<String,Object> additionalData);
    Transaction transferMoney(EOperation eOperation, Long fromUser,EAccountType fromEAccountType, Long toUser ,EAccountType toEAccountType, Double amount, String refrenceId, String desc, Map<String,Object> additionalData);
    Transaction transferMoney(EOperation eOperation, Long fromUser,EAccountType fromEAccountType, Long toUser, EAccountType toEAccountType, Double amount, String refrenceId, String desc, Map<String, Object> additionalData,Boolean negativeAllow);*/


    //Transaction chargeAccount(BankPaymentResponse bankPaymentResponse);
    //Transaction purchase(Double amount, String refrenceId,String desc);
    //Transaction transferToSeller(Double amount,Long selerUserId,String desc);

    Boolean isFinancialAdminUser(Long userId);
    Long getFinancialAdminUser();

    Transaction depositAccount(Long accountId,Double amount,Integer operationCode,TransactionType transactionType,String referenceId,String description,Transaction source,Long orderId,Long operationRequestId);
    Transaction depositAccount(Long accountId,Double amount,Integer operationCode,TransactionType transactionType,String referenceId,String description,Transaction source,Long orderId,Long operationRequestId,Long userId);
    Transaction withdrawAccount(Long accountId,Double amount,Integer operationCode,TransactionType transactionType,String referenceId,String description,Transaction source,Long orderId,Long operationRequestId,Boolean negativeAllow);
    Transaction withdrawAccount(Long accountId,Double amount,Integer operationCode,TransactionType transactionType,String referenceId,String description,Transaction source,Long orderId,Long operationRequestId,Boolean negativeAllow,Boolean ownerCheck);
    Transaction withdrawAccount(Long accountId,Double amount,Integer operationCode,TransactionType transactionType,String referenceId,String description,Transaction source,Long orderId,Long operationRequestId,Boolean negativeAllow,Boolean ownerCheck,Long userCreditId);

    ResultListPageable<Transaction> getMyAccountStatements(Integer start, Integer count);
    ResultListPageable<Transaction> getAccountStatements(Long accountId, Integer start, Integer count);


    ResultListPageable<TransactionWrapper> getMyAccountStatementWrappers(Integer start, Integer count);
    ResultListPageable<TransactionWrapper> getMyAccountStatementWrappers(EAccountType eAccountType ,Integer start, Integer count);
    ResultListPageable<TransactionWrapper> getAccountStatementWrappers(Long accountId,Long userCreditId, Integer start, Integer count);
    ResultListPageable<TransactionWrapper> getAccountStatementWrappersForMerchant(Long accountId,Long userCreditId, Integer start, Integer count);

    RequestTransactionWrapper getRequestTransactionWrapperInfo(Long transactionId);

    String requestTransferUserCredit(GeneralTransferCredit generalTransferCredit);


    Double getSumDebitByAccountTypeIdsAndUserId(List<Integer> accountTypeCodes, Long userId,  Date fromDate,  Date toDate);
    Double getSumDebitByAccountId(Long accountId, Date fromDate,  Date toDate);


    /*Transaction transferUserCredit(CTransferCredit cTransferCredit);
    Transaction transferUserCredit(String fromUserName,String toUserName,Double amount,String referenceId,String description);*/

    /* //واریز
    Transaction deposit();
    //برداشت
    Transaction withdraw();


    //انتقال*/

    //#region Finance Destination Number
      List<FinanceDestNumber> getActiveFinanceDestNumbers();
      FinanceDestNumber getActiveFinanceDestNumber(Long id);
      FinanceDestNumber getActiveFinanceDestNumber(String name);

    //#endregion

    //#region Refund Money
    RequestRefundMoney getRequestRefundMoneyInfo(Long requestRefundMoneyId);
    Long createUserRequestRefundMoney(CRequestRefundMoney cRequestRefundMoney);
    Long createRequestRefundMoney(CRequestRefundMoney cRequestRefundMoney,Long userId,ERefundType eRefundType,Long refundForId);

    Object[] getRequestRefundValidStatusesTo(Integer currentStatus);
    String cancelRequestRefundMoney(Long requestRefundMoneyId);
    String processRequestRefundMoney(CProcessRequestRefundMoney cProcessRequestRefundMoney);

    ResultListPageable<RequestRefundMoneyWrapper> getUserRequestRefundMonies(Map<String, Object> requestParams);
    ResultListPageable<RequestRefundMoneyWrapper> getAllRequestRefundMonies(Map<String, Object> requestParams);
    RequestRefundMoneyWrapper getRequestRefundMoneyWrapperInfo(Long requestRefundMoneyId);
    List<TypeWrapper> getRefundStatuses();
    //#endregion


    //#region AccountPolicyProfile
    AccountPolicyProfile getAccountPolicyProfileInfo(Long accountPolicyProfileId);
    AccountPolicyProfile getAccountPolicyProfileInfo(Long accountPolicyProfileId,Boolean onlyForUsed);
    List<AccountPolicyProfile> getAccountPolicyProfiles(Long accountTypeId,Long userId);
    AccountPolicyProfile createAccountPolicyProfile(AccountPolicyProfileDto accountPolicyProfileDto);
    String editAccountPolicyProfile(AccountPolicyProfileDto accountPolicyProfileDto);
    String deleteAccountPolicyProfile(Long accountPolicyProfileId);


    AccountPolicyProfileOperationType getAccountPolicyProfileOperationTypeInfo(Long accountPolicyProfileOperationTypeId);
    //#endregion AccountPolicyProfile

    //#region UserAccountPolicyProfile
    UserAccountPolicyProfile getUserAccountPolicyProfileInfo(Long userAccountPolicyProfileId);
    UserAccountPolicyProfile getUserAccountPolicyProfileInfo(Long accountId,Long accountPolicyProfileId,Long userId);
    UserAccountPolicyProfile getUserAccountPolicyProfileInfo(Long accountId,Long userId);
    //#endregion UserAccountPolicyProfile



    //#region ManualTransactionRequest
    ManualTransactionRequest getManualTransactionRequestInfo(Long manualTransactionRequestId);
    ManualTransactionRequestWrapper getManualTransactionRequestWrapperInfo(Long manualTransactionRequestId);
    ResultListPageable<ManualTransactionRequestWrapper> getManualTransactionRequestWrappers(Map<String,Object> requestParams);
    List<TypeWrapper> getManualTransactionRequestStatuses();

    Long createManualTransactionRequest(ManualTransactionRequestDto manualTransactionRequestDto);
    String editManualTransactionRequest(ManualTransactionRequestDto manualTransactionRequestDto);
    String removeManualTransactionRequest(Long manualTransactionRequestId);

    String approveManualTransactionRequest(ManualTransactionRequestApproveDto  manualTransactionRequestApproveDto);

    //#endregion ManualTransactionRequest


    //#region Accounting Dashboard
    AccountingDashboardWrapper getAccountingDashboardWrapper();
    //#endregion Accounting Dashboard
}
