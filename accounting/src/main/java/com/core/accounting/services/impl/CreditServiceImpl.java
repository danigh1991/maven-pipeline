package com.core.accounting.services.impl;

import com.core.accounting.model.contextmodel.AccountCreditDetailDto;
import com.core.accounting.model.contextmodel.AccountDto;
import com.core.accounting.model.contextmodel.MerchantLimitDto;
import com.core.accounting.model.contextmodel.UserAccountPolicyCreditDetailDto;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.enums.ECreditType;
import com.core.accounting.model.enums.ECreditViewType;
import com.core.accounting.model.wrapper.MerchantLimitWrapper;
import com.core.accounting.model.wrapper.UserAccountPolicyCreditDetailWrapper;
import com.core.accounting.repository.*;
import com.core.accounting.services.AccountingService;
import com.core.accounting.services.CreditService;
import com.core.accounting.services.MerchantService;
import com.core.common.services.UserService;
import com.core.common.services.impl.AbstractService;
import com.core.common.util.Utils;
import com.core.datamodel.model.enums.ERoleType;
import com.core.services.CalendarService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.model.wrapper.ResultListPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("creditServiceImpl")
public class CreditServiceImpl extends AbstractService implements CreditService {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private AccountCreditDetailRepository accountCreditDetailRepository;
    @Autowired
    private UserAccountPolicyCreditDetailRepository userAccountPolicyCreditDetailRepository;
    @Autowired
    private AccountMerchantLimitRepository accountMerchantLimitRepository;
    @Autowired
    private AccountCreditMerchantLimitRepository accountCreditMerchantLimitRepository;
    @Autowired
    private UserAccountPolicyProfileRepository userAccountPolicyProfileRepository;



    //#region AccountCredit Limit


    @Override
    public List<MerchantLimitWrapper> getAccountMerchantLimitWrappers(Long accountId) {
        if (accountId == null)
            throw new InvalidDataException("Invalid Data", "common.account.id_required");
        if (hasRoleType(ERoleType.ADMIN))
            return accountMerchantLimitRepository.findAccountMerchantLimitWrappersByAccountId(accountId);
        else
            return accountMerchantLimitRepository.findAccountMerchantLimitWrappersByAccountIdAndUserId(accountId ,Utils.getCurrentUserId());
    }

    @Override
    public MerchantLimitWrapper getAccountMerchantLimitWrapperInfo(Long accountMerchantLimitId) {
        if( accountMerchantLimitId==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.id_required");
        MerchantLimitWrapper accountMerchantLimitWrapper=null;
        if (hasRoleType(ERoleType.ADMIN))
            accountMerchantLimitWrapper = accountMerchantLimitRepository.findAccountMerchantLimitWrappersById(accountMerchantLimitId);
        else
            accountMerchantLimitWrapper = accountMerchantLimitRepository.findAccountMerchantLimitWrappersByIdAndUserId(accountMerchantLimitId,Utils.getCurrentUserId());
        if(accountMerchantLimitWrapper==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.notFount");
        return accountMerchantLimitWrapper;
    }

    @Transactional
    @Override
    public MerchantLimitWrapper addAccountMerchantLimit(MerchantLimitDto merchantLimitDto) {
        if(merchantLimitDto.getUserId()==null && merchantLimitDto.getGroupId()==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.targetId_required");
        Integer type=(merchantLimitDto.getGroupId()!=null?1:2);
        Long targetId=null;
        if(type==1){
            targetId=userService.getUserGroupInfo(merchantLimitDto.getGroupId()).getId();
        }else if(type==2){
            targetId=userService.getUserInfo(merchantLimitDto.getUserId()).getId();
            if(!merchantService.isActiveMerchant(targetId))
                throw new InvalidDataException("Invalid Data", "common.merchant.id_notFound");
        }

        AccountMerchantLimit accountMerchantLimit=null;
        if (hasRoleType(ERoleType.ADMIN))
            accountMerchantLimit = accountMerchantLimitRepository.findByAccountIdAndTargetId(merchantLimitDto.getSourceId(), type,targetId);
        else
            accountMerchantLimit = accountMerchantLimitRepository.findByAccountIdAndTargetId(merchantLimitDto.getSourceId(), type,targetId,Utils.getCurrentUserId());

        if(accountMerchantLimit!=null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.targetId_exist");

        accountMerchantLimit=new AccountMerchantLimit();
        accountMerchantLimit.setAccount(accountingService.getAccountInfoById(merchantLimitDto.getSourceId()));
        accountMerchantLimit.setType(type);
        accountMerchantLimit.setTargetId(targetId);
        accountMerchantLimit=accountMerchantLimitRepository.save(accountMerchantLimit);
        return this.getAccountMerchantLimitWrapperInfo(accountMerchantLimit.getId());
    }

    @Transactional
    @Override
    public String deleteAccountMerchantLimit(Long merchantLimitId) {
        if(merchantLimitId==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.id_required");

        AccountMerchantLimit accountMerchantLimit=null;

        if(hasRoleType(ERoleType.ADMIN))
            accountMerchantLimit = accountMerchantLimitRepository.findByEntityId(merchantLimitId);
        else
            accountMerchantLimit = accountMerchantLimitRepository.findByEntityId(merchantLimitId,Utils.getCurrentUserId());

        if(accountMerchantLimit==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.notFount");

        accountMerchantLimitRepository.delete(accountMerchantLimit);
        return Utils.getMessageResource("global.delete_info");
    }


    @Override
    public List<MerchantLimitWrapper> getAccountCreditMerchantLimitWrappers(Long accountCreditDetailId) {
        if (accountCreditDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.id_required");
        if (hasRoleType(ERoleType.ADMIN))
            return accountCreditMerchantLimitRepository.findAccountCreditMerchantLimitWrappersByAccountCreditDetailId(accountCreditDetailId);
        else
            return accountCreditMerchantLimitRepository.findAccountCreditMerchantLimitWrappersByAccountCreditDetailIdAndUserId(accountCreditDetailId,Utils.getCurrentUserId());
    }

    @Override
    public MerchantLimitWrapper getAccountCreditMerchantLimitWrapperInfo(Long accountCreditMerchantLimitId) {
        if( accountCreditMerchantLimitId==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.id_required");
        MerchantLimitWrapper accountMerchantLimitWrapper=null;
        if (hasRoleType(ERoleType.ADMIN))
            accountMerchantLimitWrapper = accountCreditMerchantLimitRepository.findAccountCreditMerchantLimitWrappersById(accountCreditMerchantLimitId);
        else
            accountMerchantLimitWrapper = accountCreditMerchantLimitRepository.findAccountCreditMerchantLimitWrappersByIdAndUserId(accountCreditMerchantLimitId,Utils.getCurrentUserId());
        if(accountMerchantLimitWrapper==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.notFount");
        return accountMerchantLimitWrapper;
    }

    @Transactional
    @Override
    public MerchantLimitWrapper addAccountCreditMerchantLimit(MerchantLimitDto merchantLimitDto) {
        if(merchantLimitDto.getUserId()==null && merchantLimitDto.getGroupId()==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.targetId_required");

        Integer type=(merchantLimitDto.getGroupId()!=null?1:2);
        Long targetId=null;

        if(type==1){
            targetId=userService.getUserGroupInfo(merchantLimitDto.getGroupId()).getId();
        }else if(type==2){
            targetId=userService.getUserInfo(merchantLimitDto.getUserId()).getId();
            if(!merchantService.isActiveMerchant(targetId))
                throw new InvalidDataException("Invalid Data", "common.merchant.id_notFound");
        }

        AccountCreditMerchantLimit accountCreditMerchantLimit=null;
        if (hasRoleType(ERoleType.ADMIN))
            accountCreditMerchantLimit = accountCreditMerchantLimitRepository.findByAccountCreditIdAndTargetId(merchantLimitDto.getSourceId(), type,targetId);
        else
            accountCreditMerchantLimit = accountCreditMerchantLimitRepository.findByAccountCreditIdAndTargetId(merchantLimitDto.getSourceId(), type,targetId,Utils.getCurrentUserId());

        if(accountCreditMerchantLimit!=null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.targetId_exist");

        accountCreditMerchantLimit=new AccountCreditMerchantLimit();
        accountCreditMerchantLimit.setAccountCreditDetail(this.getAccountCreditDetailInfo(merchantLimitDto.getSourceId()));
        accountCreditMerchantLimit.setType(type);
        accountCreditMerchantLimit.setTargetId(targetId);
        accountCreditMerchantLimit=accountCreditMerchantLimitRepository.save(accountCreditMerchantLimit);

        return this.getAccountCreditMerchantLimitWrapperInfo(accountCreditMerchantLimit.getId());

    }

    @Transactional
    @Override
    public String deleteAccountCreditMerchantLimit(Long merchantLimitId) {
        if( merchantLimitId==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.id_required");
        AccountCreditMerchantLimit accountCreditMerchantLimit=null;
        if (hasRoleType(ERoleType.ADMIN))
            accountCreditMerchantLimit = accountCreditMerchantLimitRepository.findByEntityId(merchantLimitId);
        else
            accountCreditMerchantLimit = accountCreditMerchantLimitRepository.findByEntityId(merchantLimitId,Utils.getCurrentUserId());
        if(accountCreditMerchantLimit==null)
            throw new InvalidDataException("Invalid Data", "common.merchantLimit.notFount");
        accountCreditMerchantLimitRepository.delete(accountCreditMerchantLimit);
        return Utils.getMessageResource("global.delete_info");
    }
    //#endregion

    //#region AccountCreditDetail


    @Override
    public List<AccountCreditDetail> getAccountCreditDetails(Long accountId) {
        if (accountId == null)
            throw new InvalidDataException("Invalid Data", "common.account.id_required");
        if (hasRoleType(ERoleType.ADMIN))
            return accountCreditDetailRepository.findAllByAccountId(accountId);
        else
            return accountCreditDetailRepository.findAllByAccountId(accountId, Utils.getCurrentUserId());
    }

    @Override
    public AccountCreditDetail getAccountCreditDetailInfo(Long accountCreditDetailId) {
        if (accountCreditDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.id_required");

        Optional<AccountCreditDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = accountCreditDetailRepository.findByEntityId(accountCreditDetailId);
        else
            result = accountCreditDetailRepository.findByEntityId(accountCreditDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.accountCreditDetail.id_notFound");
        return result.get();
    }

    @Override
    public AccountCreditDetail getActiveAccountCreditDetailInfo(Long accountCreditDetailId) {
        if (accountCreditDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.id_required");

        Optional<AccountCreditDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = accountCreditDetailRepository.findActiveByEntityId(accountCreditDetailId);
        else
            result = accountCreditDetailRepository.findActiveByEntityId(accountCreditDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.accountCreditDetail.id_required");
        return result.get();
    }

    @Transactional
    @Override
    public AccountCreditDetail createAccountCreditDetail(AccountCreditDetailDto accountCreditDetailDto) {
        AccountCreditDetail accountCreditDetail=new AccountCreditDetail();
        accountCreditDetail.setAccount(accountingService.getAccountInfoById(accountCreditDetailDto.getAccountId()));
        accountCreditDetail.setActive(false);
        ECreditType eCreditType=ECreditType.valueOf(accountCreditDetailDto.getCreditTypeId());
        accountCreditDetail.setCreditType(eCreditType.getId());
        accountCreditDetail=this.mapAccountDtoToDb(accountCreditDetail,accountCreditDetailDto);
        accountCreditDetail= accountCreditDetailRepository.save(accountCreditDetail);
        return accountCreditDetail;
    }

    @Transactional
    @Override
    public String editAccountCreditDetail(AccountCreditDetailDto accountCreditDetailDto) {
        AccountCreditDetail accountCreditDetail=this.getAccountCreditDetailInfo(accountCreditDetailDto.getId());
        accountCreditDetail =this.mapAccountDtoToDb(accountCreditDetail,accountCreditDetailDto);
        accountCreditDetail= accountCreditDetailRepository.save(accountCreditDetail);
        return Utils.getMessageResource("global.operation.success_info");
    }

    private  AccountCreditDetail mapAccountDtoToDb( AccountCreditDetail accountCreditDetail,AccountCreditDetailDto accountCreditDetailDto){
        ECreditType eCreditType=ECreditType.valueOf(accountCreditDetail.getCreditType());

        if(accountCreditDetail.getId()==null) {
            if (accountCreditDetailRepository.countAccountCreditDetailByTitle(accountCreditDetailDto.getTitle(),accountCreditDetail.getAccount().getId()) > 0)
                throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.title_exist");
        }else {
            if (accountCreditDetailRepository.countAccountCreditDetailByTitle(accountCreditDetail.getId(),accountCreditDetailDto.getTitle(),accountCreditDetail.getAccount().getId()) > 0)
                throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.title_exist");
        }

        accountCreditDetail.setViewType(ECreditViewType.valueOf(accountCreditDetailDto.getViewType()).getId());

        accountCreditDetail.setTitle(accountCreditDetailDto.getTitle());

        if(accountCreditDetail.getId()!=null) {
            if (accountCreditDetailDto.getCreditAmountPerUser() > accountCreditDetail.getCreditAmountPerUser()) {
                Integer countUserAssign =userAccountPolicyCreditDetailRepository.countUserAccountCredit(accountCreditDetail.getId());
                if(countUserAssign>0)
                   this.assignCreditToAccount(accountCreditDetail, countUserAssign*(accountCreditDetailDto.getCreditAmountPerUser()-accountCreditDetail.getCreditAmountPerUser()));
            } else if (accountCreditDetailDto.getCreditAmountPerUser() < accountCreditDetail.getCreditAmountPerUser()) {
                //check not used
                Integer countUserAssign =userAccountPolicyCreditDetailRepository.countUserAccountCredit(accountCreditDetail.getId());
                if(countUserAssign>0) {
                    Double amountPerUser = accountCreditDetail.getCreditAmountPerUser() - accountCreditDetailDto.getCreditAmountPerUser();
                    Double maxAmountUsed=userAccountPolicyCreditDetailRepository.maxUserAccountCreditUsed(accountCreditDetail.getId());
                    if (maxAmountUsed>amountPerUser)
                        throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.creditAmountPerUser.usedHint",maxAmountUsed);
                    this.releaseCreditFromAccount(accountCreditDetail,countUserAssign*amountPerUser);
                }
            }
            if(accountCreditDetailDto.getCreditAmount()<accountCreditDetail.getCreditAssign())
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.creditAmount.OldAssignHint");
        }
        accountCreditDetail.setCreditAmount(accountCreditDetailDto.getCreditAmount());
        accountCreditDetail.setCreditAmountPerUser(accountCreditDetailDto.getCreditAmountPerUser());
        accountCreditDetail.setMinCreditAmount(accountCreditDetailDto.getMinCreditAmount());

        AccountPolicyProfile accountPolicyProfile= accountingService.getAccountPolicyProfileInfo(accountCreditDetailDto.getDefaultAccountPolicyProfileId(),true);
        if (!accountPolicyProfile.getAccountType().getId().equals(accountCreditDetail.getAccount().getAccountType().getId()))
            throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.profilePolicy.invalid");

        if(accountCreditDetail.getId()!=null &&  !accountCreditDetail.getDefaultAccountPolicyProfile().equals(accountPolicyProfile)
           && userAccountPolicyCreditDetailRepository.countByAccountCreditDetailId(accountCreditDetail.getId())>0)
            throw new InvalidDataException("Invalid Data", "common.accountPolicyProfile.profilePolicy.changeInvalid");
        accountCreditDetail.setDefaultAccountPolicyProfile(accountPolicyProfile);
        accountCreditDetail.setDescription(accountCreditDetailDto.getDescription());
        accountCreditDetail.setInterestRate(accountCreditDetailDto.getInterestRate());
        accountCreditDetail.setForfeitRate(accountCreditDetailDto.getForfeitRate());



        accountCreditDetail.setSpendingRestrictions(accountCreditDetailDto.getSpendingRestrictions());
        if(accountCreditDetailDto.getSpendingRestrictions()){
            accountCreditDetail.setRateRestrictions(accountCreditDetailDto.getRateRestrictions());
            accountCreditDetail.setMaxAmountRestrictions(accountCreditDetailDto.getMaxAmountRestrictions());
        }else{
            accountCreditDetail.setRateRestrictions(null);
            accountCreditDetail.setMaxAmountRestrictions(null);
        }


        if(ECreditType.REVOLVING_POSTPAID== eCreditType) {
            if(accountCreditDetailDto.getSettlementPeriod()==null || accountCreditDetailDto.getSettlementPeriod()<=0)
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.settlementPeriod_required");
            if(accountCreditDetailDto.getSettlementPeriodType()==null || (accountCreditDetailDto.getSettlementPeriod()<1 && accountCreditDetailDto.getSettlementPeriod()>3))
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.settlementPeriodType_invalid");

            accountCreditDetail.setSettlementPeriod(accountCreditDetailDto.getSettlementPeriod());
            accountCreditDetail.setSettlementPeriodType(accountCreditDetailDto.getSettlementPeriodType());
        }else{
            accountCreditDetail.setSettlementPeriod(null);
            accountCreditDetail.setSettlementPeriodType(null);
        }

        if(Utils.isStringSafeEmpty(accountCreditDetailDto.getExpireDate())){
            /*if(ECreditType.PREPAID== eCreditType)
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.expireDate_required");*/
            if(ECreditType.POSTPAID== eCreditType)
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.dueDate_required");
        }
        if(ECreditType.REVOLVING_POSTPAID!=eCreditType && !Utils.isStringSafeEmpty(accountCreditDetailDto.getExpireDate())) {
            accountCreditDetail.setExpireDate(calendarService.getDateTimeAt24(calendarService.getDateFromString(accountCreditDetailDto.getExpireDate())));
            /*if (accountCreditDetail.getExpireDate().getTime() < (new Date()).getTime())
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.expireDate_current_invalid");*/
        }else{
            accountCreditDetail.setExpireDate(null);
        }
        accountCreditDetail.setIssuer(accountCreditDetailDto.getIssuer());
        return accountCreditDetail;
    }


    @Transactional
    @Override
    public Boolean changeStateAccountCreditDetail(Long accountCreditDetailId) {
        AccountCreditDetail accountCreditDetail = this.getAccountCreditDetailInfo(accountCreditDetailId);
        if (accountCreditDetail.getActive()) {
            accountCreditDetail.setActive(false);
        } else {
            accountCreditDetail.setActive(true);
        }
        accountCreditDetail = accountCreditDetailRepository.save(accountCreditDetail);

        return accountCreditDetail.getActive();
    }
    //#endregion


    //#region Credit assign


    @Override
    public List<UserAccountPolicyCreditDetailWrapper> getUserAccountPolicyCreditDetailWrappers(Long accountCreditDetailId) {
        if (accountCreditDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.id_required");
        if (hasRoleType(ERoleType.ADMIN))
            return userAccountPolicyCreditDetailRepository.findUserAccountPolicyCreditDetailWrappersByAccountCreditDetailId(accountCreditDetailId);
        else
            return userAccountPolicyCreditDetailRepository.findUserAccountPolicyCreditDetailWrappersByAccountCreditDetailIdAndUserId(accountCreditDetailId, Utils.getCurrentUserId());
    }


    @Override
    public UserAccountPolicyCreditDetailWrapper getUserAccountPolicyCreditDetailWrapperInfo(Long userAccountPolicyCreditDetailId) {
        return this.getUserAccountPolicyCreditDetailWrapperInfo(userAccountPolicyCreditDetailId,true);
    }

    @Override
    public UserAccountPolicyCreditDetailWrapper getUserAccountPolicyCreditDetailWrapperInfo(Long userAccountPolicyCreditDetailId,Boolean ownerCheck) {
        if( userAccountPolicyCreditDetailId==null)
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.id_required");
        UserAccountPolicyCreditDetailWrapper userAccountPolicyCreditDetailWrapper=null;
        if (hasRoleType(ERoleType.ADMIN) || !ownerCheck)
            userAccountPolicyCreditDetailWrapper = userAccountPolicyCreditDetailRepository.findUserAccountPolicyCreditDetailWrappersById(userAccountPolicyCreditDetailId);
        else
            userAccountPolicyCreditDetailWrapper = userAccountPolicyCreditDetailRepository.findUserAccountPolicyCreditDetailWrappersByIdAndUserId(userAccountPolicyCreditDetailId,Utils.getCurrentUserId());
        if(userAccountPolicyCreditDetailWrapper==null)
            throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.id_notFound");
        return userAccountPolicyCreditDetailWrapper;
    }

    @Override
    public UserAccountPolicyCreditDetail getUserAccountPolicyCreditDetailInfo(Long userAccountPolicyCreditDetailId) {
        return this.getUserAccountPolicyCreditDetailInfo(userAccountPolicyCreditDetailId,true);
    }

    @Override
    public UserAccountPolicyCreditDetail getUserAccountPolicyCreditDetailInfo(Long userAccountPolicyCreditDetailId,Boolean ownerCheck) {
        if (userAccountPolicyCreditDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.id_required");

        Optional<UserAccountPolicyCreditDetail> result;
        if (hasRoleType(ERoleType.ADMIN) || !ownerCheck)
            result = userAccountPolicyCreditDetailRepository.findByEntityId(userAccountPolicyCreditDetailId);
        else
            result = userAccountPolicyCreditDetailRepository.findByEntityId(userAccountPolicyCreditDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.id_notFound");
        return result.get();


    }

    private UserAccountPolicyCreditDetail getUserAccountPolicyCreditDetailInfo(Long accountCreditDetailId, Long userId) {
        if (accountCreditDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.id_required");
        if (userId == null)
            throw new InvalidDataException("Invalid Data", "common.user.id_required");

        //Optional<UserAccountPolicyCreditDetail> result;
        //if (hasRoleType(ERoleType.ADMIN))
        Optional<UserAccountPolicyCreditDetail> result = userAccountPolicyCreditDetailRepository.findByAccountCreditDetailIdAndUserId(accountCreditDetailId,userId);
        /*else
            result = userAccountPolicyCreditDetailRepository.findByAccountCreditDetailIdAndUserId(accountCreditDetailId,userId);*/

        if (!result.isPresent())
            return null;
            //throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.id_notFound");
        return result.get();

    }

    @Transactional
    @Override
    public String addUserAccountPolicyCreditDetail(UserAccountPolicyCreditDetailDto userAccountPolicyCreditDetailDto) {
        if(userAccountPolicyCreditDetailDto.getUserId()==null && userAccountPolicyCreditDetailDto.getGroupId()==null)
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.targetId_required");

        AccountCreditDetail accountCreditDetail=this.getAccountCreditDetailInfo(userAccountPolicyCreditDetailDto.getAccountCreditDetailId());
        Integer type=(userAccountPolicyCreditDetailDto.getGroupId()!=null?1:2);
        Long targetId;
        if (type==1){
            targetId=userService.getUserGroupInfo(userAccountPolicyCreditDetailDto.getGroupId()).getId();
            this.assignListToCreditToAccount(targetId,accountCreditDetail);
        }else{
            targetId=userService.getUserInfo(userAccountPolicyCreditDetailDto.getUserId()).getId();
            UserAccountPolicyCreditDetail result=this.createUserAccountPolicyCreditDetail(accountCreditDetail,targetId);
            if(result.getId()==null) {
                userAccountPolicyCreditDetailRepository.save(result);
                this.assignCreditToAccount(result.getAccountCreditDetail(), result.getCreditAmount());
            }
        }
        return Utils.getMessageResource("global.operation.success_info");
    }

    private void assignListToCreditToAccount(Long groupId,AccountCreditDetail accountCreditDetail){
        Integer start=0;
        Integer count=1000;

        ResultListPageable<Long> userIdsPageable=userService.getUserIdsByGroupId(groupId,start,count);
        do{
            List<UserAccountPolicyCreditDetail> saveList=new ArrayList<>();
            Integer saveCount=0;
            Double sumCreditAssign=0d;
            for(Long userId : userIdsPageable.getResult()) {
                UserAccountPolicyCreditDetail result=this.createUserAccountPolicyCreditDetail(accountCreditDetail,userId);
                if(result.getId()==null) {
                    sumCreditAssign=sumCreditAssign+result.getCreditAmount();
                    saveList.add(result);
                    saveCount++;
                }
                if(saveCount==100) {
                    userAccountPolicyCreditDetailRepository.saveAll(saveList);
                    this.assignCreditToAccount(saveList.get(0).getAccountCreditDetail(), sumCreditAssign);
                    saveCount=0;
                    saveList.clear();
                }
            }
            if(saveCount>0) {
                userAccountPolicyCreditDetailRepository.saveAll(saveList);
                this.assignCreditToAccount(saveList.get(0).getAccountCreditDetail(), sumCreditAssign);
                saveCount=0;
                saveList.clear();
            }
            userIdsPageable=userService.getUserIdsByGroupId(groupId,count+1,count);
        }while (userIdsPageable.getHasNext());

    }

    private void assignCreditToAccount(AccountCreditDetail accountCreditDetail ,Double sumCreditAssign){
        if(accountCreditDetail!=null  && accountCreditDetail.getAccount()!=null && sumCreditAssign>0){

            if(accountCreditDetail.getCreditAssign()+sumCreditAssign>accountCreditDetail.getCreditAmount())
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.creditAssign_overFlow");
            accountCreditDetail.setCreditAssign(accountCreditDetail.getCreditAssign()+sumCreditAssign);
            accountCreditDetailRepository.save(accountCreditDetail);

            Account account=accountCreditDetail.getAccount();
            if(account.getCreditAssign()+sumCreditAssign>account.getBalance())
                throw new InvalidDataException("Invalid Data", "common.account.balance_notEnough");
            account.setCreditAssign(account.getCreditAssign()+sumCreditAssign);
            accountRepository.save(account);
        }
    }

    private void releaseCreditFromAccount(AccountCreditDetail accountCreditDetail ,Double sumCreditRelease){
        if(accountCreditDetail!=null  && accountCreditDetail.getAccount()!=null && sumCreditRelease>0){

            if(accountCreditDetail.getCreditAssign()-sumCreditRelease<0)
                throw new InvalidDataException("Invalid Data", "common.accountCreditDetail.creditAssign_negative");

            accountCreditDetail.setCreditAssign(accountCreditDetail.getCreditAssign()-sumCreditRelease);
            accountCreditDetailRepository.save(accountCreditDetail);

            Account account=accountCreditDetail.getAccount();
            if(account.getCreditAssign()-sumCreditRelease<0)
                throw new InvalidDataException("Invalid Data", "common.account.creditAssign_negative");
            account.setCreditAssign(account.getCreditAssign()-sumCreditRelease);
            accountRepository.save(account);
        }
    }


    private UserAccountPolicyCreditDetail createUserAccountPolicyCreditDetail(AccountCreditDetail accountCreditDetail,Long userId){
        UserAccountPolicyCreditDetail userAccountPolicyCreditDetail=this.getUserAccountPolicyCreditDetailInfo(accountCreditDetail.getId(),userId);
        if(userAccountPolicyCreditDetail!=null)
            return userAccountPolicyCreditDetail;

        userAccountPolicyCreditDetail=new UserAccountPolicyCreditDetail();
        userAccountPolicyCreditDetail.setAccountCreditDetail(accountCreditDetail);
        userAccountPolicyCreditDetail.setActive(true);
        userAccountPolicyCreditDetail=this.addDefaultUserAccountPolicyProfile(userAccountPolicyCreditDetail,accountCreditDetail,userId);
        return userAccountPolicyCreditDetail;
    }

    private UserAccountPolicyCreditDetail addDefaultUserAccountPolicyProfile(UserAccountPolicyCreditDetail userAccountPolicyCreditDetail,AccountCreditDetail accountCreditDetail,Long userId){
        //UserAccountPolicyProfile userAccountPolicyProfile =accountingService.getUserAccountPolicyProfileInfo(accountCreditDetail.getAccount().getId(),accountCreditDetail.getDefaultAccountPolicyProfile().getId(),userId);
        UserAccountPolicyProfile userAccountPolicyProfile =accountingService.getUserAccountPolicyProfileInfo(accountCreditDetail.getAccount().getId(),userId);
        if(userAccountPolicyProfile==null) {
            userAccountPolicyProfile = new UserAccountPolicyProfile();
            userAccountPolicyProfile.setAccount(accountCreditDetail.getAccount());
            userAccountPolicyProfile.setUserId(userId);
            userAccountPolicyProfile.setAccountPolicyProfile(accountCreditDetail.getDefaultAccountPolicyProfile());
            userAccountPolicyProfile.getUserAccountPolicyCreditDetails().add(userAccountPolicyCreditDetail);
            userAccountPolicyProfile=userAccountPolicyProfileRepository.save(userAccountPolicyProfile);
        }
        userAccountPolicyCreditDetail.setUserAccountPolicyProfile(userAccountPolicyProfile);
        return userAccountPolicyCreditDetail;
    }

    @Transactional
    @Override
    public String deleteUserAccountPolicyCreditDetail(Long accountCreditDetailId,Long userId){
        AccountCreditDetail accountCreditDetail=this.getAccountCreditDetailInfo(accountCreditDetailId);
        UserAccountPolicyCreditDetail userAccountPolicyCreditDetail=this.getUserAccountPolicyCreditDetailInfo(accountCreditDetail.getId(),userId);
        if(userAccountPolicyCreditDetail==null)
           throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.id_notFound");
        if(userAccountPolicyCreditDetail.getUsedAmount()>0)
            throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.usedAmount_deleteHint");

        if(userAccountPolicyCreditDetail.getBlock()>0)
            throw new ResourceNotFoundException("Invalid Data", "common.userAccountPolicyCreditDetail.block_deleteHint");
        UserAccountPolicyProfile forDeleteUserAccountPolicyProfile=null;
        if(userAccountPolicyCreditDetail.getUserAccountPolicyProfile().getUserAccountPolicyCreditDetails().size()==1)
            forDeleteUserAccountPolicyProfile=userAccountPolicyCreditDetail.getUserAccountPolicyProfile();

        this.releaseCreditFromAccount(userAccountPolicyCreditDetail.getAccountCreditDetail(), userAccountPolicyCreditDetail.getCreditAmount());
        userAccountPolicyCreditDetailRepository.delete(userAccountPolicyCreditDetail);
        if(forDeleteUserAccountPolicyProfile!=null)
           userAccountPolicyProfileRepository.delete(forDeleteUserAccountPolicyProfile);

        return Utils.getMessageResource("global.delete_info");
    }

    @Transactional
    @Override
    public UserAccountPolicyCreditDetail withdrawUserAccountCredit(Long userAccountPolicyCreditDetailId, Double amount, Boolean ownerCheck) {
        UserAccountPolicyCreditDetail userCredit= this.getUserAccountPolicyCreditDetailInfo(userAccountPolicyCreditDetailId,ownerCheck);
        if(!userCredit.getActive() || !userCredit.getAccountCreditDetail().getActive())
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.unActive",userCredit.getAccountCreditDetail().getTitle());
        if(userCredit.getWithdrawBalance()<amount)
            throw new InvalidDataException("Invalid Data", "common.account.balance_invalid");
        userCredit.setUsedAmount(userCredit.getUsedAmount()+amount);
        userCredit=userAccountPolicyCreditDetailRepository.save(userCredit);
        return userCredit;
    }


    //#endregion

}
