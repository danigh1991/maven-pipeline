package com.core.accounting.services.impl;

import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.enums.*;
import com.core.accounting.model.wrapper.*;
import com.core.accounting.repository.*;
import com.core.accounting.model.contextmodel.*;
import com.core.accounting.services.CreditService;
import com.core.accounting.services.OperationService;
import com.core.common.model.enums.EConfirmableOperation;
import com.core.common.services.CommonService;
import com.core.common.services.impl.AbstractService;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.enums.*;
import com.core.datamodel.model.staticstatus.StatusMaps;
import com.core.datamodel.services.CacheService;
import com.core.exception.FinancialException;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.datamodel.model.dbmodel.*;
import com.core.datamodel.repository.*;
import com.core.accounting.services.AccountingService;
import com.core.model.wrapper.TypeWrapper;
import com.core.services.CalendarService;
import com.core.common.services.UserService;
import com.core.model.wrapper.ResultListPageable;
import com.core.util.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("AccountingServiceImpl")
public class AccountingServiceImpl extends AbstractService implements AccountingService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BaseBankRepository bankRepository;

    @Autowired
    private RequestRefundMoneyRepository requestRefundMoneyRepository;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private FinanceDestNumberRepository financeDestNumberRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private AccountPolicyProfileRepository accountPolicyProfileRepository;

    @Autowired
    private AccountPolicyProfileOperationTypeRepository accountPolicyProfileOperationTypeRepository;

    @Autowired
    private UserAccountPolicyProfileRepository userAccountPolicyProfileRepository;

    @Autowired
    private CreditService creditService;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private ManualTransactionRequestRepository manualTransactionRequestRepository;

    @Autowired
    private DynamicQueryHelper dynamicQueryHelper;

    @Value("#{${manualTransactionRequest.search.native.private.params}}")
    private HashMap<String, List<String>> MANUAL_TRANSACTION_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;

    @Value("#{${requestRefundMoney.search.native.private.params}}")
    private HashMap<String, List<String>> REQUEST_REFUND_MONEY_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;

    @Override
    public TransactionType getTransactionTypeInfo(Long transactionTypeId) {
        if (transactionTypeId==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.transactionType.id_required");

        Optional<TransactionType> result= transactionTypeRepository.findByEntityId(transactionTypeId);
        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data"  , "common.transactionType.id_notFound");
        return result.get();
    }

    @Override
    public Map<String, Object> getAccountsSummery() {
        Map<String, Object> result=new HashMap<>();
        List<AccountWrapper> allUserAccounts=accountRepository.getAccountWrappersByUserId(BaseUtils.getCurrentUserId(),false);
        List<AccountWrapper> personalUserAccounts=allUserAccounts.stream().filter(a-> a.getAccountTypeId().equals(EAccountType.PERSONAL.getId())).sorted(Comparator.comparing(AccountWrapper::getId)).collect(Collectors.toList());
        if(personalUserAccounts==null || personalUserAccounts.size()==0) {
            //Connect To Deposit Request Before register
            operationService.linkDepositRequest(Utils.getCurrentUser().getUsername(),Utils.getCurrentUserId());
            this.autoCreateNewAccount(BaseUtils.getCurrentUserId(), EAccountType.PERSONAL);
            allUserAccounts=accountRepository.getAccountWrappersByUserId(BaseUtils.getCurrentUserId(),false);
            personalUserAccounts=allUserAccounts.stream().filter(a-> a.getAccountTypeId().equals(EAccountType.PERSONAL.getId())).sorted(Comparator.comparing(AccountWrapper::getId)).collect(Collectors.toList());
        }
        List<AccountWrapper> shareUserAccounts=allUserAccounts.stream().filter(a-> a.getAccountTypeId().equals(EAccountType.SHAREABLE.getId())).sorted(Comparator.comparing(AccountWrapper::getId)).collect(Collectors.toList());
        List<AccountWrapper> creditUserAccounts=allUserAccounts.stream().filter(a-> a.getUserCreditId() !=null && (a.getAccountTypeId().equals(EAccountType.CREDIT_PERSONAL.getId()) || a.getAccountTypeId().equals(EAccountType.CREDIT_ORGANIZATION.getId()))).sorted(Comparator.comparing(AccountWrapper::getId)).collect(Collectors.toList());
        List<List<?>> resultAccount=new ArrayList<>();
        if(personalUserAccounts.size()>0)
            resultAccount.add(personalUserAccounts);
        if(shareUserAccounts.size()>0)
            resultAccount.add(shareUserAccounts);
        if(creditUserAccounts.size()>0)
            resultAccount.add(creditUserAccounts);

        result.put("pockets",resultAccount);
        List<OperationTypeWrapper> shareOperations= operationTypeRepository.findOperationTypeWrappersBySourceTypeId(ETransactionSourceType.NONE.getId());
        result.put("operations",shareOperations);
        return result;
    }

    @Override
    public List<OperationTypeWrapper> getAccountOperationTypeWrapper(Long accountId) {
        return operationTypeRepository.findOperationTypeWrappersByAccountId(accountId,BaseUtils.getCurrentUserId(), ETransactionSourceType.WALLET.getId());
    }


    public List<AccountWrapper> getAvailableAccountWrappersByOperationTypeCode(Integer operationTypeCode, Long userId, Long targetUserId,Double amount) {
        List<AccountWrapper> allAvailableAccountWrappers=accountRepository.getAvailableAccountWrappersByOperationTypeCode(userId,targetUserId, operationTypeCode,amount);
        return allAvailableAccountWrappers;
    }

    public List<AccountWrapper> getAvailableAccountWrappersByOperationTypeCodeAndIds(Integer operationTypeCode, Long userId, Long targetUserId,Double amount, List<Long> accountIds) {
        List<AccountWrapper> allAvailableAccountWrappers=accountRepository.getAvailableAccountWrappersByOperationTypeCodeAndIds(userId,targetUserId, operationTypeCode,amount,accountIds);
        return allAvailableAccountWrappers;
    }


    @Transactional
    @Override
    public AccountWrapper createAccount(AccountDto accountDTO) {
        return  this.getAccountWrapperInfo(this.createAccount(accountDTO.getUserId(), accountDTO).getId());
    }

    @Transactional
    @Override
    public Account createAccount(Long userId, AccountDto accountDTO) {
        Account account=new Account();
        if(hasRoleType(ERoleType.ADMIN)) {
            if (userId != null && userId > 0)
                userService.getUserInfo(userId);
            else
                throw new InvalidDataException("Invalid Data", "common.user.id_required");
        }else{
            userId=BaseUtils.getCurrentUserId();
        }
        account.setUserId(userId);
        account=this.mapAccountDtoToDb(accountDTO,account);
        account=accountRepository.save(account);
        account=this.addDefaultUserAccountPolicyProfile(account,accountDTO.getAccountPolicyProfileId());
        return account;
    }

    @Transactional
    @Override
    public AccountWrapper editAccount(AccountDto accountDTO) {
        Account account=this.getAccountInfoById(accountDTO.getId());
        account=this.mapAccountDtoToDb(accountDTO,account);
        account=this.addDefaultUserAccountPolicyProfile(account,accountDTO.getAccountPolicyProfileId());
        account=accountRepository.save(account);
        return this.getAccountWrapperInfo(account.getId());
    }

    @Override
    public Boolean existAccount(Long accountId) {
        return accountRepository.existsById(accountId);
    }

    @Transactional
    @Override
    public Boolean disableAccount(Long accountId) {
        Account account=this.getAccountInfoById(accountId);
        if(account.getMain())
            throw new InvalidDataException("Invalid Data", "common.account.disable_invalidMain");
        if(account.getStatus()!=1)
            throw new InvalidDataException("Invalid Data", "common.account.disable_invalidStatus");
        if(account.getBalance()>0)
            throw new InvalidDataException("Invalid Data", "common.account.disable_invalidBalance");
        account.setStatus(2);
        accountRepository.save(account);
        return true;
    }

    @Override
    public String getAccountName(Long accountId) {
        return  accountRepository.findAccountName(accountId);
    }

    @Override
    public Boolean hasOwner(Long accountId) {
        return this.hasOwner(accountId,BaseUtils.getCurrentUserId());
    }

    @Override
    public Boolean hasOwner(Long accountId, Long userId) {
        return accountRepository.hasOwner(accountId,userId);
    }

    @Override
    public Boolean hasAccountType(Long accountId, Long accountTypeId) {
        return accountRepository.hasAccountType(accountId,accountTypeId);
    }

    private Account autoCreateNewAccount(Long userId, EAccountType eAccountType){
        AccountDto accountDto=new AccountDto();
        accountDto.setName(eAccountType.getCaption()+ " " + Utils.getMessageResource("global.main"));
        accountDto.setAccountTypeId(eAccountType.getId());
        accountDto.setCapacity(5000000d);
        return this.createAccount(userId,accountDto);
    }

    private Account addDefaultUserAccountPolicyProfile(Account account,Long accountPolicyProfileId){
        //UserAccountPolicyProfile userAccountPolicyProfile =this.getUserAccountPolicyProfileInfo(account.getId(),account.getAccountType().getDefaultAccountPolicyProfile().getId(),account.getUserId());
        UserAccountPolicyProfile userAccountPolicyProfile =this.getUserAccountPolicyProfileInfo(account.getId(),account.getUserId());
        if (userAccountPolicyProfile==null) {
            userAccountPolicyProfile = new UserAccountPolicyProfile();
            userAccountPolicyProfile.setAccount(account);
            userAccountPolicyProfile.setUserId(account.getUserId());
        }

        if(hasRoleType(ERoleType.ADMIN) && accountPolicyProfileId!=null)
            userAccountPolicyProfile.setAccountPolicyProfile(this.getAccountPolicyProfileInfo(accountPolicyProfileId));
        else if(userAccountPolicyProfile.getAccountPolicyProfile()==null)
            userAccountPolicyProfile.setAccountPolicyProfile(account.getAccountType().getDefaultAccountPolicyProfile());

        account.getUserAccountPolicyProfile().add(userAccountPolicyProfile);
        userAccountPolicyProfile=userAccountPolicyProfileRepository.save(userAccountPolicyProfile);
        return account;
    }


    private Account mapAccountDtoToDb(AccountDto accountDto, Account account){
        EAccountType eAccountType =EAccountType.valueOf(accountDto.getAccountTypeId());

        if(eAccountType==EAccountType.CREDIT_ORGANIZATION && !hasRoleType(ERoleType.ADMIN))
            throw new InvalidDataException("Invalid Data", "common.account.create_permissionDenied");

        if (account.getId()==null) {
            if (accountRepository.countAccountByName(accountDto.getAccountTypeId(),accountDto.getName(),account.getUserId()) > 0)
                throw new InvalidDataException("Invalid Data", "common.account.name_exist");

            AccountType accountType=this.getAccountTypeInfo(accountDto.getAccountTypeId());

            Integer accountCountByType=accountRepository.countAccountByType(eAccountType.getId(),account.getUserId());
            if(accountCountByType >= accountType.getMaxCount())
                throw new InvalidDataException("Invalid Data", "common.account.limit_count_error",accountType.getMaxCount(),eAccountType.getCaption());

            if(eAccountType==EAccountType.PERSONAL && accountCountByType==0){
                   account.setMain(true);
            }

            account.setStatus(1);
            account.setAccountType(accountType);
            account.setOrder(accountCountByType);
            account.setAccountCategoryId(EAccountCategory.GLOBAL.getId().longValue());
        }else{
            if (accountRepository.countAccountByName(account.getId(),accountDto.getAccountTypeId(),accountDto.getName(),account.getUserId()) > 0)
                throw new InvalidDataException("Invalid Data", "common.account.name_exist");
        }

        if(hasRoleType(ERoleType.ADMIN)&& accountDto.getAccountCategoryId()!=null)
           account.setAccountCategoryId(EAccountCategory.valueOf(accountDto.getAccountCategoryId().intValue()).getId().longValue());

        account.setName(accountDto.getName());
        account.setDescription(accountDto.getDescription());
        account.setColor(accountDto.getColor());
        account.setTheme_id(accountDto.getTheme_id());
        if (eAccountType==EAccountType.PERSONAL /*&& !account.getMain() */)
          account.setCapacity(accountDto.getCapacity());
        return account;
    }


    @Override
    public List<TypeWrapper> getAllActiveAccountTypesAsTypeWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        List<EAccountType> eAccountTypes=accountTypeRepository.findAllActive().stream().map(a -> EAccountType.valueOf(a.getId())).collect(Collectors.toList());


        for (EAccountType a:eAccountTypes ) {
            if(hasRole("normalbuser") || (!hasRole("normalbuser") && EAccountType.CASH_RECEIVED!=a))
              typeWrappers.add(EAccountType.asObjectWrapper(a));
        }
        return typeWrappers;

    }

    @Override
    public AccountType getAccountTypeInfo(Long accountTypeId) {
        AccountType accountType=accountTypeRepository.findByEntityId(accountTypeId);
        if (accountType==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.accountType.id_notFound", accountTypeId);
        return accountType;
    }

    @Override
    public AccountType getAccountTypeInfo(String accountTypeName) {
        AccountType accountType=accountTypeRepository.findByName(accountTypeName);
        if (accountType==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.accountType.name_notFound", accountTypeName);
        return accountType;
    }


    @Override
    public AccountWrapper getMainAccountWrapperByUsrId(Long userId) {
        return this.getFirstAccountWrapperByUsrIdAndType(userId,EAccountType.PERSONAL);
    }

    @Override
    public AccountWrapper getFirstAccountWrapperByUsrIdAndType(Long userId, EAccountType eAccountType) {
        AccountWrapper accountWrapper=null;
        accountWrapper=accountRepository.getFirstAccountWrapperByUsrIdAndTypeId(userId,eAccountType.getId());
        if (accountWrapper==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");
        return accountWrapper;
    }

    @Override
    public AccountWrapper getAccountWrapperInfo(Long accountId) {
        AccountWrapper accountWrapper;
        if(accountId==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_required");
       if(hasRoleType(ERoleType.ADMIN))
           accountWrapper=accountRepository.getAccountWrapperById(accountId);
       else
           accountWrapper=accountRepository.getAccountWrapperByIdAndUserId(accountId,BaseUtils.getCurrentUserId());
        if(accountWrapper==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");
        return accountWrapper;
    }


    @Override
    public Account getAccountInfoById(Long accountId) {
        return this.getAccountInfoById(accountId,true);
    }

    @Override
    public Account getAccountInfoById(Long accountId,Boolean ownerCheck) {
        if(accountId==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_required");
        Account account;
        if(hasRoleType(ERoleType.ADMIN) || !ownerCheck)
            account= accountRepository.findByEntityId(accountId);
        else
            account= accountRepository.findByEntityIdAndUser(accountId,BaseUtils.getCurrentUserId());
        if(account==null)
           throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");
        return account;
    }

    @Override
    public Account getMyAccountInfo() {
        Long userId= BaseUtils.getCurrentUser().getId();
        return this.getAccountInfoByUserId(userId);
    }

    @Override
    public Account getAccountInfoByUserId(Long userId) {
        return this.getAccountInfoByUserIdAndType(userId,EAccountType.PERSONAL);
    }

    @Override
    public Account getMainPersonalAccountInfoByUserId(Long userId) {
        return this.getAccountInfoByUserIdAndType(userId,EAccountType.PERSONAL);
    }

    @Override
    public Long getMainPersonalAccountIdByUserId(Long userId) {
        return this.getAccountIdByUserIdAndType(userId,EAccountType.PERSONAL);
    }

    @Override
    public Account getAccountInfoByUserIdAndType(Long userId, EAccountType eAccountType) {
        Account account=null;
        if(eAccountType==EAccountType.PERSONAL)
            account=accountRepository.findFirstByUserIdAndAccountTypeIdAndMainIsTrue(userId,eAccountType.getId());
        else
            account=accountRepository.findFirstByUserIdAndAccountTypeIdOrderByCreateDate(userId,eAccountType.getId());

        if (account==null && (eAccountType==EAccountType.PERSONAL || /*eAccountType==EAccountType.DEBIT ||*/ eAccountType==EAccountType.CASH_RECEIVED /*|| eAccountType==EAccountType.COMMISSION || eAccountType==EAccountType.VAT*/))
            account=this.autoCreateNewAccount(userId,eAccountType);
        if (account==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");

        return account;
    }


    @Override
    public Long getAccountIdByUserIdAndType(Long userId, EAccountType eAccountType) {
        List<Long> accountIds=null;
        if(eAccountType==EAccountType.PERSONAL)
            accountIds=accountRepository.findFirstIdByUserIdAndAccountTypeIdAndMainIsTrue(userId,eAccountType.getId(),Utils.gotoPage(0,1));
        else
            accountIds=accountRepository.findFirstIdByUserIdAndAccountTypeIdOrderByCreateDate(userId,eAccountType.getId(),Utils.gotoPage(0,1));

        if (accountIds==null || accountIds.size()==0)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");

        return accountIds.get(0);
    }

    @Override
    public Long getAccountUserIdById(Long accountId) {
       if(accountId==null)
          throw new InvalidDataException("Invalid Data"  , "common.account.id_required");
        return accountRepository.findAccountUserIdById(accountId);
    }

    /*@Override
    public Long getMainPersonalAccountIdByUserId(Long userId) {
        return this.getAccountIdByUserIdAndType(userId,EAccountType.PERSONAL);
    }

    @Override
    public Long getAccountIdByUserIdAndType(Long userId, EAccountType eAccountType) {
        Account account=null;
        if(eAccountType==EAccountType.PERSONAL)
            account=accountRepository.findFirstByUserIdAndAccountTypeIdAndMainIsTrue(userId,eAccountType.getId());
        else
            account=accountRepository.findFirstByUserIdAndAccountTypeIdOrderByCreateDate(userId,eAccountType.getId());

        if (account==null &&
                (eAccountType==EAccountType.PERSONAL || eAccountType==EAccountType.DEBIT || eAccountType==EAccountType.CASH_RECEIVED || eAccountType==EAccountType.COMMISSION || eAccountType==EAccountType.VAT))
            account=this.autoCreateNewAccount(userId,eAccountType);
        if (account==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.id_notFound");

        return account;

    }*/

    @Override
    public List<Account> getMyAccounts() {
        return this.getAccounts(BaseUtils.getCurrentUser().getId());
    }

    @Override
    public List<Account> getAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }


    @Override
    public List<AccountWrapper> getMyAccountWrappers() {
        return this.getAccountWrappers(BaseUtils.getCurrentUserId());
    }

    @Override
    public List<AccountWrapper> getAccountWrappers(String userName) {
        return this.getAccountWrappers(userService.getUserInfo(userName).getId());
    }

    @Override
    public List<AccountWrapper> getAccountWrappers(Long userId) {
        /*if(hasRoleType(ERoleType.ADMIN))
            return accountRepository.getAccountWrappersByUserId(userId,false);
        else
            return accountRepository.getAccountWrappersByUserId(Utils.getCurrentUserId(),false);*/
        if(hasRoleType(ERoleType.ADMIN))
            return accountRepository.getAccountWrappersOnlyByUserId(userId);
        else
            return accountRepository.getAccountWrappersOnlyByUserId(Utils.getCurrentUserId());
    }

    @Override
    public List<AccountWrapper> getAccountWrappersByOwnerIdAndTypeId(String userName, Long accountTypeId) {
        return this.getAccountWrappersByOwnerIdAndTypeId(userService.getUserInfo(userName).getId(),accountTypeId);
    }

    @Override
    public List<AccountWrapper> getAccountWrappersByOwnerIdAndTypeId(Long userId, Long accountTypeId) {
        /*if(hasRoleType(ERoleType.ADMIN))
            return accountRepository.getAccountWrappersByUserIdAndTypeId(userId,EAccountType.valueOf(accountTypeId).getId(),true);
        else
            return accountRepository.getAccountWrappersByUserIdAndTypeId(Utils.getCurrentUserId(),EAccountType.valueOf(accountTypeId).getId(),true);*/
        if(hasRoleType(ERoleType.ADMIN))
            return accountRepository.getAccountWrappersOnlyByUserIdAndTypeId(userId,EAccountType.valueOf(accountTypeId).getId());
        else
            return accountRepository.getAccountWrappersOnlyByUserIdAndTypeId(Utils.getCurrentUserId(),EAccountType.valueOf(accountTypeId).getId());
    }

    @Override
    public List<BankAccount> getMyBankAccounts() {
        return this.getBankAccounts(BaseUtils.getCurrentUser().getId());
    }

    @Override
    public List<BankAccount> getBankAccounts(Long userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    @Override
    public BankAccount getBankAccountInfo(Long bankAccountId) {
        BankAccount bankAccount=null;
        if (hasRoleType(ERoleType.ADMIN))
            bankAccount=bankAccountRepository.findByEntityId(bankAccountId);
        else
            bankAccount=bankAccountRepository.findByEntityIdAndUserId(bankAccountId,BaseUtils.getCurrentUser().getId());
        if (bankAccount==null)
            throw new ResourceNotFoundException("Not Found","common.id_notFound");
        return bankAccount;
    }

    //todo temprory


    @Override
    public BankAccount getMyBankAccountInfo() {
        return this.getBankAccountInfoByUserId(BaseUtils.getCurrentUser().getId());
    }

    @Override
    public BankAccount getBankAccountInfoByUserId(Long userId) {
        BankAccount result=null;
        List<BankAccount> bankAccounts=this.getBankAccounts(userId);
        if(bankAccounts!=null && bankAccounts.size()>0)
            result=bankAccounts.get(0);
        return result;
    }

    @Override
    public String createBankAccountInfo(CBankAccount cBankAccount) {
        BankAccount bankAccount=new BankAccount();
        bankAccount.setUserId(BaseUtils.getCurrentUser().getId());
        bankAccount=this.mapBankAccountToDb(bankAccount,cBankAccount);
        bankAccountRepository.save(bankAccount);
        return Utils.getMessageResource("global.operation.success_info");
    }

    private BankAccount mapBankAccountToDb(BankAccount bankAccount,CBankAccount cBankAccount){
        if(cBankAccount.getBankId()!=null && cBankAccount.getBankId()>0) {
            Bank bank=null;
            try {
                bank = this.getBankInfo(cBankAccount.getBankId());
            }catch (Exception ex){}
            if(bank!=null)
                bankAccount.setBank(bank);
        }
        bankAccount.setTitle(cBankAccount.getTitle());
        bankAccount.setAccountNumber(cBankAccount.getAccountNumber());
        bankAccount.setIbanNumber(cBankAccount.getIbanNumber());
        bankAccount.setCardNumber(cBankAccount.getCardNumber());
        bankAccount.setSwiftNumber(cBankAccount.getSwiftNumber());
        bankAccount.setPaypalAccount(cBankAccount.getPaypalAccount());
        checkBankAccountInfo(bankAccount);
        return bankAccount;
    }

    @Transactional
    @Override
    public String editMyBankAccountInfo(EBankAccount eBankAccount) {
        final User user = userService.getCurrentUser().getUser();
        if (!userService.checkIfValidOldPassword(user, eBankAccount.getPassword())) {
            throw new InvalidDataException(user.getUsername(),"common.user.password_invalid");
        }
        return editBankAccountInfo(user.getId(), eBankAccount);
    }

    @Transactional
    @Override
    public String editBankAccountInfo(Long userId, EBankAccount eBankAccount) {
        if (userService.getUserInfo(userId)==null)
            throw new ResourceNotFoundException("Not Found","common.user.id_notFound" , userId.toString());

        BankAccount bankAccount=this.getBankAccountInfoByUserId(userId);
        Boolean existsBankAccount = true;
        if(bankAccount==null) {
            existsBankAccount = false;
            bankAccount = new BankAccount();
        }

        bankAccount=this.mapBankAccountToDb(bankAccount,eBankAccount);

        if(!existsBankAccount){
            bankAccount.setUserId(BaseUtils.getCurrentUser().getId());
            bankAccount.setTitle("Default Account");
        }

        bankAccountRepository.save(bankAccount);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Override
    public void checkBankAccountInfo(Long userId) {
        checkBankAccountInfo(getBankAccountInfoByUserId(userId));
    }

    public void checkBankAccountInfo(BankAccount bankAccount) {
        Boolean oneActiveFound=false;
        String mandatoryExpression=commonService.getMandatoryFinanceDestNumExp().toLowerCase();
        FinanceDestNumber fdn=this.getActiveFinanceDestNumber("account_number");
        if (fdn!=null) {
            oneActiveFound=true;
            if (bankAccount == null || Utils.isStringSafeEmpty(bankAccount.getAccountNumber()))
                mandatoryExpression=mandatoryExpression.replaceAll("account_number","false");
            else
                mandatoryExpression=mandatoryExpression.replaceAll("account_number","true");

//            throw new InvalidDataException("فاقد اطلاعات حساب بانکی", "اطلاعات حساب بانکی شما ناقص است، برای ادامه، لطفا با مراجعه به منوی اطلاعات مالی آن را تکمیل نمایید.");
        }else
            mandatoryExpression=mandatoryExpression.replaceAll("account_number","false");

        fdn=this.getActiveFinanceDestNumber("iban_number");
        if (fdn!=null ) {
            oneActiveFound=true;
            if (bankAccount == null || Utils.isStringSafeEmpty(bankAccount.getIbanNumber()))
                mandatoryExpression=mandatoryExpression.replaceAll("iban_number","false");
            else
                mandatoryExpression=mandatoryExpression.replaceAll("iban_number","true");
        }else
            mandatoryExpression=mandatoryExpression.replaceAll("iban_number","false");

        fdn=this.getActiveFinanceDestNumber("cart_number");
        if (fdn!=null ) {
            oneActiveFound=true;
            if (bankAccount == null || Utils.isStringSafeEmpty(bankAccount.getCardNumber()))
                mandatoryExpression=mandatoryExpression.replaceAll("cart_number","false");
            else
                mandatoryExpression=mandatoryExpression.replaceAll("cart_number","true");
        }else
            mandatoryExpression=mandatoryExpression.replaceAll("cart_number","false");
        fdn=this.getActiveFinanceDestNumber("swift_number");
        if (fdn!=null ) {
            oneActiveFound=true;
            if (bankAccount == null || Utils.isStringSafeEmpty(bankAccount.getSwiftNumber()))
                mandatoryExpression=mandatoryExpression.replaceAll("swift_number","false");
            else
                mandatoryExpression=mandatoryExpression.replaceAll("swift_number","true");
        }else
            mandatoryExpression=mandatoryExpression.replaceAll("swift_number","false");
        fdn=this.getActiveFinanceDestNumber("paypal_account");
        if (fdn!=null ) {
            oneActiveFound=true;
            if (bankAccount == null || Utils.isStringSafeEmpty(bankAccount.getPaypalAccount()))
                mandatoryExpression=mandatoryExpression.replaceAll("paypal_account","false");
            else
                mandatoryExpression=mandatoryExpression.replaceAll("paypal_account","true");
        }else
            mandatoryExpression=mandatoryExpression.replaceAll("paypal_account","false");
/*        if (account.getBank()==null)
            throw new InvalidDataException("فاقد اطلاعات حساب بانکی","اطلاعات حساب بانکی شما ناقص است، برای ادامه، لطفا با مراجعه به منوی اطلاعات مالی آن را تکمیل نمایید.");

        if (Utils.isStringSafeEmpty(account.getAccountNumber()) &&
                Utils.isStringSafeEmpty(account.getIbanNumber()) &&
                   Utils.isStringSafeEmpty(account.getCardNumber()))
             throw new InvalidDataException("فاقد اطلاعات حساب بانکی","اطلاعات حساب بانکی شما ناقص است، برای ادامه، لطفا با مراجعه به منوی اطلاعات مالی آن را تکمیل نمایید.");*/
        if (!oneActiveFound)
            throw new InvalidDataException("Invalid Financial Config", "global.financialConfig_invalid");
        Boolean result=false;
        if(!Utils.isStringSafeEmpty(mandatoryExpression)) {
            try {
                ExpressionParser expressionParser=new SpelExpressionParser();
                Expression ex = expressionParser.parseExpression(mandatoryExpression);
                result=ex.getValue(Boolean.class);
            }catch (Exception e){
                e.printStackTrace();
                throw new InvalidDataException("Invalid Expression", "global.expressionParsing_error");
            }
            if (!result) {
                String msg=commonService.getMandatoryFinanceDestNumExp().toLowerCase();
                msg= msg.replaceAll("account_number",Utils.getMessageResource("global.accountNumber")).replaceAll("iban_number",Utils.getMessageResource("global.sheba")).replaceAll("cart_number",Utils.getMessageResource("global.cardNumber")).replaceAll("swift_number",Utils.getMessageResource("global.swift")).replaceAll("paypal_account",Utils.getMessageResource("eBank.paypal")).replaceAll("&&"," "+Utils.getMessageResource("global.and") + " ").replaceAll("\\|\\|" ," " + Utils.getMessageResource("global.or") + " ");
                throw new InvalidDataException("Invaldi Data", "common.account.bank.info_invalid" ,msg );
            }
        }
    }

    @Override
    public void checkEnoughBalanceFor(Double amount,Long userId) {
        //Account account=getMyAccountInfo();
        if (this.getUserBalance(userId)<Utils.round(amount,Utils.getPanelCurrencyRndNumCount()))
            throw new FinancialException("Balance Less than Amount","common.account.buy_balance_notEnough");
    }

    @Override
    public void checkAccountEnoughBalanceFor(Double amount, Long accountId,Boolean ownerCheck) {
        if (this.getAccountBalance(accountId,ownerCheck)<Utils.round(amount,Utils.getPanelCurrencyRndNumCount()))
            throw new FinancialException("Balance Less than Amount","common.account.buy_balance_notEnough");
    }

    @Override
    public Boolean hasEnoughBalanceFor(Long userId,Double amount) {
        Account account=this.getAccountInfoByUserId(userId);
        return  (Utils.round(account.getAvailableBalance(),Utils.getPanelCurrencyRndNumCount())>=Utils.round(amount,Utils.getPanelCurrencyRndNumCount()));
    }

    @Override
    public Boolean hasAccountEnoughBalanceFor(Long accountId, Double amount,Boolean ownerCheck) {
        return  this.getAccountBalance(accountId,ownerCheck)>=Utils.round(amount,Utils.getPanelCurrencyRndNumCount()) ;
    }

    @Override
    public Double getMyBalance() {
        Account account=getMyAccountInfo();
        return  (account.getAvailableBalance());
    }

    @Override
    public Double getUserBalance(Long userId) {
        Account account=getAccountInfoByUserId(userId);
        return Utils.round(account.getAvailableBalance(),Utils.getPanelCurrencyRndNumCount());
    }

    @Override
    public Double getAccountBalance(Long accountId,Boolean ownerCheck) {
        Account account=this.getAccountInfoById(accountId,ownerCheck);
        return Utils.round(account.getAvailableBalance(),Utils.getPanelCurrencyRndNumCount());
    }

    public List<Bank> getBankListForAllowAccountIntro(){
        return bankRepository.findAllAllowAccountIntro();
    }

    @Override
    public Bank getBankInfo(Long bankId) {
        if (bankId==null)
            throw new InvalidDataException("Invalid Data", "common.bank.id_required");

        Bank bank=bankRepository.findByEntityId(bankId);
        if (bank==null)
            throw new ResourceNotFoundException("Invalid Data"  , "common.bank.id_notFound", bankId);

        return bank;
    }


    @Transactional
    @Override
    public Account blockMyAccount(Double amount) {
          return this.blockUserAccount(BaseUtils.getCurrentUser().getId(),amount);
    }

    @Transactional
    @Override
    public Account blockUserAccount(Long userId, Double amount) {
        Account account=getAccountInfoByUserId(userId);
        return this.blockAccount(account,amount);
    }

    @Transactional
    @Override
    public Account unBlockUserAccount(Long userId, Double amount) {
        Account account=getAccountInfoByUserId(userId);
        return this.unBlockAccount(account,amount);
    }

    @Transactional
    @Override
    public Account blockAccount(Long accountId, Double amount) {
        Account account=this.getAccountInfoById(accountId);
        return this.blockAccount(account,amount);

    }

    @Transactional
    @Override
    public Account blockAccount(Account account, Double amount) {
        if (account.getAvailableBalance()<amount)
            throw new FinancialException("Balance Less than Amount","common.account.req_balance_notEnough");
        account.setBlock(account.getBlock()+amount);
        account=accountRepository.save(account);
        return account;
    }

    @Transactional
    @Override
    public Account unBlockAccount(Long accountId, Double amount) {
        Account account=this.getAccountInfoById(accountId);
        return this.unBlockAccount(account,amount);
    }

    @Transactional
    @Override
    public Account unBlockAccount(Account account, Double amount) {
        if (account.getBlock()<amount)
            throw new FinancialException("Balance Less than Amount","common.account.unBlock_notEnough");
        account.setBlock(account.getBlock()-amount);
        account=accountRepository.save(account);
        return account;
    }

    /*    @Override
    @Transactional
    public Transaction transferMoney(EOperation eOperation, Long fromUser, Long toUser, Double amount, String referenceId, String desc) {
        return this.transferMoney(eOperation, fromUser, toUser, amount, referenceId, desc,null);
    }

    @Override
    @Transactional
    public Transaction transferMoney(EOperation eOperation, Long fromUser, Long toUser, Double amount, String referenceId, String desc, Map<String,Object> additionalData) {
        return this.transferMoney(eOperation, fromUser, EAccountType.PERSONAL, toUser, EAccountType.PERSONAL,amount, referenceId, desc, additionalData);
    }

    @Override
    public Transaction transferMoney(EOperation eOperation, Long fromUser, EAccountType fromEAccountType, Long toUser, EAccountType toEAccountType, Double amount, String referenceId, String desc, Map<String, Object> additionalData) {
        return this.transferMoney(eOperation, fromUser, fromEAccountType, toUser, toEAccountType, amount, referenceId, desc, additionalData, false) ;
    }

    @Override
    @Transactional
    public Transaction transferMoney(EOperation eOperation, Long fromUser, EAccountType fromEAccountType, Long toUser, EAccountType toEAccountType, Double amount, String referenceId, String desc, Map<String, Object> additionalData, Boolean negativeAllow) {
        Boolean depositSave = false;
        Boolean withdrawSave = false;
        Long createBy=(fromUser != null && fromUser > 0 &&  fromUser!=this.getFinancialAdminUser())?fromUser:(toUser != null && toUser > 0? toUser:this.getFinancialAdminUser());

        Transaction withdrawTransaction = null;
        if (eOperation != EOperation.CHARGE) {
            withdrawTransaction = withdrawAccount((fromUser != null && fromUser > 0) ? fromUser: this.getFinancialAdminUser(),fromEAccountType, amount, eOperation.getOperationCode(), referenceId, *//*eAccPattern.getCaption() + "-" +*//* desc, null,createBy,negativeAllow);
        }

        Transaction depositTransaction = null;
        if (eOperation != EOperation.GET_MONEY) {
            depositTransaction = depositAccount((toUser != null && toUser > 0) ? toUser: this.getFinancialAdminUser(),toEAccountType, amount, eOperation.getOperationCode(), referenceId, *//*eAccPattern.getCaption() + "-" +*//* desc, withdrawTransaction,createBy);
        }
        if (withdrawTransaction != null && depositTransaction != null) {
            withdrawTransaction.setDestination(depositTransaction);
            withdrawSave = true;
        }

        if (additionalData != null) {
            if ((eOperation == EOperation.PURCHASE || eOperation == EOperation.SELLER_PAYMENT ||
                    eOperation == EOperation.REJECT_EDIT_FACTOR || eOperation == EOperation.PURCHASE_CANCEL || eOperation == EOperation.PURCHASE_REJECT || eOperation == EOperation.PURCHASE_RETURNED )  && additionalData.get("order") != null) {
                withdrawTransaction.setOrderId(((Long) additionalData.get("orderId")));
                withdrawSave = true;
                depositTransaction.setOrderId(((Long) additionalData.get("orderId")));
                depositSave = true;
            }
        }

        if (withdrawSave)
            withdrawTransaction = transactionRepository.save(withdrawTransaction);
        if (depositSave)
            depositTransaction = transactionRepository.save(depositTransaction);

        // System.out.println((new Date()).toString() + "  Success to transferMoney from " + fromUser + " to " + toUser);

        switch (eOperation.getId()) {
            case 1: //EOperation.CHARGE
                this.checkFinancialUser(toUser);
                return depositTransaction;
            case 2: //EOperation.GET_MONEY
                return withdrawTransaction;
            case 3: //EOperation.PURCHASE
                this.checkFinancialUser(fromUser);
                return withdrawTransaction;
            case 4: //EOperation.TRANSFER
                this.checkFinancialUser(fromUser);
                return withdrawTransaction;
            case 5: //EOperation.PURCHASE_CANCEL
                this.checkFinancialUser(fromUser);
                return depositTransaction;
            case 6: //EOperation.PURCHASE_REJECT
                return depositTransaction;
            case 7: //EOperation.PURCHASE_NOT_DELIVERED
                return depositTransaction;
            case 8: //EOperation.SELLER_PAYMENT
                return depositTransaction;
            case 9: //EOperation.SELLER_RECEIVE
                return withdrawTransaction;
            case 10: //EOperation.PURCHASE_RETURNED
                return depositTransaction;
            case 11: //EOperation.AFFILIATE_PERCENT
                return depositTransaction;
            case 12: //EOperation.REJECT_EDIT_FACTOR
                this.checkFinancialUser(toUser);
                return depositTransaction;
            default:
                return null;
        }
    }*/

    private void checkFinancialUser(){
        this.checkFinancialUser(BaseUtils.getCurrentUser().getId());
    }
    private void checkFinancialUser(Long userId ){
        if (userId==null) {
            this.checkFinancialUser();
        }else {
            if (isFinancialAdminUser(userId))
                throw new InvalidDataException("FinancialAdminUser Used Error", "common.payment.financialAdmin_notAllow1");
        }
    }

   /* @Override
    public Transaction chargeAccount(BankPaymentResponse bankPaymentResponse) {
        if (isFinancialAdminUser(BaseUtils.getCurrentUser().getId()))
            throw  new InvalidDataException("FinancialAdminUser Used Error","کاربر ادمین مالی اجازه شارژ حساب خود را ندارد.");

        return depositAccount(BaseUtils.getCurrentUser().getId(),bankPaymentResponse.getAmount(),
                      1030,bankPaymentResponse.getOrderReferenceNumber(),
                      "شارژ حساب از محل پرداخت بانکی",null);
    }

     @Override
    @Transactional
    public Transaction purchase(Double amount, String referenceId,String desc) {
        if (isFinancialAdminUser(BaseUtils.getCurrentUser().getId()))
            throw  new InvalidDataException("FinancialAdminUser Used Error","کاربر ادمین مالی اجازه خرید از حساب خود را ندارد.");

        Transaction withdrawTransaction=withdrawAccount(BaseUtils.getCurrentUser().getId(),amount,1040l,
                                           referenceId,desc,null);
        Transaction depositTransaction= depositAccount(getFinancialAdminUser(),amount,
                1010l,referenceId,
                  "خرید مشتری - " + desc,withdrawTransaction);

        withdrawTransaction.setDestination(depositTransaction);
        withdrawTransaction=transactionRepository.save(withdrawTransaction);
        return withdrawTransaction;
    }

    @Override
    @Transactional
    public Transaction transferToSeller(Double amount, Long selerUserId,String desc) {

        Transaction withdrawTransaction=withdrawAccount(getFinancialAdminUser(),amount,1020,
                "","برداشت بابت " +desc,null);
        Transaction depositTransaction= depositAccount(selerUserId,amount,1032,"",
                "واریز بابت - " + desc,withdrawTransaction);

        depositTransaction.setDestination(withdrawTransaction);
        depositTransaction=transactionRepository.save(depositTransaction);
        return depositTransaction;
    }*/

    @Override
    public Boolean isFinancialAdminUser(Long userId) {
        return (userId.equals(getFinancialAdminUser())) ? true : false;
    }

    public Long getFinancialAdminUser(){
        return commonService.getFinancialUserId();
     }

/*    private Transaction depositTjoorFinancialAdminAccount(Transaction sourceTransaction ){
        Account destinationAccount=getAccountInfo(32l);
        return depositAccount(sourceTransaction,  destinationAccount);
    }
    private Transaction depositCurrentUserAccount(Transaction sourceTransaction ){
        Account destinationAccount=getAccountInfo(BaseUtils.getCurrentUser().getId());
        return depositAccount(sourceTransaction,  destinationAccount);
    }
    private Transaction depositAccount(Transaction sourceTransaction, Account destinationAccount ){
        destinationAccount.setBalance(destinationAccount.getBalance()+sourceTransaction.getDebit());
        destinationAccount.setModifDate(new Date(System.currentTimeMillis()));
        accountRepository.save(destinationAccount);

        Transaction destinationTransaction=new Transaction();
        destinationTransaction.setTransactionType(sourceTransaction.getTransactionType());
        destinationTransaction.setAccount(destinationAccount);
        destinationTransaction.setCredit(sourceTransaction.getDebit());
        destinationTransaction.setDebit(0d);
        destinationTransaction.setReferenceId(sourceTransaction.getRefrenceId());
        destinationTransaction.setSource(sourceTransaction);
        destinationTransaction.setDescription(sourceTransaction.getDescription());
        destinationTransaction.setStatus(1);
        destinationTransaction.setCreateBy(BaseUtils.getCurrentUser().getId());
        destinationTransaction.setCreateDate(new Date(System.currentTimeMillis()));
        destinationTransaction= transactionRepository.save(destinationTransaction);
        return destinationTransaction;
    }*/


    private Transaction depositAccount(Long userId,EAccountType eAccountType,Double amount,Integer operationCode,String referenceId,String description,Transaction source,Long createBy){
        Optional<OperationType> operationType= operationTypeRepository.findByCode(operationCode);
        if (!operationType.isPresent())
            throw new ResourceNotFoundException(" Purchase Transaction Type code :" +operationCode + " Not Found" , "operation.type.deposit_invalid" , operationCode );
//        if (operationType.getOperationType()!='D')
//           throw new InvalidDataException("Invalid Transaction Type for Deposit","نوع تراکنش انتخاب شده با کد :" + operationCode +" با عملیات واریز مطابقت ندارد.");
        //Update Account
        Account depositAccount=getAccountInfoByUserIdAndType(userId,eAccountType);
        depositAccount.setBalance(depositAccount.getBalance()+amount);
        //depositAccount.setBalance(depositAccount.getAccountType().getNature()==2?depositAccount.getBalance()+amount:depositAccount.getBalance()-amount);
        //depositAccount.setModifDate(new Date(System.currentTimeMillis()));
        accountRepository.save(depositAccount);

        //Update Transaction
        Transaction depositTransaction=new Transaction();
        depositTransaction.setOperationType(operationType.get());
        depositTransaction.setAccount(depositAccount);
        depositTransaction.setCredit(amount);
        depositTransaction.setDebit(0d);
        depositTransaction.setReferenceId(referenceId);
        if (source!=null)
            depositTransaction.setSource(source);
        depositTransaction.setDescription(description);
        depositTransaction.setStatus(1);
        //depositTransaction.setCreateBy(createBy);
        //depositTransaction.setCreateDate(new Date(System.currentTimeMillis()));
        depositTransaction= transactionRepository.save(depositTransaction);
        return depositTransaction;
    }

    @Override
    public Transaction depositAccount(Long accountId, Double amount, Integer operationCode, TransactionType transactionType, String referenceId, String description, Transaction source, Long orderId, Long operationRequestId) {
        return this.depositAccount( accountId, amount, operationCode, transactionType, referenceId, description, source, orderId, operationRequestId,null) ;
    }

    public Transaction depositAccount(Long accountId, Double amount, Integer operationCode, TransactionType transactionType , String referenceId, String description, Transaction source, Long orderId, Long operationRequestId, Long userId){
        Optional<OperationType> operationType= operationTypeRepository.findByCode(operationCode);
        if (!operationType.isPresent())
            throw new ResourceNotFoundException(" Purchase Transaction Type code :" +operationCode + " Not Found" , "operation.type.deposit_invalid" , operationCode );

        Account depositAccount=this.getAccountInfoById(accountId,false);
        depositAccount.setBalance(depositAccount.getBalance()+amount);
        accountRepository.save(depositAccount);

        //Update Transaction
        Transaction depositTransaction=new Transaction();
        depositTransaction.setOperationType(operationType.get());
        if(transactionType!=null)
            depositTransaction.setTransactionType(transactionType);

        depositTransaction.setAccount(depositAccount);
        depositTransaction.setCredit(amount);
        depositTransaction.setDebit(0d);
        depositTransaction.setReferenceId(referenceId);
        if (source!=null)
            depositTransaction.setSource(source);
        depositTransaction.setDescription(description);
        depositTransaction.setStatus(1);
        if (orderId!=null && orderId>0)
           depositTransaction.setOrderId(orderId);
        if (operationRequestId!=null && operationRequestId>0)
           depositTransaction.setOperationRequestId(operationRequestId);

        if(userId!=null && userId>0) {
            depositTransaction.setCreateBy(userId);
            depositTransaction.setModifyBy(userId);
        }
        depositTransaction= transactionRepository.save(depositTransaction);
        return depositTransaction;
    }




    private Transaction withdrawAccount(Long userId,EAccountType eAccountType,Double amount,Integer operationCode,String referenceId,String description,Transaction source,Long createBy,Boolean negativeAllow){
        Optional<OperationType> operationType= operationTypeRepository.findByCode(operationCode);
        if (!operationType.isPresent())
            throw new ResourceNotFoundException(" Purchase Transaction Type code :" +operationCode + " Not Found" , " operation.type.withdraw_invalid" ,operationCode);
       // if (operationType.getOperationType()!='W')
       //     throw new InvalidDataException("Invalid Operation Type for Deposit","نوع تراکنش انتخاب شده با کد :" + trnsactionCode +" با عملیات برداشت مطابقت ندارد.");

        //Update Account
        Account withdrawAccount=getAccountInfoByUserIdAndType(userId,eAccountType);
        //if (withdrawAccount.getAccountType().getNature()==2 && withdrawAccount.getAvailableBalance()<amount)
        if (!negativeAllow && withdrawAccount.getAvailableBalance()<amount)
            throw new FinancialException("Balance Less than Amount","common.account.buy_balance_notEnough");

        withdrawAccount.setBalance(withdrawAccount.getBalance()-amount);
        //withdrawAccount.setBalance(withdrawAccount.getAccountType().getNature()==2?withdrawAccount.getBalance()-amount:withdrawAccount.getBalance()+amount);
        //withdrawAccount.setModifDate(new Date(System.currentTimeMillis()));
        accountRepository.save(withdrawAccount);

        //Update Transaction
        Transaction withdrawTransaction=new Transaction();
        withdrawTransaction.setOperationType(operationType.get());
        withdrawTransaction.setAccount(withdrawAccount);
        withdrawTransaction.setDebit(amount);
        withdrawTransaction.setCredit(0d);
        withdrawTransaction.setReferenceId(referenceId);
        withdrawTransaction.setDescription(description);
        withdrawTransaction.setStatus(1);
        //withdrawTransaction.setCreateBy(createBy);
        //withdrawTransaction.setCreateDate(new Date(System.currentTimeMillis()));
        //withdrawTransaction.setModifDate(new Date(System.currentTimeMillis()));
        withdrawTransaction=transactionRepository.save(withdrawTransaction);
        return withdrawTransaction;
    }

    public Transaction withdrawAccount(Long accountId,Double amount,Integer operationCode,TransactionType transactionType,String referenceId,String description,Transaction source,Long orderId,Long operationRequestId,Boolean negativeAllow){
        return this.withdrawAccount(accountId, amount, operationCode,transactionType , referenceId, description, source, orderId, operationRequestId, negativeAllow,true);
    }

    @Override
    public Transaction withdrawAccount(Long accountId, Double amount, Integer operationCode, TransactionType transactionType, String referenceId, String description, Transaction source, Long orderId, Long operationRequestId, Boolean negativeAllow, Boolean ownerCheck) {
        return this.withdrawAccount(accountId, amount, operationCode,transactionType , referenceId, description, source, orderId, operationRequestId, negativeAllow,ownerCheck,null);
    }

    public Transaction withdrawAccount(Long accountId, Double amount, Integer operationCode, TransactionType transactionType, String referenceId, String description, Transaction source, Long orderId, Long operationRequestId, Boolean negativeAllow, Boolean ownerCheck, Long userCreditId){
        Optional<OperationType> operationType= operationTypeRepository.findByCode(operationCode);
        if (!operationType.isPresent())
            throw new ResourceNotFoundException(" Purchase Transaction Type code :" +operationCode + " Not Found" , "operation.type.withdraw_invalid" ,operationCode);

        //Update Account
        Account withdrawAccount=this.getAccountInfoById(accountId,ownerCheck);
        if (!negativeAllow && withdrawAccount.getAvailableBalance()<amount)
            throw new FinancialException("Balance Less than Amount","common.account.buy_balance_notEnough");

        EAccountType eAccountType=EAccountType.valueOf(withdrawAccount.getAccountType().getId());
        if(userCreditId==null && (eAccountType==EAccountType.CREDIT_PERSONAL || eAccountType==EAccountType.CREDIT_ORGANIZATION))
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.id_required");
        UserAccountPolicyCreditDetail userCredit=null;
        if(userCreditId!=null)
            userCredit=creditService.withdrawUserAccountCredit(userCreditId,amount,ownerCheck);

        withdrawAccount.setBalance(withdrawAccount.getBalance()-amount);
        accountRepository.save(withdrawAccount);


        //Update Transaction
        Transaction withdrawTransaction=new Transaction();
        withdrawTransaction.setOperationType(operationType.get());
        if(transactionType!=null)
            withdrawTransaction.setTransactionType(transactionType);

        withdrawTransaction.setAccount(withdrawAccount);
        if(userCredit!=null)
            withdrawTransaction.setUserAccountPolicyCreditDetail(userCredit);
        withdrawTransaction.setDebit(amount);
        withdrawTransaction.setCredit(0d);
        withdrawTransaction.setReferenceId(referenceId);
        withdrawTransaction.setDescription(description);
        withdrawTransaction.setStatus(1);
        if (orderId!=null && orderId>0)
            withdrawTransaction.setOrderId(orderId);
        if (operationRequestId!=null && operationRequestId>0)
            withdrawTransaction.setOperationRequestId(operationRequestId);
        withdrawTransaction=transactionRepository.save(withdrawTransaction);
        return withdrawTransaction;
    }


    @Override
    public ResultListPageable<Transaction> getMyAccountStatements(Integer start, Integer count) {
        return getAccountStatements(getMyAccountInfo().getId(),start,count);
    }

    @Override
    public ResultListPageable<Transaction> getAccountStatements(Long accountId, Integer start, Integer count) {
        return generatePageableResult(transactionRepository.getAllByAccountIdOrderByCreateDateDescIdDesc(accountId,gotoPage(start,count+1)),count);
    }


    @Override
    public ResultListPageable<TransactionWrapper> getMyAccountStatementWrappers(Integer start, Integer count) {
        return this.getMyAccountStatementWrappers(EAccountType.PERSONAL , start, count);
    }

    @Override
    public ResultListPageable<TransactionWrapper> getMyAccountStatementWrappers(EAccountType eAccountType ,Integer start, Integer count) {
        return this.getAccountStatementWrappers(this.getAccountInfoByUserIdAndType(BaseUtils.getCurrentUserId(), eAccountType).getId(),null,start,count);
    }


    @Override
    public ResultListPageable<TransactionWrapper> getAccountStatementWrappers(Long accountId, Long userCreditId, Integer start, Integer count) {
        EAccountType eAccountType=EAccountType.valueOf(accountRepository.findAccountTypeByAccountId(accountId));
        if (eAccountType==EAccountType.CREDIT_PERSONAL || eAccountType==EAccountType.CREDIT_ORGANIZATION) {
            if(userCreditId==null)
                throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.id_required");
            if(userCreditId==-1)
               userCreditId = 0l;
        }else{
            userCreditId = -1l;
        }

        if (hasRoleType(ERoleType.ADMIN))
            return this.generatePageableResult(transactionRepository.getAllTransactionWrappersByAccId(accountId,userCreditId,gotoPage(start,count+1)),count);
        else
            return this.generatePageableResult(transactionRepository.getAllTransactionWrappersByAccIdAndUserId(accountId,userCreditId, BaseUtils.getCurrentUserId(),gotoPage(start,count+1)),count);

    }

    @Override
    public ResultListPageable<TransactionWrapper> getAccountStatementWrappersForMerchant(Long accountId, Long userCreditId, Integer start, Integer count) {
        EAccountType eAccountType=EAccountType.valueOf(accountRepository.findAccountTypeByAccountId(accountId));
        if (eAccountType==EAccountType.CREDIT_PERSONAL || eAccountType==EAccountType.CREDIT_ORGANIZATION) {
            if(userCreditId==null)
                throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.id_required");
            if(userCreditId==0)
                userCreditId = -1l;
        }else{
            userCreditId = -1l;
        }

        if (hasRoleType(ERoleType.ADMIN))
            return this.generatePageableResult(transactionRepository.getAllTransactionWrappersByAccId(accountId,userCreditId,gotoPage(start,count+1)),count);
        else
            return this.generatePageableResult(transactionRepository.getAllTransactionWrappersByAccIdAndUserId(accountId,userCreditId, BaseUtils.getCurrentUserId(),gotoPage(start,count+1)),count);
    }

    @Override
    public RequestTransactionWrapper getRequestTransactionWrapperInfo(Long transactionId) {
        if(transactionId==null)
            throw new InvalidDataException("Invalid Data", "common.transaction.id_required");

        if (hasRoleType(ERoleType.ADMIN))
            return transactionRepository.getRequestTransactionWrappersByTransactionId(transactionId);
        else
            return transactionRepository.getRequestTransactionWrappersByTransactionIdAndUserId(transactionId,BaseUtils.getCurrentUserId());
    }

    @Override
    public String requestTransferUserCredit(GeneralTransferCredit generalTransferCredit) {
        Map<String,Object> additionalData=new HashMap<>();
        additionalData.put("amount",generalTransferCredit.getAmount());
        additionalData.put("toUserName",generalTransferCredit.getToUserName());
        return commonService.generateAndSendConfirmCode(BaseUtils.getCurrentUser().getId(), EConfirmableOperation.TRANSFER_MONEY,generalTransferCredit.getToUserName(),additionalData);
    }

    @Override
    public Double getSumDebitByAccountTypeIdsAndUserId(List<Integer> accountTypeCodes, Long userId, Date fromDate, Date toDate) {
        return transactionRepository.sumDebitByAccountTypeIdsAndUserId(accountTypeCodes,userId,fromDate,toDate);
    }

    @Override
    public Double getSumDebitByAccountId(Long accountId, Date fromDate, Date toDate) {
        return transactionRepository.sumDebitByAccountId(accountId,fromDate,toDate);
    }

/*@Override
    public Transaction transferUserCredit(CTransferCredit cTransferCredit) {
        commonService.validateConfirmCodeData(BaseUtils.getCurrentUser().getId(),EConfirmableOperation.TRANSFER_MONEY,cTransferCredit.getToUserName(),cTransferCredit.getUserCode());
        return this.transferUserCredit(BaseUtils.getCurrentUser().getUsername(),cTransferCredit.getToUserName(),cTransferCredit.getAmount(),"",cTransferCredit.getDescription());
    }

    @Override
    public Transaction transferUserCredit(String fromUserName, String toUserName, Double amount,String referenceId, String description) {
        User fromUser=userService.getUserInfo(fromUserName);
        User toUser=userService.getUserInfo(toUserName);
        return this.transferMoney(EOperation.TRANSFER,fromUser.getId(),toUser.getId(),amount,referenceId,description);
    }*/

    //#region Finance Destination Number
    @Override
    public List<FinanceDestNumber> getActiveFinanceDestNumbers() {
        List<FinanceDestNumber> result = (List<FinanceDestNumber>) cacheService.getCacheValue("financeDestNumber", "financeDestNumbers");
        if (result != null)
            return result;
        result= financeDestNumberRepository.findAllByActiveTrue();
        if (result != null)
            cacheService.putCacheValue("financeDestNumber", "financeDestNumbers", result,15,  TimeUnit.DAYS);
        return result;
    }

    @Override
    public FinanceDestNumber getActiveFinanceDestNumber(Long id) {
        if (id==null)
            return null;
        FinanceDestNumber result = (FinanceDestNumber) cacheService.getCacheValue("financeDestNumber", id);
        if (result != null)
            return result;
        result= financeDestNumberRepository.findActiveById(id);
        if (result != null)
           cacheService.putCacheValue("financeDestNumber", id, result,15,  TimeUnit.DAYS);
        return result;
    }

    @Override
    public FinanceDestNumber getActiveFinanceDestNumber(String name) {
        if (Utils.isStringSafeEmpty(name))
            return null;
        FinanceDestNumber result = (FinanceDestNumber) cacheService.getCacheValue("financeDestNumber", name);
        if (result != null)
            return result;
        result= financeDestNumberRepository.findActiveByName(name);
        if (result != null)
            cacheService.putCacheValue("financeDestNumber", name, result,15,  TimeUnit.DAYS);
        return result;
    }
    //#endregion


    //#region Refund Money

    @Override
    public RequestRefundMoney getRequestRefundMoneyInfo(Long requestRefundMoneyId) {
        if (requestRefundMoneyId == null)
            throw new InvalidDataException("Invalid Data", "common.requestRefundMoney.id_required");

        RequestRefundMoney requestRefundMoney;
        if (hasRoleType(ERoleType.ADMIN))
            requestRefundMoney = requestRefundMoneyRepository.findByEntityId(requestRefundMoneyId);
        else
            requestRefundMoney = requestRefundMoneyRepository.findByEntityId(requestRefundMoneyId, BaseUtils.getCurrentUser().getId());

        if (requestRefundMoney == null)
            throw new ResourceNotFoundException(requestRefundMoneyId.toString(), "common.requestRefundMoney.id_notFound" , requestRefundMoneyId );
        return requestRefundMoney;
    }

    @Transactional
    @Override
    public Long createUserRequestRefundMoney(CRequestRefundMoney cRequestRefundMoney) {
        //this.checkBankAccountInfo( BaseUtils.getCurrentUser().getId());
        return this.createRequestRefundMoney(cRequestRefundMoney,BaseUtils.getCurrentUser().getId(),ERefundType.USER_REQUEST,null);
    }

    private BankAccount checkAndUpdateBankAccount(CRequestRefundMoney cRequestRefundMoney,Long userId){
        if(cRequestRefundMoney.getBankAccountId()==null && Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestName()) && Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestValue()))
            throw new FinancialException("invalid Data","common.bankAccount.data_invalid");

        if(cRequestRefundMoney.getBankAccountId()==null && (Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestName()) || Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestValue())))
            throw new FinancialException("invalid Data","common.bankAccount.data_invalid");

        if(cRequestRefundMoney.getBankAccountId()!=null){
            BankAccount bankAccount=this.getBankAccountInfo(cRequestRefundMoney.getBankAccountId());
            if (!Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestName()) && !Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestValue())  && !bankAccount.getValueByFinanceDestName(cRequestRefundMoney.getFinanceDestName()).equals(cRequestRefundMoney.getFinanceDestValue())){
                bankAccount.setValueByFinanceDestName(cRequestRefundMoney.getFinanceDestName(),cRequestRefundMoney.getFinanceDestValue());
                return bankAccountRepository.save(bankAccount);
            }else
                return bankAccount;
        }else if(!Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestName()) && !Utils.isStringSafeEmpty(cRequestRefundMoney.getFinanceDestValue())){
            BankAccount bankAccount=new BankAccount();
            bankAccount.setTitle("Bank Account Info");
            bankAccount.setUserId(userId);
            bankAccount.setValueByFinanceDestName(cRequestRefundMoney.getFinanceDestName(),cRequestRefundMoney.getFinanceDestValue());
            return bankAccountRepository.save(bankAccount);
        }
        return null;
    }

    @Transactional
    @Override
    public Long createRequestRefundMoney(CRequestRefundMoney cRequestRefundMoney,Long userId,ERefundType eRefundType,Long refundForId){
        if (this.getAccountInfoByUserId(userId).getAvailableBalance()<cRequestRefundMoney.getReqAmount())
            throw new FinancialException("Balance Less than Amount","common.account.req_balance_notEnough");
        if(cRequestRefundMoney.getReqAmount()< commonService.getMinRequestRefundMoney())
            throw new FinancialException("Amount Less Than Min Amount","common.account.req_minAmount_low");

        BankAccount bankAccount=this.checkAndUpdateBankAccount(cRequestRefundMoney,userId);
        this.checkBankAccountInfo(bankAccount);
        this.blockAccount(cRequestRefundMoney.getAccountId(),cRequestRefundMoney.getReqAmount());
        RequestRefundMoney requestRefundMoney=new RequestRefundMoney();

        requestRefundMoney.setAccount(this.getAccountInfoById(cRequestRefundMoney.getAccountId()));
        requestRefundMoney.setFinanceDestName(cRequestRefundMoney.getFinanceDestName());
        requestRefundMoney.setFinanceDestCaption(this.getActiveFinanceDestNumber(cRequestRefundMoney.getFinanceDestName()).getCaption());
        requestRefundMoney.setFinanceDestValue(bankAccount.getValueByFinanceDestName(cRequestRefundMoney.getFinanceDestName()));
        //requestRefundMoney.setCreateDate(new Date());
        //requestRefundMoney.setModifyDate(new Date());
        requestRefundMoney.setReqUserId(userId);
        requestRefundMoney.setRefundTypeId(eRefundType.getId());
        requestRefundMoney.setRefundForId(refundForId);
        requestRefundMoney.setReqDesc(cRequestRefundMoney.getReqDesc());
        requestRefundMoney.setReqAmount(cRequestRefundMoney.getReqAmount());
        requestRefundMoney.setStatus(0);
        requestRefundMoney=requestRefundMoneyRepository.save(requestRefundMoney);

        return requestRefundMoney.getId();
    }

    @Override
    public Object[] getRequestRefundValidStatusesTo(Integer currentStatus) {
        return AccountingServiceImpl.getStaticRequestRefundValidStatusesTo(currentStatus);
    }

    public static Object[] getStaticRequestRefundValidStatusesTo(Integer currentStatus) {
        Map<Integer, TypeWrapper> result = StatusMaps.REFUND_VALID_CHANGE_STATUS_MAP.get(currentStatus).entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> ERefundStatus.asObjectWrapper(entry.getValue())));
        return result.values().toArray();
    }

    public static Object[] getStaticRequestRefundValidStatusesTo(Integer currentStatus, Long userId) {
        Map<Integer, TypeWrapper> result = new HashMap<Integer, TypeWrapper>(StatusMaps.REFUND_VALID_CHANGE_STATUS_MAP.get(currentStatus).entrySet().stream().collect(Collectors.toMap(
                                                            entry -> entry.getKey(),
                                                            entry -> ERefundStatus.asObjectWrapper(entry.getValue()))));
        if(Utils.getCurrentUserId() != userId)
            result.remove(4);
        return result.values().toArray();
    }

    @Transactional
    @Override
    public String cancelRequestRefundMoney(Long requestRefundMoneyId) {
        RequestRefundMoney requestRefundMoney =this.getRequestRefundMoneyInfo(requestRefundMoneyId);
        if (StatusMaps.REFUND_VALID_CHANGE_STATUS_MAP.get(requestRefundMoney.getStatus()).get(ERefundStatus.USER_CANCEL.getId()) == null)
            throw new InvalidDataException("Invalid Data", "common.processRefundMoney.state_invalid" , ERefundStatus.captionOf(requestRefundMoney.getStatus()) );

        requestRefundMoney.setStatus(ERefundStatus.USER_CANCEL.getId());
        requestRefundMoneyRepository.save(requestRefundMoney);

        this.unBlockUserAccount(requestRefundMoney.getReqUserId(), requestRefundMoney.getReqAmount());

        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String processRequestRefundMoney(CProcessRequestRefundMoney cProcessRequestRefundMoney) {

        RequestRefundMoney requestRefundMoney =this.getRequestRefundMoneyInfo(cProcessRequestRefundMoney.getId());
        ERefundType eRefundType=ERefundType.valueOf(requestRefundMoney.getRefundTypeId());
        ERefundStatus eRefundStatus=ERefundStatus.valueOf(cProcessRequestRefundMoney.getStatus());


        if (StatusMaps.REFUND_VALID_CHANGE_STATUS_MAP.get(requestRefundMoney.getStatus()).get(eRefundStatus.getId()) == null)
            throw new InvalidDataException("Invalid Data", "common.processRefundMoney.state_invalid" , ERefundStatus.captionOf(requestRefundMoney.getStatus()) );

        if (eRefundStatus==ERefundStatus.DEPOSIT) {
            if (cProcessRequestRefundMoney.getPayBankId()==null || cProcessRequestRefundMoney.getPayBankId()<=0)
                throw new InvalidDataException("Invalid Data", "common.processRefundMoney.fromBank.id_required");
            requestRefundMoney.setPayBank(this.getBankInfo(cProcessRequestRefundMoney.getPayBankId()));

            if (Utils.isStringSafeEmpty(cProcessRequestRefundMoney.getPayBankRef()))
                throw new InvalidDataException("Invalid Data", "common.processRefundMoney.fromBank.reference_required");
            requestRefundMoney.setPayBankRef(cProcessRequestRefundMoney.getPayBankRef());

            if (Utils.isStringSafeEmpty(cProcessRequestRefundMoney.getPayDate()))
                throw new InvalidDataException("Invalid Data", "common.processRefundMoney.date_required");

            Date paydate = calendarService.getDateFromString(cProcessRequestRefundMoney.getPayDate());

            if(paydate.getTime()>(new Date()).getTime())
               throw new InvalidDataException("Invalid Data", "global.date_invalid");
            requestRefundMoney.setPayDate(paydate);
            requestRefundMoney.setPayDesc(cProcessRequestRefundMoney.getPayDsc());

            if (!Utils.isStringSafeEmpty(requestRefundMoney.getFinanceDestName()) && requestRefundMoney.getFinanceDestName().equalsIgnoreCase("account_number")
                    && (cProcessRequestRefundMoney.getToBankId()==null || cProcessRequestRefundMoney.getToBankId()<=0))
                throw new InvalidDataException("Invalid Data", "common.processRefundMoney.toBank.id_required");


            if (cProcessRequestRefundMoney.getToBankId()!=null && cProcessRequestRefundMoney.getToBankId()>0)
                requestRefundMoney.setToBank(this.getBankInfo(cProcessRequestRefundMoney.getToBankId()));

            if (!Utils.isStringSafeEmpty(cProcessRequestRefundMoney.getFinanceDestValue()))
                //throw new InvalidDataException("Invalid Data", "common.processRefundMoney.fromBank.accountNumber_required");
                requestRefundMoney.setFinanceDestValue(cProcessRequestRefundMoney.getFinanceDestValue());

            this.unBlockUserAccount(requestRefundMoney.getReqUserId(), requestRefundMoney.getReqAmount());

            ///////////todo uncomment after implement get monay
            TransactionType transactionType=this.getTransactionTypeInfo(ETransactionType.GET_MONEY_BY_REQUEST.getId().longValue());
            String description=BaseUtils.getMessageResource("accounting.depositToAccountNumber") + " " + requestRefundMoney.getFinanceDestCaption() + " " + requestRefundMoney.getFinanceDestValue();
            Transaction payTransaction=this.withdrawAccount(requestRefundMoney.getAccount().getId() , requestRefundMoney.getReqAmount(), EOperation.GET_MONEY.getOperationCode(), transactionType , requestRefundMoney.getPayBankRef(), description, null, null, null, false, false);

            //Calculate and get Wage
            OperationType operationType =operationService.getOperationTypeInfoByCode(EOperation.GET_MONEY.getOperationCode());
            operationService.calculateAndGetWageFromAccount(operationType,requestRefundMoney.getReqUserId(),
                                                            requestRefundMoney.getAccount().getId(),requestRefundMoney.getReqAmount(),operationType.getDescription()/*description*/, requestRefundMoney.getPayBankRef(),
                                                            null,null,false);
            requestRefundMoney.setPayTransaction(payTransaction);
        }else if (/*eRefundStatus==ERefundStatus.USER_CANCEL ||*/ eRefundStatus==ERefundStatus.REJECT) {
            if (Utils.isStringSafeEmpty(cProcessRequestRefundMoney.getPayDsc()))
                throw new InvalidDataException("Invalid Data", "common.processRefundMoney.desc_required");
            requestRefundMoney.setPayDesc(cProcessRequestRefundMoney.getPayDsc());

            this.unBlockUserAccount(requestRefundMoney.getReqUserId(), requestRefundMoney.getReqAmount());
        }else if (eRefundStatus==ERefundStatus.WAIT_FOR_DEPOSIT) {

        }else{
            throw new InvalidDataException("Invalid Data", "common.processRefundMoney.state_invalid1");
        }

        requestRefundMoney.setStatus(cProcessRequestRefundMoney.getStatus());
        requestRefundMoney.setPayUserId(BaseUtils.getCurrentUser().getId());
        requestRefundMoneyRepository.save(requestRefundMoney);
        return Utils.getMessageResource("global.operation.success_info");

    }

    @Override
    public ResultListPageable<RequestRefundMoneyWrapper> getUserRequestRefundMonies(Map<String, Object> requestParams) {
        return this.getAllRequestRefundMonies(requestParams);
    }

    @Override
    public ResultListPageable<RequestRefundMoneyWrapper> getAllRequestRefundMonies(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(REQUEST_REFUND_MONEY_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "requestRefundMoneyMapping");
        ResultListPageable<RequestRefundMoneyWrapper> requestRefundMoneyGeneral=this.getRequestRefundMoneyGeneral(requestParams);
        return requestRefundMoneyGeneral;
    }

    @Override
    public RequestRefundMoneyWrapper getRequestRefundMoneyWrapperInfo(Long requestRefundMoneyId) {
        if (requestRefundMoneyId == null)
            throw new InvalidDataException("Invalid Data", "common.requestRefundMoney.id_required");

        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("id",requestRefundMoneyId);
        requestParams.put("start",0);
        requestParams.put("count",1);
        requestParams.put("resultSetMapping", "requestRefundMoneyMapping");

        ResultListPageable<RequestRefundMoneyWrapper> result=this.getRequestRefundMoneyGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);
        throw new ResourceNotFoundException(requestRefundMoneyId.toString(), "common.requestRefundMoney.id_notFound" , requestRefundMoneyId );
    }

    @Override
    public List<TypeWrapper> getRefundStatuses() {
        return ERefundStatus.getAllAsObjectWrapper();
    }


    private String getRequestRefundMoneyBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return "SELECT r.rrm_id as id, a.acc_id as accountId, a.acc_name as accountName, r.rrm_status as status, r.rrm_req_usr_id as reqUserId, r.rrm_req_dsc as reqDesc, \n" +
                "      r.rrm_amount as reqAmount, r.rrm_create_date as createDate, r.rrm_modify_date as modifyDate, r.rrm_pay_usr_id as payUserId, r.rrm_pay_date as payDate, \n" +
                "      r.rrm_pay_bnk_ref as payBankRef, r.rrm_pay_desc as payDesc, r.rrm_fdn_name as financeDestName, r.rrm_fdn_caption as financeDestCaption, \n " +
                "      r.rrm_fdn_value as financeDestValue, r.rrm_refund_type_id as refundTypeId, r.rrm_refund_for_id as refundForId, r.rrm_pay_bnk_id as payBankId, \n " +
                "      pay_b.bnk_name as payBankName, r.rrm_to_bnk_id as toBankId, to_b.bnk_name as toBankName, \n " +
                "      nvl(concat(concat(ur.usr_last_name,'-'),ur.usr_username),ur.usr_username) as reqUserName, " +
                "      nvl(concat(concat(up.usr_last_name,'-'),up.usr_username),up.usr_username) as payUserName\n" ;
    }

    private String getRequestRefundMoneyBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getRequestRefundMoneyBaseQueryBody(Map<String, Object> requestParams) {
        String queryString=" FROM sc_request_refund_money r  \n" +
                " inner JOIN sc_user ur on r.rrm_req_usr_id=ur.usr_id  \n" +
                " left  JOIN sc_user up on r.rrm_pay_usr_id=up.usr_id  \n" +
                " inner JOIN sc_account a on a.acc_id=r.rrm_acc_id  \n" +
                " LEFT OUTER JOIN sc_bank pay_b on r.rrm_pay_bnk_id = pay_b.bnk_id  \n" +
                " LEFT OUTER JOIN sc_bank to_b on r.rrm_to_bnk_id = to_b.bnk_id  \n" ;
        return  queryString;
    }

    private ResultListPageable<RequestRefundMoneyWrapper> getRequestRefundMoneyGeneral(Map<String, Object> requestParams) {

        String queryString = this.getRequestRefundMoneyBaseQueryHead(requestParams)+ this.getRequestRefundMoneyBaseQueryBody(requestParams);

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getRequestRefundMoneyBaseCountQueryHead(requestParams)+this.getRequestRefundMoneyBaseQueryBody(requestParams);

        return dynamicQueryHelper.getDataGeneral(requestParams,REQUEST_REFUND_MONEY_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,RequestRefundMoneyWrapper.class);
    }

    //#endregion


    //#region AccountPolicyProfile

    @Override
    public AccountPolicyProfile getAccountPolicyProfileInfo(Long accountPolicyProfileId) {
        return this.getAccountPolicyProfileInfo(accountPolicyProfileId,false);
    }

    @Override
    public AccountPolicyProfile getAccountPolicyProfileInfo(Long accountPolicyProfileId, Boolean onlyForUsed) {
        if (accountPolicyProfileId == null)
            throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.id_required");

        Optional<AccountPolicyProfile> result =null;
        if(hasRoleType(ERoleType.ADMIN))
            result=accountPolicyProfileRepository.findByEntityId(accountPolicyProfileId);
        else {
            if(onlyForUsed)
                result = accountPolicyProfileRepository.findByEntityIdForUsed(accountPolicyProfileId, Utils.getCurrentUserId());
            else
               result = accountPolicyProfileRepository.findByEntityId(accountPolicyProfileId, Utils.getCurrentUserId());
        }

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.accountPolicyProfile.id_notFound");
        return result.get();
    }

    @Override
    public List<AccountPolicyProfile> getAccountPolicyProfiles(Long accountTypeId,Long userId) {
        if(!hasRoleType(ERoleType.ADMIN) || userId==null)
            userId=Utils.getCurrentUserId();
        return accountPolicyProfileRepository.findByUserIdAccountTypeId(accountTypeId,userId);
    }

    @Transactional
    @Override
    public AccountPolicyProfile createAccountPolicyProfile(AccountPolicyProfileDto accountPolicyProfileDto) {
        AccountPolicyProfile accountPolicyProfile=new AccountPolicyProfile();
        accountPolicyProfile=this.mapAccountPolicyProfileDroToDB(accountPolicyProfile,accountPolicyProfileDto);
        accountPolicyProfile=accountPolicyProfileRepository.save(accountPolicyProfile);
        accountPolicyProfile=this.addAccountPolicyProfileOperationType(accountPolicyProfile,accountPolicyProfileDto.getAccountPolicyProfileOperationTypesDto());
        return accountPolicyProfile;
    }

    @Transactional
    @Override
    public String editAccountPolicyProfile(AccountPolicyProfileDto accountPolicyProfileDto) {
        AccountPolicyProfile accountPolicyProfile=this.getAccountPolicyProfileInfo(accountPolicyProfileDto.getId());

        accountPolicyProfile=this.mapAccountPolicyProfileDroToDB(accountPolicyProfile,accountPolicyProfileDto);
        accountPolicyProfile=accountPolicyProfileRepository.save(accountPolicyProfile);
        accountPolicyProfile=this.addAccountPolicyProfileOperationType(accountPolicyProfile,accountPolicyProfileDto.getAccountPolicyProfileOperationTypesDto());

        return Utils.getMessageResource("global.operation.success_info");
    }

    private AccountPolicyProfile mapAccountPolicyProfileDroToDB(AccountPolicyProfile accountPolicyProfile,AccountPolicyProfileDto accountPolicyProfileDto){
       accountPolicyProfile.setName(accountPolicyProfileDto.getName());
       accountPolicyProfile.setDescription(accountPolicyProfileDto.getDescription());
       accountPolicyProfile.setActive(accountPolicyProfileDto.getActive());
       if(accountPolicyProfile.getId()==null) {
           accountPolicyProfile.setAccountType(this.getAccountTypeInfo(accountPolicyProfileDto.getAccountTypeId()));
           if (hasRoleType(ERoleType.ADMIN)) {
               if (accountPolicyProfileDto.getUserId() != null && accountPolicyProfileDto.getUserId() > 0)
                   accountPolicyProfile.setUserId(userService.getUserInfo(accountPolicyProfileDto.getUserId()).getId());
           } else
               accountPolicyProfile.setUserId(Utils.getCurrentUserId());
       }
        if(accountPolicyProfile.getId()==null) {
            if (accountPolicyProfileRepository.countAccountPolicyProfileByName(accountPolicyProfile.getName(),accountPolicyProfile.getUserId()) > 0)
                throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.name_exist");
        }else {
            if (accountPolicyProfileRepository.countAccountPolicyProfileByName(accountPolicyProfile.getId(),accountPolicyProfile.getName(),accountPolicyProfile.getUserId()) > 0)
                throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.name_exist");
        }

        return accountPolicyProfile;
    }

    private AccountPolicyProfile addAccountPolicyProfileOperationType(AccountPolicyProfile accountPolicyProfile,List<AccountPolicyProfileOperationTypeDto> accountPolicyProfileOperationTypesDto){
        if(accountPolicyProfile.getAccountPolicyProfileOperationTypes()!=null) {
            for (AccountPolicyProfileOperationType accountPolicyProfileOperationType : accountPolicyProfile.getAccountPolicyProfileOperationTypes()) {
                if (accountPolicyProfileOperationTypesDto.stream().filter(o->accountPolicyProfileOperationType.getId().equals(o.getId())).count()<=0)
                    accountPolicyProfileOperationTypeRepository.delete(accountPolicyProfileOperationType);
            }
        }

        List<AccountPolicyProfileOperationType> addList=new ArrayList<>();
        for (AccountPolicyProfileOperationTypeDto accountPolicyProfileOperationTypeDto :accountPolicyProfileOperationTypesDto) {
            AccountPolicyProfileOperationType accountPolicyProfileOperationType=null;
            if(accountPolicyProfileOperationTypeDto.getId()!=null)
               accountPolicyProfileOperationType=this.getAccountPolicyProfileOperationTypeInfo(accountPolicyProfileOperationTypeDto.getId());
            if(accountPolicyProfileOperationType==null){
                accountPolicyProfileOperationType=new AccountPolicyProfileOperationType();
                accountPolicyProfileOperationType.setAccountPolicyProfile(accountPolicyProfile);
                accountPolicyProfileOperationType.setOperationType( operationService.getOperationTypeInfo(accountPolicyProfileOperationTypeDto.getOperationTypeId()));
            }
            if(accountPolicyProfileOperationType.getOperationType().getSourceType() !=ETransactionSourceType.WALLET.getId() &&
                    accountPolicyProfileOperationType.getOperationType().getSourceType() !=ETransactionSourceType.NONE.getId())
                throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.operationType_invalid");

            accountPolicyProfileOperationType.setMinAmount(accountPolicyProfileOperationTypeDto.getMinAmount());
            accountPolicyProfileOperationType.setMaxAmount(accountPolicyProfileOperationTypeDto.getMaxAmount());
            accountPolicyProfileOperationType.setGlobalMaxDailyAmount(accountPolicyProfileOperationTypeDto.getGlobalMaxDailyAmount());
            if (accountPolicyProfileOperationTypeDto.getDefaultAmounts() != null && accountPolicyProfileOperationTypeDto.getDefaultAmounts().size() > 0)
                accountPolicyProfileOperationType.setDefaultAmounts(accountPolicyProfileOperationTypeDto.getDefaultAmounts().stream().map(s -> s.toString()).collect(Collectors.joining(",")));
            else
                accountPolicyProfileOperationType.setDefaultAmounts(null);
            accountPolicyProfileOperationType.setOrder(accountPolicyProfileOperationTypeDto.getOrder());
            accountPolicyProfileOperationType.setNotify(accountPolicyProfileOperationTypeDto.getNotify());

            addList.add(accountPolicyProfileOperationType);
        }
        if(addList.size()>0)
            addList=accountPolicyProfileOperationTypeRepository.saveAll(addList);
        accountPolicyProfile.setAccountPolicyProfileOperationTypes(addList);
        return accountPolicyProfile;
    }

    @Transactional
    @Override
    public String deleteAccountPolicyProfile(Long accountPolicyProfileId) {
        AccountPolicyProfile accountPolicyProfile=this.getAccountPolicyProfileInfo(accountPolicyProfileId);
        if(accountPolicyProfileRepository.UsedAccountPolicyProfileForUser(accountPolicyProfileId) ||accountPolicyProfileRepository.UsedAccountPolicyProfileForCredit(accountPolicyProfileId)
        ||accountPolicyProfileRepository.UsedAccountPolicyProfileForAccountType(accountPolicyProfileId))
          throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.delete_used");

        accountPolicyProfileOperationTypeRepository.deleteAll(accountPolicyProfile.getAccountPolicyProfileOperationTypes());
        accountPolicyProfileRepository.delete(accountPolicyProfile);
        return Utils.getMessageResource("global.delete_info");
    }

    @Override
    public AccountPolicyProfileOperationType getAccountPolicyProfileOperationTypeInfo(Long accountPolicyProfileOperationTypeId) {
        if (accountPolicyProfileOperationTypeId == null)
            throw new InvalidDataException("Invalid Data", "common.accountPolicyProfileOperationType.id_required");

        Optional<AccountPolicyProfileOperationType> result = accountPolicyProfileOperationTypeRepository.findByEntityId(accountPolicyProfileOperationTypeId);
        if (!result.isPresent())
            return null;
        return result.get();

    }


    //#endregion


    //#region UserAccountPolicyProfile

    @Override
    public UserAccountPolicyProfile getUserAccountPolicyProfileInfo(Long userAccountPolicyProfileId) {
        if (userAccountPolicyProfileId == null)
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyProfile.id_required");
        Optional<UserAccountPolicyProfile> result=null;
        if(hasRoleType(ERoleType.ADMIN))
            result = userAccountPolicyProfileRepository.findByEntityId(userAccountPolicyProfileId);
        else
            result = userAccountPolicyProfileRepository.findByEntityId(userAccountPolicyProfileId,Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyProfile.id_notFound");
        return result.get();
    }

    @Override
    public UserAccountPolicyProfile getUserAccountPolicyProfileInfo(Long accountId, Long accountPolicyProfileId, Long userId) {
        if (accountId == null)
            throw new InvalidDataException("Invalid Data", "common.account.id_required");
        if (userId == null)
            throw new InvalidDataException("Invalid Data", "common.user.id_required");
        Optional<UserAccountPolicyProfile> result = userAccountPolicyProfileRepository.findByAccountIdAndAccountPolicyProfileIdAndUserId(accountId,accountPolicyProfileId,userId);

        if (!result.isPresent())
            return null;
           //throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.id_notFound");
        return result.get();
    }

    @Override
    public UserAccountPolicyProfile getUserAccountPolicyProfileInfo(Long accountId, Long userId) {
        if (accountId == null)
            throw new InvalidDataException("Invalid Data", "common.account.id_required");
        if (userId == null)
            throw new InvalidDataException("Invalid Data", "common.user.id_required");
        Optional<UserAccountPolicyProfile> result = userAccountPolicyProfileRepository.findByAccountIdAndUserId(accountId,userId);
        if (!result.isPresent())
            return null;
        return result.get();
    }

    //#endregion



    //#region ManualTransactionRequest

    @Override
    public ManualTransactionRequest getManualTransactionRequestInfo(Long manualTransactionRequestId) {
        if (manualTransactionRequestId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<ManualTransactionRequest> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = manualTransactionRequestRepository.findByEntityId(manualTransactionRequestId);
        else
            result = manualTransactionRequestRepository.findByEntityId(manualTransactionRequestId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public ManualTransactionRequestWrapper getManualTransactionRequestWrapperInfo(Long manualTransactionRequestId) {
        if(manualTransactionRequestId==null)
            throw new InvalidDataException("Invalid Data", "common.manualTransactionRequest.id_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("id",manualTransactionRequestId);
        requestParams.put("resultSetMapping", "manualTransactionRequestMapping");
        requestParams.put("loadDetails", true);

        ResultListPageable<ManualTransactionRequestWrapper> result=this.getManualTransactionRequestGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);
        throw new ResourceNotFoundException(manualTransactionRequestId.toString(), "common.manualTransactionRequest.id_notFound" );
    }

    @Override
    public ResultListPageable<ManualTransactionRequestWrapper> getManualTransactionRequestWrappers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(MANUAL_TRANSACTION_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "manualTransactionRequestMapping");
        ResultListPageable<ManualTransactionRequestWrapper> requestGeneral=this.getManualTransactionRequestGeneral(requestParams);
        return requestGeneral;
    }

    @Override
    public List<TypeWrapper> getManualTransactionRequestStatuses() {
        return EManualTransactionRequestStatus.getAllAsObjectWrapper();
    }

    @Transactional
    @Override
    public Long createManualTransactionRequest(ManualTransactionRequestDto manualTransactionRequestDto) {
        ManualTransactionRequest manualTransactionRequest = new ManualTransactionRequest();
        manualTransactionRequest.setAccount(this.getAccountInfoById(manualTransactionRequestDto.getAccountId()));
        manualTransactionRequest = this.mapManualTransactionRequestDtoToDb(manualTransactionRequestDto, manualTransactionRequest);
        manualTransactionRequest = manualTransactionRequestRepository.save(manualTransactionRequest);
        return manualTransactionRequest.getId();//Utils.getMessageResource("global.operation.success_info");
    }

    private ManualTransactionRequest mapManualTransactionRequestDtoToDb(ManualTransactionRequestDto manualTransactionRequestDto, ManualTransactionRequest manualTransactionRequest) {
        if(manualTransactionRequestDto.getAmount()<=0)
            throw new InvalidDataException("Invalid Data", "common.manualTransactionRequest.amount_invalid");
        if(manualTransactionRequest.getId()!=null){
            EManualTransactionRequestStatus eManualTransactionRequestStatus=EManualTransactionRequestStatus.valueOf(manualTransactionRequest.getStatus());
            if(eManualTransactionRequestStatus!=EManualTransactionRequestStatus.TEMPORARY)
               throw new InvalidDataException("Invalid Data", "common.manualTransactionRequest.status_editInvalid");
        }else{
            manualTransactionRequest.setStatus(EManualTransactionRequestStatus.TEMPORARY.getId());
        }
        manualTransactionRequest.setAmount(manualTransactionRequestDto.getAmount());
        manualTransactionRequest.setDescription(manualTransactionRequestDto.getDescription());
        manualTransactionRequest.setReference(manualTransactionRequestDto.getReference());
        manualTransactionRequest.setReferenceDate(calendarService.getDateFromString(manualTransactionRequestDto.getReferenceDate()));

        if (manualTransactionRequest.getReferenceDate().getTime() > (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.manualTransactionRequest.referenceDate_invalid");

        return manualTransactionRequest;
    }

    @Transactional
    @Override
    public String editManualTransactionRequest(ManualTransactionRequestDto manualTransactionRequestDto) {
        ManualTransactionRequest manualTransactionRequest = this.getManualTransactionRequestInfo(manualTransactionRequestDto.getId());
        manualTransactionRequest = this.mapManualTransactionRequestDtoToDb(manualTransactionRequestDto, manualTransactionRequest);
        manualTransactionRequest = manualTransactionRequestRepository.save(manualTransactionRequest);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String removeManualTransactionRequest(Long manualTransactionRequestId) {
        ManualTransactionRequest manualTransactionRequest = this.getManualTransactionRequestInfo(manualTransactionRequestId);
        if(manualTransactionRequest.getId()!=null && manualTransactionRequest.getStatus()>0)
            throw new InvalidDataException("Invalid Data", "common.manualTransactionRequest.status_removeDenied");
        manualTransactionRequestRepository.delete(manualTransactionRequest);
        return Utils.getMessageResource("global.operation.success_info");

    }

    @Transactional
    @Override
    public String approveManualTransactionRequest(ManualTransactionRequestApproveDto  manualTransactionRequestApproveDto) {
        ManualTransactionRequest manualTransactionRequest = this.getManualTransactionRequestInfo(manualTransactionRequestApproveDto.getId());
        EManualTransactionRequestStatus eManualTransactionRequestStatus=EManualTransactionRequestStatus.valueOf(manualTransactionRequest.getStatus());
        if(eManualTransactionRequestStatus!=EManualTransactionRequestStatus.TEMPORARY)
            throw new InvalidDataException("Invalid Data", "common.manualTransactionRequest.status_as" , eManualTransactionRequestStatus.getCaption());
        EManualTransactionRequestStatus eNewManualTransactionRequestStatus=EManualTransactionRequestStatus.valueOf(manualTransactionRequestApproveDto.getStatus());
        manualTransactionRequest.setStatus(eNewManualTransactionRequestStatus.getId());
        manualTransactionRequest.setApprovedDescription(manualTransactionRequestApproveDto.getApprovedDescription());
        manualTransactionRequest.setApprovedBy(Utils.getCurrentUserId());
        manualTransactionRequest.setApprovedDate(new Date());
        if(eNewManualTransactionRequestStatus==EManualTransactionRequestStatus.APPROVED){
            TransactionType transactionType=this.getTransactionTypeInfo(ETransactionType.MANUAL_DEPOSIT.getId().longValue());
            String transactionDesc = Utils.isStringSafeEmpty(manualTransactionRequest.getDescription()) ? ETransactionType.MANUAL_DEPOSIT.getCaption() : ETransactionType.MANUAL_DEPOSIT.getCaption() + "-" + manualTransactionRequest.getDescription();
            Transaction depositTransaction =this.depositAccount(manualTransactionRequest.getAccount().getId(),manualTransactionRequest.getAmount(), EOperation.MANUAL_DEPOSIT.getOperationCode(),
                    transactionType, manualTransactionRequest.getReference(), transactionDesc, null, null,null);
            manualTransactionRequest.setTransaction(depositTransaction);

        }else if(Utils.isStringSafeEmpty(manualTransactionRequestApproveDto.getApprovedDescription()))
                throw new InvalidDataException("Invalid Data","common.manualTransactionRequest.approvedDescription_required");

        manualTransactionRequest=manualTransactionRequestRepository.save(manualTransactionRequest);
        return Utils.getMessageResource("global.operation.success_info");

    }

    private String getManualTransactionRequestBaseQueryHead(Map<String, Object> requestParams ) {
        return "select  mtr.mtr_id as id, mtr.mtr_acc_id as accountId,acc.acc_name as accountName, act.act_id as accountTypeId,\n" +
                "        act.act_desc as accountTypeDesc, acc.acc_usr_id as userId,u.usr_username  as userName, mtr.mtr_trn_id as transactionId,\n" +
                "        mtr.mtr_status as status, mtr.mtr_reference as reference, mtr.mtr_reference_date as referenceDate, mtr.mtr_description as description,\n" +
                "        mtr.mtr_amount as amount, mtr.mtr_approved_by as approvedBy, mtr.mtr_approved_date as approvedDate,mtr.mtr_approved_description as approvedDescription, \n" +
                "        mtr.mtr_create_by as createBy, mtr.mtr_create_date as createDate, mtr.mtr_modify_by as modifyBy, mtr.mtr_modify_date as modifyDate \n" ;

    }
    private String getManualTransactionRequestBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getManualTransactionRequestBaseQueryBody(Map<String, Object> requestParams) {
        String queryString=" from sc_manual_transaction  mtr \n" +
                "inner join  sc_account acc on (mtr.mtr_acc_id=acc.acc_id) \n" +
                "inner join  sc_account_type act on (acc.acc_act_id=act.act_id) \n" +
                "inner join sc_user u on (acc.acc_usr_id=u.usr_id) \n";
        return  queryString;
    }

    private ResultListPageable<ManualTransactionRequestWrapper> getManualTransactionRequestGeneral(Map<String, Object> requestParams) {
        String queryString = this.getManualTransactionRequestBaseQueryHead(requestParams)+
                Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getManualTransactionRequestBaseQueryBody(requestParams));

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getManualTransactionRequestBaseCountQueryHead(requestParams)+
                    Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getManualTransactionRequestBaseQueryBody(requestParams));

        return dynamicQueryHelper.getDataGeneral(requestParams,MANUAL_TRANSACTION_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString, ManualTransactionRequestWrapper.class);
    }

    //#endregion ManualTransactionRequest

    //#region Accounting Dashboard

    @Override
    public AccountingDashboardWrapper getAccountingDashboardWrapper() {
        return accountRepository.findDashboardWrapperSummery();
    }

    //#endregion Accounting Dashboard






}
