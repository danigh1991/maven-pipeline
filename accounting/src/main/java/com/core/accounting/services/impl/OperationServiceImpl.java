package com.core.accounting.services.impl;

import com.core.accounting.model.contextmodel.*;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.enums.*;
import com.core.accounting.model.wrapper.*;
import com.core.accounting.repository.*;
import com.core.accounting.services.*;
import com.core.accounting.services.factory.PaymentServiceFactory;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.services.CommonService;
import com.core.common.services.RetryableService;
import com.core.common.services.UserService;
import com.core.common.services.impl.AbstractService;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralBankObject;
import com.core.datamodel.model.dbmodel.User;
import com.core.datamodel.model.enums.EBank;
import com.core.datamodel.model.enums.ENotifyTargetType;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.model.enums.ETargetTypes;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.model.wrapper.QRCodeDataWrapper;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;
import com.core.services.CalendarService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.util.BaseUtils;
import com.core.util.search.SearchCriteria;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service("operationServiceImpl")
public class OperationServiceImpl extends AbstractService implements OperationService {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private OperationRequestRepository operationRequestRepository;
    @Autowired
    private PaymentUtilsService paymentUtilsService;
    @Autowired
    private PaymentServiceFactory paymentServiceFactory;
    @Autowired
    private RetryableService retryableService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private DepositRequestRepository depositRequestRepository;
    @Autowired
    private DepositRequestDetailRepository depositRequestDetailRepository;
    @Autowired
    private CostSharingService costSharingService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private DynamicQueryHelper dynamicQueryHelper;
    @Autowired
    private MerchantService merchantService;


    @Value("#{${operationRequest.search.native.private.params}}")
    private HashMap<String, List<String>> OPERATION_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;


    private static Map<Integer,EOperation> LOCAL_OPERATIONS;
    static {
        LOCAL_OPERATIONS = new HashMap<>();
        LOCAL_OPERATIONS.put(1010,EOperation.valueOfCode(1010));
        LOCAL_OPERATIONS.put(1020,EOperation.valueOfCode(1020));
        LOCAL_OPERATIONS.put(1030,EOperation.valueOfCode(1030));
        LOCAL_OPERATIONS.put(1031,EOperation.valueOfCode(1031));
        LOCAL_OPERATIONS.put(1032,EOperation.valueOfCode(1032));
        LOCAL_OPERATIONS.put(1033,EOperation.valueOfCode(1033));
        LOCAL_OPERATIONS.put(1034,EOperation.valueOfCode(1034));
    }


    @Override
    public OperationType getOperationTypeInfo(Long operationTypeId) {
        if (operationTypeId == null)
            throw new ResourceNotFoundException("Invalid Data", "common.costShareType.id_required");

        Optional<OperationType> result = operationTypeRepository.findByEntityId(operationTypeId);
        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.costShareType.id_notFound");
        return result.get();
    }

    @Override
    public OperationType getOperationTypeInfoByCode(Integer code) {
        if (code == null)
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.code_required");

        Optional<OperationType> result = operationTypeRepository.findByCode(code);
        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.code_invalid", code);
        return result.get();

    }

    @Override
    public OperationTypeWrapper getActiveOperationTypeWrappersByName(String operationTypeName) {
        if (Utils.isStringSafeEmpty(operationTypeName))
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.name_required");

        Optional<OperationTypeWrapper> result = operationTypeRepository.findActiveOperationTypeWrappersByName(operationTypeName);
        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.name_notFound", operationTypeName);
        return result.get();
    }

    @Override
    public OperationTypeWrapper getActiveOperationTypeWrappersByCode(Integer code) {
        if (code == null)
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.code_required");

        Optional<OperationTypeWrapper> result = operationTypeRepository.findActiveOperationTypeWrappersByCode(code);
        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.code_invalid", code);
        return result.get();
    }

    @Override
    public OperationType editOperationType(OperationTypeDto operationTypeDto) {
        OperationType operationTypeType = this.getOperationTypeInfo(operationTypeDto.getId());

        //#region Validate
        if (
                operationTypeDto.getMinAmount() != null && operationTypeDto.getMaxAmount() != null && operationTypeDto.getMinAmount() > operationTypeDto.getMaxAmount() ||
                operationTypeDto.getMinAmount() != null && operationTypeDto.getGlobalMaxDailyAmount() != null && operationTypeDto.getMinAmount() > operationTypeDto.getGlobalMaxDailyAmount() ||
                operationTypeDto.getMaxAmount() != null && operationTypeDto.getGlobalMaxDailyAmount() != null && operationTypeDto.getMaxAmount() > operationTypeDto.getGlobalMaxDailyAmount()
        ) {
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.limits_validate_error_hint");
        }

        Double minDefaultAmounts = operationTypeDto.getDefaultAmounts().stream().min(Comparator.comparing(Double::valueOf)).orElse((double) -1);
        Double maxDefaultAmounts = operationTypeDto.getDefaultAmounts().stream().max(Comparator.comparing(Double::valueOf)).orElse((double) -1);
        if (
                operationTypeDto.getMinAmount() != null && minDefaultAmounts != -1 && operationTypeDto.getMinAmount() > minDefaultAmounts ||
                operationTypeDto.getMaxAmount() != null && maxDefaultAmounts != -1 && operationTypeDto.getMaxAmount() < maxDefaultAmounts ||
                operationTypeDto.getGlobalMaxDailyAmount() != null && maxDefaultAmounts != -1 && operationTypeDto.getGlobalMaxDailyAmount() < maxDefaultAmounts
        ) {
            throw new ResourceNotFoundException("Invalid Data", "common.operationType.limits_validate_default_amount_confilict_error_hint");
        }
        //#endregion

        operationTypeType.setName(operationTypeDto.getName());
        operationTypeType.setDescription(operationTypeDto.getDescription());
        operationTypeType.setActive(operationTypeDto.getActive());
        operationTypeType.setMinAmount(operationTypeDto.getMinAmount());
        operationTypeType.setMaxAmount(operationTypeDto.getMaxAmount());
        /*operationTypeType.setMaxAmountDurationType(operationTypeDto.getMaxAmountDurationType());
        operationTypeType.setMaxAmountDuration(operationTypeDto.getMaxAmountDuration());*/
        operationTypeType.setGlobalMaxDailyAmount(operationTypeDto.getGlobalMaxDailyAmount());

        EWageType eWageType=EWageType.valueOf(operationTypeDto.getWageType());
        operationTypeType.setWageType(eWageType.getId());
        operationTypeType.setWageRate(operationTypeDto.getWageRate());
        operationTypeType.setWageAmount(operationTypeDto.getWageAmount());
        operationTypeType.setOrder(operationTypeDto.getOrder());
        operationTypeType.setNotify(operationTypeDto.getNotify());

        if (operationTypeDto.getDefaultAmounts() != null && operationTypeDto.getDefaultAmounts().size() > 0)
            operationTypeType.setDefaultAmounts(operationTypeDto.getDefaultAmounts().stream().map(s -> s.toString()).collect(Collectors.joining(",")));
        else
            operationTypeType.setDefaultAmounts(null);

        operationTypeType = operationTypeRepository.save(operationTypeType);
        return operationTypeType;
    }

    @Override
    public List<OperationTypeWrapper> getAllOperationTypeWrappers() {
        return operationTypeRepository.findAllOperationTypeWrappers();
    }

    @Override
    public List<OperationTypeWrapper> getAllAccountOperationTypeWrappers() {
        return operationTypeRepository.findAllOperationTypeWrappersBySourceTypeIds(Arrays.asList(0,1));
    }

    @Override
    public Boolean getOperationTypeSendNotifyStatusByCode(Integer code) {
        Boolean result=operationTypeRepository.findOperationTypeSendNotifyStatusByCode(code);
        return result==null? false : result;
    }
/*@Override
    public Boolean getOperationTypeSendNotifyStatusForAccountByCode(Long accountId, Integer code) {
        return null;
    }*/

    @Transactional
    @Override
    public BankPaymentResponse onlineCharge(BankPaymentResponse bankPaymentResponse) {
        try {
            if (bankPaymentResponse.getVerified()) {
                //todo check Policy to Charge Account
                Transaction chargeTransaction = accountingService.depositAccount(bankPaymentResponse.getAccountId(), bankPaymentResponse.getAmount(), EOperation.CHARGE.getOperationCode(), null, bankPaymentResponse.getOrderReferenceNumber(), "transaction.charge.onLine_desc", null, bankPaymentResponse.getOrderId(), bankPaymentResponse.getOperationRequestId(),bankPaymentResponse.getUserId());
                bankPaymentResponse.setTransactionId(chargeTransaction.getId());
                paymentUtilsService.updateBankResponseInCurrentTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), bankPaymentResponse.getTransactionId(), null, null, null, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, null, null, null, null, "Error In Commit chargeTransaction After  bpVerifyRequest :" + e.getMessage(), null);
            throw e;
        }
        return bankPaymentResponse;
    }


    @Override
    public EOperation getOperationTypeByOperationRequestId(Long operationRequestId) {
        if (operationRequestId == null )
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");
        Integer operationTypeId=operationRequestRepository.findOperationTypeId(operationRequestId);
        if (operationTypeId==null)
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return EOperation.valueOf(operationTypeId);
    }

    @Override
    public OperationRequest getOperationRequestInfo(Long operationRequestId, String referenceNumber) {
        return this.getOperationRequestInfo(operationRequestId, referenceNumber, true);
    }

    @Override
    public OperationRequest getOperationRequestInfo(Long operationRequestId, String referenceNumber, Boolean forceOwner) {
        if (operationRequestId == null || Utils.isStringSafeEmpty(referenceNumber))
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        Optional<OperationRequest> result;
        if (hasRoleType(ERoleType.ADMIN) || !forceOwner)
            result = operationRequestRepository.findByIdAndReferenceNumber(operationRequestId, referenceNumber);
        else
            result = operationRequestRepository.findByIdAndReferenceNumberAndUserId(operationRequestId, referenceNumber, BaseUtils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();


    }

    @Override
    public OperationRequest getOperationRequestInfo(String referenceNumber) {
        return this.getOperationRequestInfo(referenceNumber, true);
    }

    @Override
    public OperationRequest getOperationRequestInfo(String referenceNumber, Boolean forceOwner) {
        if (Utils.isStringSafeEmpty(referenceNumber))
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        Optional<OperationRequest> result;
        if (hasRoleType(ERoleType.ADMIN) || !forceOwner)
            result = operationRequestRepository.findByReferenceNumber(referenceNumber);
        else
            result = operationRequestRepository.findByReferenceNumberAndUserId(referenceNumber, BaseUtils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();


    }


    @Override
    public OperationRequest getOperationRequestInfo(Long operationRequestId) {
        return this.getOperationRequestInfo(operationRequestId, true);
    }

    @Override
    public OperationRequest getOperationRequestInfo(Long operationRequestId, Boolean forceOwner) {
        if (operationRequestId == null)
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        Optional<OperationRequest> result;
        if (hasRoleType(ERoleType.ADMIN) || !forceOwner)
            result = operationRequestRepository.findByEntityId(operationRequestId);
        else
            result = operationRequestRepository.findByEntityIdAndUserId(operationRequestId, BaseUtils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public OperationRequestWrapper getOperationRequestWrapperInfo(Long operationRequestId) {
        if (operationRequestId == null)
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        Optional<OperationRequestWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = operationRequestRepository.findOperationRequestWrapperById(operationRequestId);
        else
            result = operationRequestRepository.findOperationRequestWrapperByIdAndUserId(operationRequestId, BaseUtils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public OperationRequestWrapper getOperationRequestWrapperInfo(String referenceNumber) {
        if (Utils.isStringSafeEmpty(referenceNumber))
            throw new ResourceNotFoundException("Invalid Data", "common.operationRequest.referenceNumber_required");

        Optional<OperationRequestWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = operationRequestRepository.findOperationRequestWrapperByReferenceNumber(referenceNumber);
        else
            result = operationRequestRepository.findOperationRequestWrapperByReferenceNumberAndUserId(referenceNumber, BaseUtils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.operationRequest.referenceNumber_notFound");
        return result.get();
    }

    /*@Override
    public List<OperationRequestWrapper> getMyOperationRequestWrappers(Map<String,Object> requestParams) {
        return this.getOperationRequestWrappers(BaseUtils.getCurrentUserId());
    }*/

/*
    @Override
    public List<OperationRequestWrapper> getOperationRequestWrappers1(Map<String, Object> requestParams) {

        Long userId = Utils.getAsLongFromMap(requestParams, "userId", false, Utils.getCurrentUserId());
        Integer start = Utils.getAsIntegerFromMap(requestParams, "start", false);
        Integer count = Utils.getAsIntegerFromMap(requestParams, "count", false);

        if (!hasRoleType(ERoleType.ADMIN))
            userId = Utils.getCurrentUserId();

        return operationRequestRepository.findOperationRequestWrapperByUserId(userId, Utils.gotoPage(start, count));
    }
*/

    @Override
    public ResultListPageable<OperationRequestWrapper> getOperationRequestWrappers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =this.dynamicQueryHelper.getSortParams(OPERATION_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "operationRequestWrapperMapping");
        ResultListPageable<OperationRequestWrapper> operationRequestWrapperResultListPageable=this.getOperationRequestGeneral(requestParams);
        return operationRequestWrapperResultListPageable;
    }

    @Override
    public List<TypeWrapper> getOperationRequestStatuses() {
        return EOperationRequestStatus.getAllAsObjectWrapper();
    }



    //#region query generator
    private String getBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return  "select o.opr_id as id, o.opr_title as title, o.opr_usr_id as userId, o.opr_opt_id as operationTypeId, o.opr_source_type as sourceType,\n" +
                "       o.opr_amount as amount, o.opr_wage as wage, \n" +
                "       o.opr_status as status, o.opr_card_number as cardNumber, " + (loadDetails ? "o.opr_details" : "''") + " as details,\n" +
                "       o.opr_reference_number as referenceNumber, o.opr_description  as description, opr_self as self, \n" +
                "       o.opr_create_date as createDate,  o.opr_create_by as createBy, \n" +
                "       o.opr_modify_date as modifyDate,o.opr_modify_by as modifyBy       \n";

    }

    private String getBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getBaseQueryBody(Map<String, Object> requestParams) {
        String queryString="from sc_operation_request o \n";
        return  queryString;
    }

    private ResultListPageable<OperationRequestWrapper> getOperationRequestGeneral(Map<String, Object> requestParams) {
        String trackingCode = Utils.getAsStringFromMap(requestParams, "trackingCode", false,null);
        if(!Utils.isStringSafeEmpty(trackingCode)) {
            requestParams.put("id", Utils.getIdentityFromTrackingCode(trackingCode));
        }



        String queryString = this.getBaseQueryHead(requestParams)+
                Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getBaseCountQueryHead(requestParams)+
                    Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        return dynamicQueryHelper.getDataGeneral(requestParams,OPERATION_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,OperationRequestWrapper.class);

    }
    //#endregion query generator

    @Override
    public QRCodeDataWrapper getUserQRCodeData(String qrCodeText) {
        QRCodeDataWrapper qrCodeDataWrapper=userService.getQRCodeData(qrCodeText);
        User user;
        try {
            user = userService.getUserInfo(qrCodeDataWrapper.getUserName());
        } catch (ResourceNotFoundException ex) {
            throw new InvalidDataException("Invalid data", "common.user.username_invalidHint");
        }
        qrCodeDataWrapper.setMerchant(merchantService.isActiveMerchant(user.getId()));
        return qrCodeDataWrapper;
    }

    @Override
    public OperationTypeWrapper checkAllowOperationTo(Integer operationTypeCode, String userName, Boolean systemic) {
        EOperation eOperation = EOperation.valueOfCode(operationTypeCode);
        User user;
        try {
            user = userService.getUserInfo(userName);
        } catch (ResourceNotFoundException ex) {
            throw new InvalidDataException(userName, "common.user.username_invalidHint", userName);
        }
        Account mainAccount = accountingService.getMainPersonalAccountInfoByUserId(user.getId());
        Boolean isMerchant=merchantService.isActiveMerchant(mainAccount.getUserId());
        Integer merchantTransferOperationTypeCode=null;
        if (isMerchant ) {
            merchantTransferOperationTypeCode=merchantService.getMerchantTransferOperationTypeCode(mainAccount.getUserId());
            if(eOperation == EOperation.TRANSFER)
               eOperation = EOperation.valueOfCode(merchantTransferOperationTypeCode);
        }
        this.checkAllowOperationTo(eOperation, mainAccount, systemic,isMerchant,merchantTransferOperationTypeCode);
        return this.getActiveOperationTypeWrappersByCode(eOperation.getOperationCode());
    }

    @Override
    public Boolean checkAllowOperationTo(Integer operationTypeCode, Long accountId, Boolean systemic) {
        if (accountId == null)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.account_required");

        EOperation eOperation = EOperation.valueOfCode(operationTypeCode);
        Account account = accountingService.getAccountInfoById(accountId, false);
        Boolean isMerchant=merchantService.isActiveMerchant(account.getUserId());
        Integer merchantTransferOperationTypeCode=null;
        if (isMerchant /*&& eOperation == EOperation.TRANSFER*/)
            merchantTransferOperationTypeCode=merchantService.getMerchantTransferOperationTypeCode(account.getUserId());

        return this.checkAllowOperationTo(eOperation, account, systemic,isMerchant,merchantTransferOperationTypeCode);
    }

    private Boolean checkAllowOperationTo(EOperation eOperation, Account account, Boolean systemic,Boolean isMerchant,Integer merchantTransferOperationTypeCode) {
        if (!systemic && eOperation == EOperation.CHARGE && !Utils.getCurrentUserId().equals(account.getUserId()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.chargeOtherAccount_notAllow");

        if (eOperation == EOperation.PURCHASE || eOperation == EOperation.TRANSPORTATION_TRANSFER || eOperation == EOperation.CHARITY_TRANSFER) {
           if(!isMerchant)
              throw new InvalidDataException("Invalid Data", "common.operationRequest.purchase.merchant_invalid");
           else if(eOperation!= EOperation.valueOfCode(merchantTransferOperationTypeCode))
              throw new InvalidDataException("Invalid Data", "common.operationRequest.merchant_operationType_invalid",EOperation.valueOfCode(merchantTransferOperationTypeCode).getCaption());
        }

        if (!systemic && (eOperation == EOperation.TRANSFER || eOperation == EOperation.GROUP_TRANSFER || eOperation == EOperation.PURCHASE ||
                          eOperation == EOperation.TRANSPORTATION_TRANSFER || eOperation == EOperation.CHARITY_TRANSFER) && Utils.getCurrentUserId().equals(account.getUserId()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.transferMySelf_notAllow");

        if (eOperation == EOperation.TRANSFER && isMerchant)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.transfer.merchant_invalid");

        //userService.hasExistRole(account.getUserId(), "merchant")
        if (eOperation == EOperation.GROUP_TRANSFER && isMerchant)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.group_transfer.merchant_invalid",userService.getUserInfo(account.getUserId()).getUsername());

        return true;
    }

    @Override
    public Map<String, Object> getAvailableAccountWrappersByOperationTypeCode(OperationRequestTargetDto operationRequestTargetDto) {
        return this.getAvailableAccountWrappersByOperationTypeCode(operationRequestTargetDto, Utils.getCurrentUserId());
    }

    @Override
    public Map<String, Object> getAvailableAccountWrappersByOperationTypeCode(OperationRequestTargetDto operationRequestTargetDto, Long userId) {
        EOperation eOperation = EOperation.valueOfCode(operationRequestTargetDto.getOperationTypeCode());

        if(operationRequestTargetDto.getToAccounts()!=null && operationRequestTargetDto.getToAccounts().size()==1){
            Long toAccountUserId=operationRequestTargetDto.getToAccounts().get(0).getUserId();
            if (toAccountUserId==null && !Utils.isStringSafeEmpty(operationRequestTargetDto.getToAccounts().get(0).getUserName()))
                toAccountUserId=userService.getUserInfo(operationRequestTargetDto.getToAccounts().get(0).getUserName()).getId();
            else if(toAccountUserId==null)
                toAccountUserId=accountingService.getAccountUserIdById(operationRequestTargetDto.getToAccounts().get(0).getAccountId());
            Boolean isMerchant=merchantService.isActiveMerchant(toAccountUserId);
            Integer merchantTransferOperationTypeCode=null;
            if (isMerchant ) {
                merchantTransferOperationTypeCode=merchantService.getMerchantTransferOperationTypeCode(toAccountUserId);
                if(eOperation == EOperation.TRANSFER)
                    eOperation = EOperation.valueOfCode(merchantTransferOperationTypeCode);
            }
        }

        OperationTypeWrapper targetOperationTypeWrapper = this.getActiveOperationTypeWrappersByCode(eOperation.getOperationCode());
        this.validateToAccounts(eOperation, operationRequestTargetDto.getToAccounts(), userId, false);

        Map<String, Object> result = new HashMap<>();
        if (operationRequestTargetDto.getToAccounts() == null || operationRequestTargetDto.getToAccounts().size() == 0)
            return result;


        result.put("targetOperationTypeCode", eOperation.getOperationCode());

        Double sumToAccountsAmount=BaseUtils.round(((Double)operationRequestTargetDto.getToAccounts().stream().mapToDouble(TargetAccountDto::getAmount).sum()).doubleValue(), ShareUtils.getPanelCurrencyRndNumCount());
        Double wageAmount=this.calculateWage(eOperation.getOperationCode(),sumToAccountsAmount);
        if(wageAmount>0)
            result.put("wageAmount", wageAmount);

        if (operationRequestTargetDto.getToAccounts() != null && operationRequestTargetDto.getToAccounts().size() > 1) {
            AccountWrapper mainAccount = accountingService.getMainAccountWrapperByUsrId(userId);
            List<AccountWrapper> personalUserAccounts = new ArrayList<>();
            personalUserAccounts.add(mainAccount);
            if (personalUserAccounts.size() > 0)
                result.put("personal", personalUserAccounts);

            return result;
        }

        this.validateOrDoneReferenceOperationType(operationRequestTargetDto, null, false);

        Long targetUserId = this.getTargetUserId(operationRequestTargetDto.getToAccounts());
        Double targetAmount=operationRequestTargetDto.getToAccounts().get(0).getAmount();
        List<AccountWrapper> allAvailableAccountWrappers = accountingService.getAvailableAccountWrappersByOperationTypeCode(eOperation.getOperationCode(), BaseUtils.getCurrentUserId(), targetUserId,targetAmount);

        for (TargetAccountDto ta : operationRequestTargetDto.getToAccounts()) {
            allAvailableAccountWrappers = allAvailableAccountWrappers.stream().filter(a -> !ta.getAccountId().equals(a.getId())).collect(Collectors.toList());
        }

        List<AccountWrapper> personalUserAccounts = allAvailableAccountWrappers.stream().filter(a -> a.getAccountTypeId().equals(EAccountType.PERSONAL.getId())).collect(Collectors.toList());
        if (personalUserAccounts.size() > 0)
            result.put("personal", personalUserAccounts);
        List<AccountWrapper> shareUserAccounts = allAvailableAccountWrappers.stream().filter(a -> a.getAccountTypeId().equals(EAccountType.SHAREABLE.getId())).collect(Collectors.toList());
        if (shareUserAccounts.size() > 0)
            result.put("share", shareUserAccounts);


        //.sorted(Comparator.comparing(AccountWrapper::getId))
        List<AccountWrapper> creditUserAccounts = allAvailableAccountWrappers.stream().filter(a -> (a.getAccountTypeId().equals(EAccountType.CREDIT_PERSONAL.getId()) || a.getAccountTypeId().equals(EAccountType.CREDIT_ORGANIZATION.getId()))).collect(Collectors.toList());
        if (creditUserAccounts.size() > 0) {
            List<AccountWrapper> creditResult =creditUserAccounts.stream().filter(a->a.getViewType()==1).collect(Collectors.toList());
            Map<Long, List<AccountWrapper>> creditUserAccountGroupByAccountId = creditUserAccounts.stream().filter(a->a.getViewType()==2).collect(Collectors.groupingBy(AccountWrapper::getId));
            for (Long key:creditUserAccountGroupByAccountId.keySet()) {
                List<AccountWrapper> tmpList= creditUserAccountGroupByAccountId.get(key);
                AccountWrapper tmp=tmpList.get(0);
                if(tmpList.size()>1)
                    tmp.setInnerAccount((List<AccountWrapper>) tmpList.subList(1,tmpList.size()));
                creditResult.add(tmp);
            }

            if (creditResult.size() > 0)
                result.put("credit", creditResult);
        }
        return result;
    }

    private void validateOrDoneReferenceOperationType(OperationRequestTargetDto operationRequestTargetDto, Double amount, Boolean done) {
        if (operationRequestTargetDto.getReferenceOperationTypeCode() != null && operationRequestTargetDto.getReferenceOperationTypeCode() > 0
                && operationRequestTargetDto.getReferenceId() != null && operationRequestTargetDto.getReferenceId() > 0) {
            switch (operationRequestTargetDto.getReferenceOperationTypeCode()) {
                case 1040:
                    if (!operationRequestTargetDto.getOperationTypeCode().equals(EOperation.TRANSFER.getOperationCode()) && !operationRequestTargetDto.getOperationTypeCode().equals(EOperation.PURCHASE.getOperationCode())
                        && !operationRequestTargetDto.getOperationTypeCode().equals(EOperation.TRANSPORTATION_TRANSFER.getOperationCode()) && !operationRequestTargetDto.getOperationTypeCode().equals(EOperation.CHARITY_TRANSFER.getOperationCode()) )
                        throw new InvalidDataException("Invalid Data", "common.operationType.code_invalid");
                    if (done)
                        this.doneDepositRequestDetail(operationRequestTargetDto.getReferenceId(), amount);
                    else
                        this.validatePreDoneDepositRequestDetail(operationRequestTargetDto.getReferenceId(), amount);
                    break;
                case 1050:
                    if (!operationRequestTargetDto.getOperationTypeCode().equals(EOperation.TRANSFER.getOperationCode()) && !operationRequestTargetDto.getOperationTypeCode().equals(EOperation.PURCHASE.getOperationCode())
                            && !operationRequestTargetDto.getOperationTypeCode().equals(EOperation.TRANSPORTATION_TRANSFER.getOperationCode()) && !operationRequestTargetDto.getOperationTypeCode().equals(EOperation.CHARITY_TRANSFER.getOperationCode()) )
                        throw new InvalidDataException("Invalid Data", "common.operationType.code_invalid");
                    if (done)
                        costSharingService.doneCostShareRequestDetail(operationRequestTargetDto.getReferenceId(), amount);
                    else
                        costSharingService.validatePreDoneCostShareRequestDetail(operationRequestTargetDto.getReferenceId(), amount);

                    break;
                default:
                    throw new InvalidDataException("Invalid Data", "common.operationRequest.referenceOperationTypeCode_invalid");
            }
        }
    }

    @Transactional
    @Override
    public OperationRequestResult prepareOperationRequestOnlinePayment(Long operationRequestId, String referenceNumber) {
        OperationRequest operationRequest = this.getOperationRequestInfo(operationRequestId, referenceNumber, false);
        if (operationRequest.getStatus() != 0)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.onlinePay_invalid");
        operationRequest.setReferenceNumber(Utils.createUniqueRandom());
        operationRequestRepository.save(operationRequest);

        OperationRequestResult operationRequestResult = new OperationRequestResult();
        operationRequestResult.setUserId(operationRequest.getUserId());
        operationRequestResult.setReferenceNumber(operationRequest.getReferenceNumber());
        operationRequestResult.setOperationRequestId(operationRequest.getId());

        EOperation eOperation = EOperation.valueOfCode(operationRequest.getOperationType().getCode());
        FinalOperationRequestDto finalOperationRequestDto = operationRequest.getFinalOperationRequestDto();
        if (finalOperationRequestDto.hasOnlinePayment()) {
            //todo read EBank from Config and load balance payment request between
            List<EBank> banks = commonService.getValidActiveBanks();
            EBank eBank = banks.get(0);

            Map<String, Object> additional = new HashMap<>();
            additional.put("accountId", finalOperationRequestDto.getTargetOnlineAccountId());
            additional.put("title", eOperation.getCaption());
            additional.put("operationRequestId", operationRequest.getId());
            //additional.put("returnUrl",/*Utils.getCompleteUrl(*/"http://192.168.0.115:7275/onlinepayment/onlinepaymentresponse?myRefId=" + operationRequestResult.getReferenceNumber()/*, true)*/);
            additional.put("returnUrl",Utils.getCompleteUrl("/onlinepayment/onlinepaymentresponse?myRefId=" + operationRequestResult.getReferenceNumber(), true));
            additional.put("paymentMethod", null);
            additional.put("currency", null);
            additional.put("notifyUrl", Utils.getCompleteUrl("/api/onlinepayment/onlinepaymentnotify?myRefId=" + operationRequestResult.getReferenceNumber() + "&result=1", false));
            operationRequestResult = (OperationRequestResult) paymentUtilsService.createBankPaymentRequest((GeneralBankObject) operationRequestResult, finalOperationRequestDto.getOnlineAmountAndWage(), eBank, operationRequestResult.getReferenceNumber(), additional);
            operationRequestResult.setStatus(true);
            return operationRequestResult;
        } else {
            throw new InvalidDataException("Invalid Data", "common.operationRequest.onlineAmount._invalid");
        }
    }


    @Transactional
    @Override
    public OperationRequestResult operationRequestCall(OperationRequestDto operationRequestDto) {
        if (LOCAL_OPERATIONS.get(operationRequestDto.getOperationTypeCode())==null)
            throw new InvalidDataException("Invalid Data", "common.operationType.code_invalid");
        return this.operationRequestCall(operationRequestDto, Utils.getCurrentUserId(), null);
    }

    @Transactional
    @Override
    public OperationRequestResult operationRequestCall(OperationRequestDto operationRequestDto, Long userId, Map<String, Object> additionalData) {
        switch (operationRequestDto.getOperationTypeCode()) {
            case 1010:
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1020:
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1030:
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1031:
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1032: // Gift Transfer
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1033: // Charity Transfer
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1034: // transportation Transfer
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1060:
                return this.transfer(operationRequestDto, userId, additionalData);
            case 1070:
                return this.transfer(operationRequestDto, userId, additionalData);
            case 4000:
                return this.transfer(operationRequestDto, userId, additionalData);
            default:
                throw new InvalidDataException("Invalid Data", "common.operationType.code_invalid");
        }
    }


    @Transactional
    @Override
    public OperationRequestResult operationRequestCall(String platform, Integer operationTypeCode, String description, Long transactionTypeId, Long fromUserId, Long toUserId, Double amount, Long userId, Map<String, Object> additionalData) {
        OperationRequestDto operationRequestDto = new OperationRequestDto();
        operationRequestDto.setPlatform(platform);
        operationRequestDto.setTransactionTypeId(transactionTypeId);
        operationRequestDto.setOperationTypeCode(operationTypeCode);
        operationRequestDto.setDescription(description);

        operationRequestDto.getFromAccounts().add(TargetAccountDto.builder()
                .userId(fromUserId)
                .accountId(accountingService.getAccountInfoByUserId(fromUserId).getId())
                .amount(amount).build());
        operationRequestDto.getToAccounts().add(TargetAccountDto.builder()
                .userId(toUserId)
                .accountId(accountingService.getAccountInfoByUserId(toUserId).getId())
                .amount(amount).build());
        return this.operationRequestCall(operationRequestDto, userId, additionalData);

    }

    private OperationRequestResult transfer(OperationRequestDto operationRequestDto, Long userId, Map<String, Object> additionalData) {
        EOperation eOperation = EOperation.valueOfCode(operationRequestDto.getOperationTypeCode());
        OperationTypeWrapper targetOperationTypeWrapper = this.getActiveOperationTypeWrappersByCode(operationRequestDto.getOperationTypeCode());


        Boolean systemic = Utils.getAsBooleanFromMap(additionalData, "systemic", false);
        //Boolean negativeAllow=(additionalData!=null && additionalData.get("negativeAllow")!=null) ? (Boolean) additionalData.get("negativeAllow"):false;
        Boolean negativeAllow = (additionalData != null && additionalData.get("negativeAllow") != null) ? (Boolean) additionalData.get("negativeAllow") : false;
        /*if(eOperation==EOperation.GET_MONEY)
            operationRequestDto.setToAccounts(null);*/

        this.validateToAccounts(eOperation, operationRequestDto.getToAccounts(), userId, systemic);
        this.validateFromAccounts(operationRequestDto.getFromAccounts()/*,fromRequired*/, systemic, negativeAllow);
        this.matchingFromToAccounts(operationRequestDto.getFromAccounts(), operationRequestDto.getToAccounts());

        Long targetUserId = this.getTargetUserId(operationRequestDto.getToAccounts()/*,toRequired*/);
        //if(fromRequired)

        if (!systemic) {
            Double amount=operationRequestDto.getToAccounts().get(0).getAmount();
            this.validateOperationTypeFromAccounts(targetOperationTypeWrapper.getCaption(), targetOperationTypeWrapper.getCode(), userId, operationRequestDto.getFromAccounts(), targetUserId,amount);
        }

        FinalOperationRequestDto finalOperationRequestDto = this.prepareFinalOperationRequestDto(operationRequestDto, userId);
        OperationRequest operationRequest = this.createOperation(finalOperationRequestDto, userId, additionalData);

        this.checkLimitation(targetOperationTypeWrapper, userId, operationRequest.getAmount());


        OperationRequestResult operationRequestResult = new OperationRequestResult();
        operationRequestResult.setReferenceNumber(operationRequest.getReferenceNumber());
        operationRequestResult.setOperationRequestId(operationRequest.getId());

        if (finalOperationRequestDto.getSumFromAccountsAmount() > 0 && finalOperationRequestDto.getSumToAccountsAmount() > 0
                //&& eOperation!=EOperation.CHARGE && eOperation!=EOperation.GET_MONEY
                && !finalOperationRequestDto.getSumFromAccountsAmount().equals(finalOperationRequestDto.getSumToAccountsAmount()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.invalidBalanceAmount");


        if (finalOperationRequestDto.hasOnlinePayment()) {
            //Validate References
            this.validateOrDoneReferenceOperationType(operationRequestDto, operationRequest.getAmount(), false);

            //TODO UNCOMMENT **TEMP**
            operationRequestResult.setRedirectUrl(Utils.getCompleteUrl("/onlinepayment/prepareOnlinePayment?refNumber=" + operationRequestResult.getReferenceNumber() + "&requestId="+operationRequest.getId(), false));
            //operationRequestResult.setRedirectUrl("http://192.168.0.115:7275/onlinepayment/prepareOnlinePayment?refNumber=" + operationRequestResult.getReferenceNumber() + "&requestId=" + operationRequest.getId());
            operationRequestResult.setStatus(true);
            return operationRequestResult;
        } else {
            return this.commitOperationRequest(operationRequest, operationRequestResult, additionalData);
        }
    }

    private void checkLimitation(OperationTypeWrapper operationTypeWrapper, Long userId, Double amount) {
        List<Integer> operationTypeCodes = new ArrayList<>();
        operationTypeCodes.add(operationTypeWrapper.getCode());
        if (operationTypeWrapper.getCode().equals(1030))
            operationTypeCodes.add(1031);
        else if (operationTypeWrapper.getCode().equals(1031))
            operationTypeCodes.add(1030);
        Date currentDate = new Date();
        Double sumTodayAmount = accountingService.getSumDebitByAccountTypeIdsAndUserId(operationTypeCodes, userId, calendarService.getDateOnly(currentDate), calendarService.getDateTimeAt24(currentDate));
        if (operationTypeWrapper.getGlobalMaxDailyAmount() != null && amount + sumTodayAmount > operationTypeWrapper.getGlobalMaxDailyAmount())
            throw new InvalidDataException("Invalid Data", "common.operationType.maxDailyDebit_hint");
    }


    private FinalOperationRequestDto prepareFinalOperationRequestDto(OperationRequestDto operationRequestDto, Long userId) {
        //todo set Default Transaction Type
        /*if(operationRequestDto.getTransactionTypeId()==null)
            operationRequestDto.setTransactionTypeId(1l);*/

        FinalOperationRequestDto result = new FinalOperationRequestDto(operationRequestDto);

        Long targetChargeAccountId = null;
        if (result.getToAccounts().size() == 1 && result.getToAccounts().get(0).getAccountId() > 0
                && accountingService.hasOwner(result.getToAccounts().get(0).getAccountId(), userId)
                && accountingService.hasAccountType(result.getToAccounts().get(0).getAccountId() , EAccountType.PERSONAL.getId())) {
            targetChargeAccountId = result.getToAccounts().get(0).getAccountId();
            result.setSelf(true);
        }

        if (result.hasOnlinePayment()) {
            if (targetChargeAccountId != null) {
                if (result.getFromAccounts() != null && result.getFromAccounts().size() > 0) {
                    result.getToAccounts().get(0).setAmount(result.getToAccounts().get(0).getAmount() - result.getOnlineAmount());
                } else {
                    result.getToAccounts().clear();
                }
                result.calcSumToAccountsAmount();
                result.setTargetOnlineAccountId(targetChargeAccountId);
            } else {
                targetChargeAccountId = accountingService.getMainPersonalAccountIdByUserId(userId);
                if (result.getFromAccounts() != null && result.getFromAccounts().size() > 0) {
                    Long finalTargetChargeAccountId = targetChargeAccountId;
                    List<TargetAccountDto> targetChargeAccountDto = result.getFromAccounts().stream().filter(a -> a.getAccountId().equals(finalTargetChargeAccountId)).collect(Collectors.toList());
                    if (targetChargeAccountDto.size() > 0)
                        targetChargeAccountDto.get(0).setAmount(targetChargeAccountDto.get(0).getAmount() + result.getOnlineAmount());
                    else
                        result.getFromAccounts().add(TargetAccountDto.builder()
                                .accountId(targetChargeAccountId)
                                .amount(result.getOnlineAmount())
                                .build());
                } else {
                    if (result.getFromAccounts() == null)
                        result.setFromAccounts(new ArrayList<>());
                    result.getFromAccounts().add(TargetAccountDto.builder()
                            .accountId(targetChargeAccountId)
                            .amount(result.getOnlineAmount())
                            .build());
                }

                result.calcSumFromAccountsAmount();
                result.calcSumToAccountsAmount();
                result.setTargetOnlineAccountId(targetChargeAccountId);
            }
        }

        if (result.getSumFromAccountsAmount() > 0 && result.getSumToAccountsAmount() > 0
                //&& eOperation!=EOperation.CHARGE && eOperation!=EOperation.GET_MONEY
                && !result.getSumFromAccountsAmount().equals(result.getSumToAccountsAmount()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.invalidBalanceAmount");

        if (result.getSumFromAccountsAmount() > 0 && result.getOnlineAmount() < 0)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.invalidBalanceAmount");

        Double sumAmount=0d;
        if ((result.getSumToAccountsAmount() + (result.getSelf() ? result.getOnlineAmount() : 0)) > 0)
            sumAmount=result.getSumToAccountsAmount() + (result.getSelf() ? result.getOnlineAmount() : 0);
        else
            sumAmount=result.getSumFromAccountsAmount();

        //todo Calc Wage Per Transaction Type (Operation)
        Double wageAmount=this.calculateWage(this.getOperationTypeInfoByCode(result.getOperationTypeCode()), sumAmount);
        if(wageAmount>0){
           result.setWage(wageAmount);
           Account targetWageAccount=null;
           if (targetChargeAccountId != null && accountingService.hasAccountType(targetChargeAccountId , EAccountType.PERSONAL.getId()))
               targetWageAccount= accountingService.getAccountInfoById(targetChargeAccountId);
           else
               targetWageAccount= accountingService.getMainPersonalAccountInfoByUserId(userId);

           Long targetWageAccountId=targetWageAccount.getId();
           Double minBalance=BaseUtils.round(wageAmount+((Double)result.getFromAccounts().stream().filter(a->a.getAccountId().equals(targetWageAccountId)).mapToDouble(TargetAccountDto::getAmount).sum()).doubleValue()-result.getOnlineAmount(), ShareUtils.getPanelCurrencyRndNumCount());
           if (targetWageAccount.getAvailableBalance()<minBalance && !result.getSelf()) {
               result.setOnlineWage(minBalance - targetWageAccount.getAvailableBalance());
               result.setTargetOnlineAccountId(targetChargeAccountId);
               result.setOnlineAmountAndWage(null);
           }
        }else {
            result.setWage(0d);
            result.setOnlineWage(0d);
        }
        return result;
    }

    private OperationRequest createOperation(FinalOperationRequestDto finalOperationRequestDto, Long userId, Map<String, Object> additionalData) {
        OperationRequest operationRequest = new OperationRequest();

        TransactionType transactionType = null;
        if (finalOperationRequestDto.getTransactionTypeId() != null && finalOperationRequestDto.getTransactionTypeId() > 0) {
            transactionType = accountingService.getTransactionTypeInfo(finalOperationRequestDto.getTransactionTypeId());
            if (transactionType != null && transactionType.getSystemOnly()) {
                Long additionalTransactionTypeId = Utils.getAsLongFromMap(additionalData, "transactionTypeId", false);
                if (additionalTransactionTypeId == 0)
                    throw new InvalidDataException("Invalid Data", "common.transactionType.id_notFound");
            }
        }
        operationRequest.setTransactionType(transactionType);

        //String referenceNumber=(additionalData!=null && additionalData.get("referenceNumber")!=null) ? (String) additionalData.get("referenceNumber"):null;
        String referenceNumber = Utils.getAsStringFromMap(additionalData, "referenceNumber", false);
        operationRequest.setMaster(Utils.isStringSafeEmpty(referenceNumber));
        if (Utils.isStringSafeEmpty(referenceNumber)) {
            referenceNumber = Utils.createUniqueRandom();
        }
        operationRequest.setReferenceNumber(referenceNumber);

        Long orderId = Utils.getAsLongFromMap(additionalData, "orderId", false);
        if (orderId > 0)
            operationRequest.setOrderId(orderId);

        operationRequest.setTitle(EOperation.valueOfCode(finalOperationRequestDto.getOperationTypeCode()).getCaption());
        operationRequest.setOperationType(this.getOperationTypeInfoByCode(finalOperationRequestDto.getOperationTypeCode()));
        operationRequest.setUserId(userId);
        if (!Utils.isStringSafeEmpty(finalOperationRequestDto.getPlatform()) && finalOperationRequestDto.getPlatform().toLowerCase().equals("mobile"))
            operationRequest.setPlatform("MOBILE");
        else
            operationRequest.setPlatform("WEB");

        if ((finalOperationRequestDto.getSumToAccountsAmount() + (finalOperationRequestDto.getSelf() ? finalOperationRequestDto.getOnlineAmount() : 0)) > 0)
            operationRequest.setAmount(finalOperationRequestDto.getSumToAccountsAmount() + (finalOperationRequestDto.getSelf() ? finalOperationRequestDto.getOnlineAmount() : 0));
        else
            operationRequest.setAmount(finalOperationRequestDto.getSumFromAccountsAmount());

        operationRequest.setWage(finalOperationRequestDto.getWage());

        operationRequest.setSelf(finalOperationRequestDto.getSelf());
        if (!Utils.isStringSafeEmpty(finalOperationRequestDto.getDescription()))
            operationRequest.setDescription(finalOperationRequestDto.getDescription());
        else
            operationRequest.setDescription(EOperation.valueOfCode(finalOperationRequestDto.getOperationTypeCode()).getCaption());

        operationRequest.setSourceType(ETransactionSourceType.WALLET.getId());
        operationRequest.setFinalOperationRequestDto(finalOperationRequestDto);
        operationRequest = operationRequestRepository.save(operationRequest);
        return operationRequest;
    }

    private OperationRequestResult commitOperationRequest(OperationRequest operationRequest, OperationRequestResult operationRequestResult, Map<String, Object> additionalData) {
        FinalOperationRequestDto finalOperationRequestDto = operationRequest.getFinalOperationRequestDto();
        if (finalOperationRequestDto == null)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.invalid");

        EOperation eOperation = EOperation.valueOfCode(operationRequest.getOperationType().getCode());
        if (finalOperationRequestDto.getSumFromAccountsAmount() > 0 && finalOperationRequestDto.getSumToAccountsAmount() > 0
                //&& eOperation!=EOperation.CHARGE && eOperation!=EOperation.GET_MONEY
                && !finalOperationRequestDto.getSumFromAccountsAmount().equals(finalOperationRequestDto.getSumToAccountsAmount()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.invalidBalanceAmount");

        //Validate And Done References
        this.validateOrDoneReferenceOperationType(finalOperationRequestDto, operationRequest.getAmount(), true);
        //Long orderId=(additionalData!=null && additionalData.get("orderId")!=null) ? (Long)additionalData.get("orderId"):null;
        Boolean negativeAllow = (additionalData != null && additionalData.get("negativeAllow") != null) ? (Boolean) additionalData.get("negativeAllow") : false;

        String depositDescription=operationRequest.getDescription();
        if (finalOperationRequestDto.getSumFromAccountsAmount() > 0 && finalOperationRequestDto.getFromAccounts().size() ==1 && finalOperationRequestDto.getFromAccounts().get(0).getUserId()!=null/* > 0 && finalOperationRequestDto.getToAccounts().size()==1*/) {
            if(!finalOperationRequestDto.getSelf()) {
                depositDescription = depositDescription + " - (" + BaseUtils.getMessageResource("global.from") + " "+ userService.getUserInfo(finalOperationRequestDto.getFromAccounts().get(0).getUserId()).getUsername() +")";
            }else if (finalOperationRequestDto.getFromAccounts().get(0).getAccountId()!=null) {
                String accountName=accountingService.getAccountName(finalOperationRequestDto.getFromAccounts().get(0).getAccountId());
                depositDescription = depositDescription + (!BaseUtils.isStringSafeEmpty(accountName) ? " - (" + BaseUtils.getMessageResource("global.from") + " "+accountName+")":"");
            }
        }

        String withdrawDescription=operationRequest.getDescription();
        if (finalOperationRequestDto.getSumToAccountsAmount() > 0 && finalOperationRequestDto.getToAccounts().size()==1  && finalOperationRequestDto.getToAccounts().get(0).getUserId()!=null/*>0 && finalOperationRequestDto.getFromAccounts().size()==1*/) {
            if(!finalOperationRequestDto.getSelf()) {
                withdrawDescription=withdrawDescription +" - (" + BaseUtils.getMessageResource("global.on") + " " + userService.getUserInfo(finalOperationRequestDto.getToAccounts().get(0).getUserId()).getUsername() +")";
            }else if(finalOperationRequestDto.getToAccounts().get(0).getAccountId()!=null) {
                String accountName=accountingService.getAccountName(finalOperationRequestDto.getToAccounts().get(0).getAccountId());
                withdrawDescription=withdrawDescription + (!BaseUtils.isStringSafeEmpty(accountName) ? " - (" + BaseUtils.getMessageResource("global.on") + " "+accountName +")":"");
            }
        }

        Boolean sendNotifyStatus=operationRequest.getOperationType().getNotify();

        if (finalOperationRequestDto.getSumFromAccountsAmount() > 0) {
            String finalWithdrawDescription = withdrawDescription;
            final Account[] firstTransactionAccount = {null};
            finalOperationRequestDto.getFromAccounts().forEach(a -> {
                Transaction withdrawTransaction= accountingService.withdrawAccount(a.getAccountId(), a.getAmount(), operationRequest.getOperationType().getCode(), operationRequest.getTransactionType(), operationRequest.getReferenceNumber(), finalWithdrawDescription, null, operationRequest.getOrderId(), operationRequest.getId(), negativeAllow, false,a.getUserCreditId());
                if(firstTransactionAccount[0] ==null)
                    firstTransactionAccount[0] =withdrawTransaction.getAccount();
            });

            //todo notification
            if(sendNotifyStatus) {
                String[] message;
                Long smsBodyCode=0l;
                if (finalOperationRequestDto.getFromAccounts().size()>1) {
                    smsBodyCode=18l;

                    String[] tmp ={operationRequest.getOperationType().getDescription(), Utils.formatMoney(Utils.round(finalOperationRequestDto.getSumFromAccountsAmount(),Utils.getPanelCurrencyRndNumCount())),calendarService.getNormalDateTimeShortFormat(new Date())};
                    message=tmp;
                }else {
                    smsBodyCode=19l;
                    String[] tmp ={operationRequest.getOperationType().getDescription(),Utils.getMessageResource("global.withdraw"), Utils.formatMoney(Utils.round(finalOperationRequestDto.getSumFromAccountsAmount(),Utils.getPanelCurrencyRndNumCount())),firstTransactionAccount[0].getFormatAvailableBalance(),firstTransactionAccount[0].getName(),calendarService.getNormalDateTimeShortFormat(new Date())};
                    message=tmp;
                }
                User withdrawUser=userService.getUserInfo(operationRequest.getUserId());
                commonService.sendNotification(smsBodyCode, operationRequest.getUserId(), Arrays.asList(withdrawUser.getMobileNumber()), message, Arrays.asList(withdrawUser.getEmail()),  message,((Integer) ETargetTypes.OPERATION_REQUEST.value()).longValue(), operationRequest.getId());
            }
        }

        if (finalOperationRequestDto.getSumToAccountsAmount() > 0) {
            String finalDepositDescription = depositDescription;
            finalOperationRequestDto.getToAccounts().forEach(a -> {
                Transaction depositTransaction=accountingService.depositAccount(a.getAccountId(), a.getAmount(), operationRequest.getOperationType().getCode(), operationRequest.getTransactionType(), operationRequest.getReferenceNumber(), finalDepositDescription, null, operationRequest.getOrderId(), operationRequest.getId());
                //todo add User To merchant
                if(eOperation==EOperation.PURCHASE || eOperation==EOperation.TRANSPORTATION_TRANSFER || eOperation==EOperation.CHARITY_TRANSFER)
                    merchantService.addMerchantCustomer(operationRequest.getUserId(),depositTransaction.getAccount().getUserId());

                //todo notification
                if(sendNotifyStatus) {
                    Long smsBodyCode=19l;
                    String[] message = {operationRequest.getOperationType().getDescription(),Utils.getMessageResource("global.deposit"), Utils.formatMoney(depositTransaction.getCredit()),depositTransaction.getAccount().getFormatAvailableBalance(),depositTransaction.getAccount().getName(),calendarService.getNormalDateTimeShortFormat(new Date())};
                    User depositUser=userService.getUserInfo(depositTransaction.getAccount().getUserId());
                    commonService.sendNotification(smsBodyCode, depositTransaction.getAccount().getUserId(), Arrays.asList(depositUser.getMobileNumber()),  message, Arrays.asList(depositUser.getEmail()), message,((Integer) ETargetTypes.OPERATION_REQUEST.value()).longValue(), operationRequest.getId());
                }
            });
        }


        Long wageAccountId=(finalOperationRequestDto.getTargetOnlineAccountId()!=null  &&
                             accountingService.hasAccountType(finalOperationRequestDto.getTargetOnlineAccountId() , EAccountType.PERSONAL.getId()))
                           ? finalOperationRequestDto.getTargetOnlineAccountId()
                           : accountingService.getMainPersonalAccountIdByUserId(operationRequest.getUserId());
        this.getWageFromAccount(operationRequest.getOperationType(),operationRequest.getUserId(),wageAccountId,operationRequest.getWage(),
                                operationRequest.getOperationType().getDescription()/*operationRequest.getDescription()*/,  operationRequest.getReferenceNumber(),
                                operationRequest.getOrderId(),operationRequest.getId(),negativeAllow);

        operationRequest.setStatus(1);
        operationRequestResult.setStatus(true);
        return operationRequestResult;
    }

    @Transactional
    @Override
    public void calculateAndGetWageFromAccount(OperationType operationType, Long userId, Long accountId ,Double amount,String description,String referenceNumber, Long orderId, Long operationRequestId, Boolean negativeAllow){
        Double wageAmount=this.calculateWage(operationType,amount);
        this.getWageFromAccount(operationType,userId, accountId , wageAmount, description, referenceNumber, orderId, operationRequestId, negativeAllow);
    }

    @Override
    public void getWageFromAccount(OperationType operationType, Long userId, Long accountId, Double wageAmount, String description, String referenceNumber, Long orderId, Long operationRequestId, Boolean negativeAllow) {
        if(wageAmount>0){
            TransactionType transactionType=accountingService.getTransactionTypeInfo(ETransactionType.COMMISSION.getId().longValue());
            String  witDesc=transactionType.getCaption()  + " - " + description;
            accountingService.withdrawAccount(accountId, wageAmount, operationType.getCode(), transactionType,
                    referenceNumber, witDesc, null, orderId, operationRequestId, negativeAllow, false,null);
            String  depDesc=description + " - (" + BaseUtils.getMessageResource("global.from") + " "+ userService.getUserInfo(userId).getUsername() +")";
            accountingService.depositAccount(commonService.getConfigValue("wageAccountId"), wageAmount, operationType.getCode(),
                    transactionType, referenceNumber, depDesc, null, orderId, operationRequestId);
        }
    }

    @Override
    public Double calculateWage(Integer operationTypeCode,Double amount){
        return this.calculateWage(this.getOperationTypeInfoByCode(operationTypeCode),amount);
    }
    private Double calculateWage(OperationType operationType,Double amount){
        EWageType eWageType=EWageType.valueOf(operationType.getWageType());
        if(eWageType==EWageType.NONE || amount==null || amount<=0)
            return 0d;
        else if(eWageType==EWageType.AMOUNT && operationType.getWageAmount()>0)
            return BaseUtils.round(operationType.getWageAmount(), ShareUtils.getPanelCurrencyRndNumCount());
        else if(eWageType==EWageType.PERCENT && operationType.getWageRate()>0)
            return BaseUtils.round((operationType.getWageRate().doubleValue()/100 * amount), ShareUtils.getPanelCurrencyRndNumCount());
        else
            return 0d;
    };


    private void validateToAccounts(EOperation eOperation, List<TargetAccountDto> toAccounts, Long userId, Boolean systemic) {
        if (toAccounts != null) {
            toAccounts.stream().forEach(to -> {
                this.validateToAccount(eOperation, to, systemic);
                if (toAccounts.size() > 1 && accountingService.hasOwner(to.getAccountId(), userId))
                    throw new InvalidDataException("Invalid Data", "common.operationRequest.To_invalid");
            });
        }
    }

    private void validateFromAccounts(List<TargetAccountDto> fromAccounts/*,Boolean required*/, Boolean systemic, Boolean negativeAllow) {
        if (fromAccounts != null) {
            fromAccounts.stream().forEach(from -> {
                this.validateFromAccount(from, systemic, negativeAllow);
            });
        }
    }

    private void validateFromAccount(TargetAccountDto accountDto, Boolean systemic, Boolean negativeAllow) {
        if (accountDto.getAmount() <= 0)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.amount_invalid");

        if (accountDto.getAccountId() == null)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.account_required");

        Account account;
        //if (systemic)
            account = accountingService.getAccountInfoById(accountDto.getAccountId(), false);
        //else
        //    account = accountingService.getAccountInfoById(accountDto.getAccountId());
        accountDto.setUserId(account.getUserId());
        if (!negativeAllow && account.getAvailableBalance() < accountDto.getAmount())
            throw new InvalidDataException("Invalid Data", "common.account.balance_invalid");

        EAccountType eAccountType=EAccountType.valueOf(account.getAccountType().getId());
        if(accountDto.getUserCreditId()==null && (eAccountType==EAccountType.CREDIT_PERSONAL || eAccountType==EAccountType.CREDIT_ORGANIZATION))
            throw new InvalidDataException("Invalid Data", "common.userAccountPolicyCreditDetail.id_required");

        if(accountDto.getUserCreditId()!=null) {
            UserAccountPolicyCreditDetailWrapper userCreditWrapper;
            if (systemic)
                userCreditWrapper=creditService.getUserAccountPolicyCreditDetailWrapperInfo( accountDto.getUserCreditId(),false);
            else
                userCreditWrapper=creditService.getUserAccountPolicyCreditDetailWrapperInfo( accountDto.getUserCreditId());
            if (userCreditWrapper.getWithdrawBalance() < accountDto.getAmount())
                throw new InvalidDataException("Invalid Data", "common.account.balance_invalid");
        }

        //todo Check the account Profile Police.
    }

    private void validateToAccount(EOperation eOperation, TargetAccountDto accountDto, Boolean systemic) {
        if (accountDto.getAmount() <= 0)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.amount_invalid");

        if (!Utils.isStringSafeEmpty(accountDto.getUserName())) {
            Account account = accountingService.getAccountInfoByUserId(userService.getUserInfo(accountDto.getUserName()).getId());
            accountDto.setAccountId(account.getId());
            accountDto.setUserId(account.getUserId());
        }
        if (accountDto.getAccountId() == null)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.account_required");

        this.checkAllowOperationTo(eOperation.getOperationCode(), accountDto.getAccountId(), systemic);
    }


    private Long getTargetUserId(List<TargetAccountDto> toAccounts/*,Boolean required*/) {
        Long targetUserId = 0l;
        TargetAccountDto targetAccount = (/*required && */ toAccounts != null && toAccounts.size() == 1) ? toAccounts.get(0) : null;
        if (targetAccount != null) {
            if (!Utils.isStringSafeEmpty(targetAccount.getUserName()))
                targetUserId = userService.getUserInfo(targetAccount.getUserName()).getId();
            else
                targetUserId = 0l; //todo Check the account owner's user ID later.
        }
        return targetUserId;
    }

    private void matchingFromToAccounts(List<TargetAccountDto> fromAccounts, List<TargetAccountDto> toAccounts) {
        if (fromAccounts != null && fromAccounts.size() > 1 && toAccounts != null && toAccounts.size() > 1)
            throw new InvalidDataException("Invalid Data", "common.operationRequest.tooMeanyAccount");

        fromAccounts.forEach(from -> {
            if (toAccounts.stream().filter(to -> to.getAccountId().equals(from.getAccountId())).count() > 0)
                throw new InvalidDataException("Invalid Data", "common.operationRequest.fromTo_invalid");
        });
    }

    private void validateOperationTypeFromAccounts(String operationTypeCaption, Integer OperationTypeCode, Long userId, List<TargetAccountDto> fromAccounts, Long targetUserId,Double amount) {
        if (fromAccounts != null) {
            List<Long> fromAccountIds = fromAccounts.stream().mapToLong(a -> a.getAccountId()).boxed().collect(Collectors.toList());
            List<AccountWrapper> fromAccountWrappers = accountingService.getAvailableAccountWrappersByOperationTypeCodeAndIds(OperationTypeCode, userId, targetUserId,amount, fromAccountIds);
            fromAccounts.forEach(a -> {
                if (fromAccountWrappers.stream().filter(fa -> fa.getId().equals(a.getAccountId()) && fa.getAvailableBalance() >= a.getAmount()).count() == 0)
                    throw new InvalidDataException("Invalid Data", "common.account.operationType_invalid", operationTypeCaption, a.getAccountId());
            });
        }
    }


    @Override
    public BankPaymentResponse commitAfterOnlinePayment(HttpServletRequest request) throws ServletRequestBindingException {
        return this.commitAfterOnlinePayment(request,false);
    }

    @Override
    public BankPaymentResponse commitAfterOnlinePayment(HttpServletRequest request,Boolean chargeOnly) throws ServletRequestBindingException {
        String orderReferenceNumber = ServletRequestUtils.getStringParameter(request, "myRefId");
        if (orderReferenceNumber == null)
            orderReferenceNumber = ServletRequestUtils.getStringParameter(request, "myrefid");
        if (orderReferenceNumber == null)
            throw new ResourceNotFoundException("Empty myRefId", "global.referenceNumber_notReceived");

        BankPayment bankPayment = paymentUtilsService.getBankPaymentInfoByMyRefNum(orderReferenceNumber);

        if (!chargeOnly && bankPayment.getOperationRequestId() != null && bankPayment.getOperationRequestId() > 0)
            this.getOperationRequestInfo(bankPayment.getOperationRequestId(), false);

        BankPaymentResponse bankPaymentResponse = paymentServiceFactory.getPaymentService(EBank.valueOf(bankPayment.getBank().getId().intValue())).fillBankPaymentFromResponse(request);
        bankPaymentResponse.setBankRequestNumber(bankPayment.getId().toString());
        bankPaymentResponse.setAccountId(bankPayment.getAccountId());
        bankPaymentResponse.setUserId(bankPayment.getCreateBy());
        bankPaymentResponse.setAmount(bankPayment.getAmount());
        bankPaymentResponse.setShipmentIdList(bankPayment.getShipmentIdList());
        if(!chargeOnly)
           bankPaymentResponse.setOperationRequestId(bankPayment.getOperationRequestId());

        return this.generalCommitAfterOnlinePayment(bankPaymentResponse);
    }

    @Override
    public BankPaymentResponse commitAfterOnlineStripePayment(HttpServletRequest request) throws ServletRequestBindingException {
        return this.commitAfterOnlineStripePayment(request,false);
    }

    @Override
    public BankPaymentResponse commitAfterOnlineStripePayment(HttpServletRequest request,Boolean chargeOnly) throws ServletRequestBindingException {
        BankPaymentResponse bankPaymentResponse = paymentServiceFactory.getPaymentService(EBank.STRIPE).fillBankPaymentFromResponse(request);

        if (bankPaymentResponse.getStatus()) {
            BankPayment bankPayment = paymentUtilsService.getBankPaymentInfoByMyRefNum(bankPaymentResponse.getOrderReferenceNumber());

            if (!chargeOnly && bankPayment.getOperationRequestId() != null && bankPayment.getOperationRequestId() > 0)
                this.getOperationRequestInfo(bankPayment.getOperationRequestId(), false);
            bankPaymentResponse.setBankRequestNumber(bankPayment.getId().toString());
            bankPaymentResponse.setAccountId(bankPayment.getAccountId());
            bankPaymentResponse.setUserId(bankPayment.getCreateBy());
            bankPaymentResponse.setAmount(bankPayment.getAmount());
            bankPaymentResponse.setOrderId(bankPayment.getOrderId());
            bankPaymentResponse.setShipmentIdList(bankPayment.getShipmentIdList());
            if(!chargeOnly)
              bankPaymentResponse.setOperationRequestId(bankPayment.getOperationRequestId());
            return this.generalCommitAfterOnlinePayment(bankPaymentResponse);
        } else {
            return bankPaymentResponse;
        }

    }

    public BankPaymentResponse generalCommitAfterOnlinePayment(BankPaymentResponse bankPaymentResponse) throws ServletRequestBindingException {
        try {
            bankPaymentResponse = paymentServiceFactory.getPaymentService(bankPaymentResponse.getBank()).verify(bankPaymentResponse);
        } catch (Exception ex) {
            paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, -2, bankPaymentResponse.getBankReferenceNumber(), null, "unSuccess", "verify:" + ex.getMessage(), null);
            bankPaymentResponse = paymentServiceFactory.getPaymentService(bankPaymentResponse.getBank()).reverse(bankPaymentResponse);
            throw ex;
        }

        if (bankPaymentResponse.getRollback()) {
            //todo rollebak Operation Request
        }

        if (bankPaymentResponse.getRollback() || bankPaymentResponse.getReverse() || bankPaymentResponse.getSettlement())
            return bankPaymentResponse;

        if (bankPaymentResponse.getVerified()) {
            try {
                BankPaymentResponse finalBankPaymentResponse = bankPaymentResponse;
                if (bankPaymentResponse.getOperationRequestId() != null && bankPaymentResponse.getOperationRequestId() > 0)
                    bankPaymentResponse = retryableService.runInRetryable(() -> this.chargeAccountAndCommitOperationRequest(finalBankPaymentResponse));
                else
                    bankPaymentResponse = retryableService.runInRetryable(() -> this.chargeAccount(finalBankPaymentResponse));
            } catch (Exception ex) {
                paymentUtilsService.updateBankResponseByNewTransaction(Utils.parsLong(bankPaymentResponse.getBankRequestNumber()), null, -2, bankPaymentResponse.getBankReferenceNumber(), null, "unSuccess", "settlement:" + ex.getMessage(), null);
                bankPaymentResponse = paymentServiceFactory.getPaymentService(bankPaymentResponse.getBank()).reverse(bankPaymentResponse);
                throw ex;
            }
        }
        return bankPaymentResponse;
    }

    @Override
    @Transactional
    public BankPaymentResponse chargeAccountAndCommitOperationRequest(BankPaymentResponse bankPaymentResponse) {
        bankPaymentResponse = this.onlineCharge(bankPaymentResponse);

        OperationRequest operationRequest = this.getOperationRequestInfo(bankPaymentResponse.getOperationRequestId(), false);
        OperationRequestResult operationRequestResult = new OperationRequestResult();
        operationRequestResult.setOperationRequestId(bankPaymentResponse.getOperationRequestId());
        EOperation eOperation = EOperation.valueOfCode(operationRequest.getOperationType().getCode());
        operationRequestResult = this.commitOperationRequest(operationRequest, operationRequestResult, null);
        bankPaymentResponse.setStatus(operationRequestResult.getStatus());

        bankPaymentResponse = paymentServiceFactory.getPaymentService(bankPaymentResponse.getBank()).settlement(bankPaymentResponse);
        return bankPaymentResponse;
    }

    @Override
    @Transactional
    public BankPaymentResponse chargeAccount(BankPaymentResponse bankPaymentResponse) {
        bankPaymentResponse = this.onlineCharge(bankPaymentResponse);
        bankPaymentResponse = paymentServiceFactory.getPaymentService(bankPaymentResponse.getBank()).settlement(bankPaymentResponse);
        return bankPaymentResponse;
    }


//region DepositRequest

    @Override
    public DepositRequest getDepositRequestInfo(Long depositRequestId) {
        if (depositRequestId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<DepositRequest> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = depositRequestRepository.findByEntityId(depositRequestId);
        else
            result = depositRequestRepository.findByEntityId(depositRequestId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public DepositRequestWrapper getDepositRequestWrapperInfo(Long depositRequestId) {
        if (depositRequestId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<DepositRequestWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = depositRequestRepository.findWrapperById(depositRequestId);
        else
            result = depositRequestRepository.findWrapperByIdAndUserId(depositRequestId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }


    @Override
    public List<DepositRequestWrapper> getDepositRequestWrappers(Map<String, Object> requestParams) {
        Long userId = Utils.getAsLongFromMap(requestParams, "userId", false, Utils.getCurrentUserId());
        Integer start = Utils.getAsIntegerFromMap(requestParams, "start", false);
        Integer count = Utils.getAsIntegerFromMap(requestParams, "count", false);

        if (!hasRoleType(ERoleType.ADMIN))
            userId = Utils.getCurrentUserId();

        return depositRequestRepository.findWrapperByUserId(userId, Utils.gotoPage(start, count));
    }

    @Transactional
    @Override
    public String createDepositRequest(DepositRequestDto depositRequestDto) {
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setUserId(BaseUtils.getCurrentUser().getId());
        depositRequest = this.mapDepositRequestDtoToDb(depositRequestDto, depositRequest);

        depositRequest.setAccount(accountingService.getAccountInfoById(depositRequestDto.getAccountId()));

        if (depositRequestDto.getDepositRequestDetails() != null) {
            Map<String, Long> countByTargetUser = depositRequestDto.getDepositRequestDetails().stream().collect(Collectors.groupingBy(DepositRequestDetailDto::getTargetUser, Collectors.counting()));
            if(countByTargetUser.entrySet().stream().filter(e-> e.getValue()>1).count()>0)
               throw new InvalidDataException("Invalid Data", "common.depositRequest.targetUser_tooMany");

            for (DepositRequestDetailDto d : depositRequestDto.getDepositRequestDetails()) {
                this.createDepositRequestDetail(d, depositRequest);
            }
        }
        depositRequest = depositRequestRepository.save(depositRequest);
        depositRequestDetailRepository.saveAll(depositRequest.getDepositRequestDetails());

        depositRequest.getDepositRequestDetails().forEach(d->{
            this.sendDepositRequestNotify(d);
        });

        return Utils.getMessageResource("global.operation.success_info");
    }

    private DepositRequest mapDepositRequestDtoToDb(DepositRequestDto depositRequestDto, DepositRequest depositRequest) {
        depositRequest.setTitle(depositRequestDto.getTitle());
        depositRequest.setDescription(depositRequestDto.getDescription());
        depositRequest.setActive(depositRequestDto.getActive());
        depositRequest.setExpireDate(calendarService.getDateTimeAt24(calendarService.getDateFromString(depositRequestDto.getExpireDate())));

        if (depositRequest.getExpireDate().getTime() < (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.depositRequest.expireDate_current_invalid");

        if (depositRequest.getDepositRequestDetails() != null && depositRequest.getDepositRequestDetails().stream().filter(d -> d.getDoneDate() != null && d.getDoneDate().getTime() > depositRequest.getExpireDate().getTime()).count() > 0)
            throw new InvalidDataException("Invalid Data", "common.depositRequest.expireDate_done_invalid");

        return depositRequest;
    }

    @Transactional
    @Override
    public String editDepositRequest(DepositRequestDto depositRequestDto) {
        DepositRequest depositRequest = this.getDepositRequestInfo(depositRequestDto.getId());
        depositRequest = this.mapDepositRequestDtoToDb(depositRequestDto, depositRequest);
        depositRequestRepository.save(depositRequest);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String removeDepositRequest(Long depositRequestId) {
        DepositRequest depositRequest = this.getDepositRequestInfo(depositRequestId);
        if (depositRequest.getDepositRequestDetails() != null && depositRequest.getDepositRequestDetails().stream().filter(d -> d.getDoneDate() != null).count() > 0)
            throw new InvalidDataException("Invalid Data", "common.depositRequest.remove_denied");

        depositRequestDetailRepository.deleteAll(depositRequest.getDepositRequestDetails());
        depositRequestRepository.delete(depositRequest);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Override
    public DepositRequestDetail getDepositRequestDetailInfo(Long depositRequestDetailId) {
        if (depositRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<DepositRequestDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = depositRequestDetailRepository.findByEntityId(depositRequestDetailId);
        else
            result = depositRequestDetailRepository.findByEntityId(depositRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public DepositRequestDetail getDepositRequestDetailInfoForReceiverUser(Long depositRequestDetailId) {
        if (depositRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<DepositRequestDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = depositRequestDetailRepository.findByEntityId(depositRequestDetailId);
        else
            result = depositRequestDetailRepository.findByEntityIdAndReceiverId(depositRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public DepositRequestDetailWrapper getDepositRequestDetailWrapperInfo(Long depositRequestDetailId) {
        if (depositRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<DepositRequestDetailWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = depositRequestDetailRepository.findWrapperById(depositRequestDetailId);
        else
            result = depositRequestDetailRepository.findWrapperByIdAndUserId(depositRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public Map<String, Object> allowDepositRequest(String targetUserName) {
        if (targetUserName == null)
            throw new InvalidDataException("Invalid Data", "common.depositRequestDetail.targetUser_required");

        Map<String, Object> result=new HashMap<>();
        Utils.userNameValidate(targetUserName);
        Boolean existUser=userService.existUserByUserName(targetUserName);
        Boolean sendNotifyStatus=this.getOperationTypeSendNotifyStatusByCode(EOperation.DEPOSIT_REQUEST.getOperationCode());
        String hint="";
        if(!existUser && sendNotifyStatus && commonService.getExternalDepositRequestStatus()){
            existUser=true;
            hint=Utils.getMessageResource("common.depositRequestDetail.targetUser_notRegisterHint");
        }
        result.put("allow",existUser);
        result.put("hint",hint);
        return result;
    }

    @Override
    public MessageBoxWrapper getDetailMessageWrapperInfo(Integer notifyTargetTypeId, Long targetId) {
        ENotifyTargetType eNotifyTargetType=ENotifyTargetType.valueOf(notifyTargetTypeId);
        if(eNotifyTargetType==ENotifyTargetType.MESSAGE_BOX)
           return null;
        else if(eNotifyTargetType==ENotifyTargetType.DEPOSIT_REQUEST)
           return this.getDepositRequestDetailMessageWrapperInfo(targetId);
        else if(eNotifyTargetType==ENotifyTargetType.COST_SHARE_REQUEST)
            return costSharingService.getCostShareRequestDetailMessageWrapperInfo(targetId);
        else
            return null;
    }

    @Override
    public MessageBoxWrapper getDepositRequestDetailMessageWrapperInfo(Long depositRequestDetailId) {
        if (depositRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<MessageBoxWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = depositRequestDetailRepository.findMessageBoxWrappersByDepositRequestDetailId(depositRequestDetailId);
        else
            result = depositRequestDetailRepository.findMessageBoxWrappersByDepositRequestDetailIdAndUserId(depositRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");

        if (result.get().getSeenDate() == null && Utils.getCurrentUserId().equals(result.get().getTargetUserId()))
            this.seenDepositRequestDetail(depositRequestDetailId);

        return result.get();

    }

    @Transactional
    @Override
    public String addDepositRequestDetail(DepositRequestDetailNewDto depositRequestDetailNewDto) {
        DepositRequest depositRequest = this.getDepositRequestInfo(depositRequestDetailNewDto.getDepositRequestId());

        DepositRequestDetail depositRequestDetail = this.createDepositRequestDetail(depositRequestDetailNewDto, depositRequest);
        depositRequestDetailRepository.save(depositRequestDetail);
        this.sendDepositRequestNotify(depositRequestDetail);
        return Utils.getMessageResource("global.operation.success_info");
    }

    private void sendDepositRequestNotify(DepositRequestDetail depositRequestDetail){
        Boolean sendNotifyStatus=this.getOperationTypeSendNotifyStatusByCode(EOperation.DEPOSIT_REQUEST.getOperationCode());
        if(sendNotifyStatus) {
            List<String> mobileNumbers=new ArrayList<>();
            List<String> emails=new ArrayList<>();
            String targetUser=null;
            if(depositRequestDetail.getUserId()!=null){
                User targetUserInfo=userService.getUserInfo(depositRequestDetail.getUserId());
                if(!Utils.isStringSafeEmpty(targetUserInfo.getMobileNumber()))
                    mobileNumbers.add(targetUserInfo.getMobileNumber());
                if(!Utils.isStringSafeEmpty(targetUserInfo.getEmail()))
                    emails.add(targetUserInfo.getEmail());
            }else{
                if(Utils.getUserNameField().equalsIgnoreCase("mobile"))
                    mobileNumbers.add(depositRequestDetail.getTargetUser());
                else if(Utils.getUserNameField().equalsIgnoreCase("email"))
                    emails.add(depositRequestDetail.getTargetUser());
            }
            Long smsBodyCode = 20l;
            String[] message = {Utils.getCurrentUser().getUsername()};
            commonService.sendNotification(smsBodyCode, depositRequestDetail.getUserId(),mobileNumbers, message, emails, message, ((Integer) ETargetTypes.DEPOSIT_REQUEST.value()).longValue(), depositRequestDetail.getId());
        }
    }

    private DepositRequestDetail createDepositRequestDetail(DepositRequestDetailDto depositRequestDetailDto, DepositRequest depositRequest) {
        DepositRequestDetail depositRequestDetail = new DepositRequestDetail();
        depositRequestDetail = this.mapDepositRequestDetailDtoToDb(depositRequestDetailDto, depositRequestDetail);
        User targetUser=null;
        try {
            targetUser = userService.getUserInfo(depositRequestDetailDto.getTargetUser());
        }catch (ResourceNotFoundException ex){
            if(!commonService.getExternalDepositRequestStatus())
                throw ex;
        }

        if(targetUser!=null) {
           if (Utils.getCurrentUserId().equals(targetUser.getId()))
               throw new InvalidDataException("Invalid Data", "common.depositRequest.self_invalid");
           depositRequestDetail.setUserId(targetUser.getId());
        }else{
           Utils.userNameValidate(depositRequestDetailDto.getTargetUser());
           depositRequestDetail.setTargetUser(depositRequestDetailDto.getTargetUser());
        }
        depositRequestDetail.setDepositRequest(depositRequest);

        if(depositRequest.getId()!=null) {
            List<DepositRequestDetailWrapper> depositRequestDetailWrappers = depositRequestDetailRepository.findWrappersByDepositRequestId(depositRequest.getId());
            if (depositRequestDetailWrappers != null && depositRequestDetailWrappers.stream().filter(d -> d.getTargetUser().equalsIgnoreCase(depositRequestDetailDto.getTargetUser())).count() > 0)
                throw new InvalidDataException("Invalid Data", "common.depositRequest.targetUser_tooMany");
        }

        /*final Long targetUserId = depositRequestDetail.getUserId();
        if (depositRequest.getDepositRequestDetails() != null && depositRequest.getDepositRequestDetails().stream().filter(d -> d.getUserId().equals(targetUserId)).count() > 0)
            throw new InvalidDataException("Invalid Data", "common.depositRequest.targetUser_tooMany");*/

        depositRequest.getDepositRequestDetails().add(depositRequestDetail);
        return depositRequestDetail;
    }

    private DepositRequestDetail mapDepositRequestDetailDtoToDb(DepositRequestDetailDto depositRequestDetailDto, DepositRequestDetail depositRequestDetail) {
        depositRequestDetail.setAmount(depositRequestDetailDto.getAmount());
        return depositRequestDetail;
    }


    @Transactional
    @Override
    public String editDepositRequestDetail(DepositRequestDetailDto depositRequestDetailDto) {
        DepositRequestDetail depositRequestDetail = this.getDepositRequestDetailInfo(depositRequestDetailDto.getId());
        if (depositRequestDetail.getDoneDate() != null)
            throw new InvalidDataException("Invalid Data", "common.depositRequestDetail.done_hint");

        if (depositRequestDetail.getDepositRequest().getExpireDate().getTime() < (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.depositRequest.expireDate_hint");

        depositRequestDetail = this.mapDepositRequestDetailDtoToDb(depositRequestDetailDto, depositRequestDetail);
        depositRequestDetailRepository.save(depositRequestDetail);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String removeDepositRequestDetail(Long depositRequestDetailId) {
        if (depositRequestDetailId == null)
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        DepositRequestDetail depositRequestDetail = this.getDepositRequestDetailInfo(depositRequestDetailId);
        if (depositRequestDetail.getDoneDate() != null)
            throw new InvalidDataException("Invalid Data", "common.depositRequestDetail.done_hint");

        if (depositRequestDetail.getDepositRequest().getDepositRequestDetails().size() == 1) {
            return this.removeDepositRequest(depositRequestDetail.getDepositRequest().getId());
        } else {
            depositRequestDetailRepository.delete(depositRequestDetail);
            return Utils.getMessageResource("global.operation.success_info");
        }
    }

    @Transactional
    @Override
    public void seenDepositRequestDetail(Long depositRequestDetailId) {
        if (depositRequestDetailId == null)
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        DepositRequestDetail depositRequestDetail = this.getDepositRequestDetailInfoForReceiverUser(depositRequestDetailId);
        if (depositRequestDetail.getSeenDate() == null) {
            depositRequestDetail.setSeenDate(new Date());
            depositRequestDetailRepository.save(depositRequestDetail);
        }
    }

    @Transactional
    @Override
    public void doneDepositRequestDetail(Long depositRequestDetailId, Double amount) {
        DepositRequestDetail depositRequestDetail = this.validatePreDoneDepositRequestDetail(depositRequestDetailId, amount);
        depositRequestDetail.setDoneDate(new Date());
        depositRequestDetailRepository.save(depositRequestDetail);
    }

    @Override
    public DepositRequestDetail validatePreDoneDepositRequestDetail(Long depositRequestDetailId, Double amount) {
        DepositRequestDetail depositRequestDetail = this.getDepositRequestDetailInfoForReceiverUser(depositRequestDetailId);
        if (depositRequestDetail.getDoneDate() != null)
            throw new InvalidDataException("Invalid Data", "common.depositRequestDetail.done_older");
        if (depositRequestDetail.getDepositRequest().getExpireDate().getTime() < (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.depositRequest.expireDate_hint");
        if (amount != null && !amount.equals(depositRequestDetail.getAmount()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.referenceOperationTypeAmount_invalid");

        return depositRequestDetail;
    }

    @Transactional
    @Override
    public Integer linkDepositRequest(String userName, Long usrId) {
        return depositRequestDetailRepository.linkMyDepositRequest(userName,usrId);
    }

    //endregion DepositRequest



}
