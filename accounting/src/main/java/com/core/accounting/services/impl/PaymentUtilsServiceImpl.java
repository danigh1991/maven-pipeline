package com.core.accounting.services.impl;

import com.core.accounting.model.enums.EBankPaymentStatus;
import com.core.accounting.model.wrapper.BankPaymentWrapper;
import com.core.accounting.services.PaymentService;
import com.core.accounting.services.PaymentUtilsService;
import com.core.accounting.services.factory.PaymentServiceFactory;
import com.core.common.services.impl.AbstractService;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralBankObject;
import com.core.datamodel.repository.BaseBankRepository;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.datamodel.model.dbmodel.Bank;
import com.core.accounting.model.dbmodel.BankPayment;
import com.core.datamodel.model.enums.EBank;
import com.core.accounting.repository.BankPaymentRepository;
import com.core.accounting.services.AccountingService;
import com.core.common.services.UserService;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("paymentUtilsService")
public class PaymentUtilsServiceImpl  extends AbstractService implements PaymentUtilsService{

    @Value("#{${bankPayment.search.native.private.params}}")
    private HashMap<String, List<String>> BANK_PAYMENT_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;

    @Autowired
    private BankPaymentRepository bankPaymentRepository;

    @Autowired
    private  AccountingService accountingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private PaymentServiceFactory paymentServiceFactory;

    @Autowired
    private DynamicQueryHelper dynamicQueryHelper;

    @Autowired
    private BaseBankRepository baseBankRepository;



    //#region  Bank

    @Override
    public List<Bank> getAllBanks() {
        return baseBankRepository.findAllBanks();
    }


    //#endregion Bank

    //#region  Bank Payment

    @Override
    public BankPayment getBankPaymentInfo(Long bankPaymentId) {
        if(bankPaymentId == null)
            throw new InvalidDataException("Invali Data", "bankPayment.id_required");
        BankPayment bankPayment=bankPaymentRepository.findByEntityId(bankPaymentId);
        if (bankPayment== null)
            throw new ResourceNotFoundException(bankPaymentId.toString(), "bankPayment.id_notFound" , bankPaymentId );
        return bankPayment;
    }

    @Override
    public BankPayment getBankPaymentInfoByMyRefNum(String myRefNum) {
        if(Utils.isStringSafeEmpty(myRefNum))
            throw new InvalidDataException("Invalid Data", "bankPayment.reference_required");
        BankPayment bankPayment=bankPaymentRepository.findByMyReferenceNumber(myRefNum);
        if (bankPayment== null)
            throw new ResourceNotFoundException(myRefNum, "bankPayment.reference_notFound" , myRefNum );
        return bankPayment;
    }

    @Override
    public Integer countSuccessPaymentByBankReferenceNumber(String bankReferenceNumber) {
        return bankPaymentRepository.countSuccessPaymentByBankReferenceNumber(bankReferenceNumber);
    }

    @Override
    public Integer countSuccessByMyReferenceNumber(String myReferenceNumber) {
        return bankPaymentRepository.countSuccessPaymentByMyReferenceNumber(myReferenceNumber);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BankPayment createBankResponse(Long userId,Long accountId,Double amount , EBank eBank, Long transactionId, Integer status, String bankReferenceNumber, String bankTransactionRef, String bankStatus, String bankResponse, String myReferenceNumber, Long orderId,String shipmentIds,Long operationRequestId){
        BankPayment bankPayment=new BankPayment();
        bankPayment.setAccountId(accountId);
        bankPayment.setAmount(amount);
        Bank bank=accountingService.getBankInfo(eBank.getId().longValue());
        if (bank==null)
            throw new ResourceNotFoundException("Invalid Data"  , "eBank.id_notFound1" , eBank.getCaption() );
        bankPayment.setBank(bank);
        bankPayment.setTransactionId(transactionId);
        bankPayment.setBankReferenceNumber(bankReferenceNumber);
        bankPayment.setBankTransactionRef(bankTransactionRef);
        bankPayment.setBankResponseLastStatus(bankStatus);
        bankPayment.setBankResponseStatusHist((bankPayment.getBankResponseStatusHist()!=null? bankPayment.getBankResponseStatusHist() + "<>" + bankStatus:bankStatus));
        bankPayment.setCreateBy(userId);
        //bankPayment.setCreateBy(userService.getCurrentUser().getId());
        bankPayment.setStatus(status);
        bankPayment.setCreateDate(new Date(System.currentTimeMillis()));
        bankPayment.setModifyDate(new Date(System.currentTimeMillis()));
        bankPayment.setModifyBy(userId);
        bankPayment.setBankResponse(bankResponse);
        bankPayment.setMyReferenceNumber(myReferenceNumber);
        bankPayment.setOrderId(orderId);
        bankPayment.setShipmentIds(shipmentIds);
        bankPayment.setOperationRequestId(operationRequestId);
        bankPayment=bankPaymentRepository.save(bankPayment);
        return bankPayment;
    }



    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateBankResponseByNewTransaction(Long bankPaymentId, Long transactionId, Integer status, String bankReferenceNumber, String bankTransactionRef, String bankStatus, String bankResponse,Long orderId){
        BankPayment bankPayment=this.getBankPaymentInfo(bankPaymentId);
        if(transactionId!=null)   bankPayment.setTransactionId(transactionId);

        if(bankReferenceNumber!=null) bankPayment.setBankReferenceNumber(bankReferenceNumber);

        if(bankTransactionRef!=null) bankPayment.setBankTransactionRef(bankTransactionRef);

        if(bankStatus!=null) {
            bankPayment.setBankResponseLastStatus(bankStatus);
            bankPayment.setBankResponseStatusHist((bankPayment.getBankResponseStatusHist()!=null? bankPayment.getBankResponseStatusHist() + "<>" + bankStatus:bankStatus));
        }

        if(status!=null) bankPayment.setStatus(status);

        bankPayment.setModifyDate(new Date(System.currentTimeMillis()));
        bankPayment.setModifyBy(Utils.getCurrentUserId());

        if(bankResponse!=null) bankPayment.setBankResponse((bankPayment.getBankResponse()!=null? bankPayment.getBankResponse() + "<>" + bankResponse:bankResponse));

        if(orderId!=null) bankPayment.setOrderId(orderId);

        bankPaymentRepository.save(bankPayment);
    }

    @Override
    public void updateBankResponseInCurrentTransaction(Long bankPaymentId, Long transactionId, Integer status, String bankReferenceNumber, String bankTransactionRef, String bankStatus, String bankResponse, Long orderId) {
        BankPayment bankPayment=this.getBankPaymentInfo(bankPaymentId);
        if(transactionId!=null)   bankPayment.setTransactionId(transactionId);

        if(bankReferenceNumber!=null) bankPayment.setBankReferenceNumber(bankReferenceNumber);

        if(bankTransactionRef!=null) bankPayment.setBankTransactionRef(bankTransactionRef);

        if(bankStatus!=null) {
            bankPayment.setBankResponseLastStatus(bankStatus);
            bankPayment.setBankResponseStatusHist((bankPayment.getBankResponseStatusHist()!=null? bankPayment.getBankResponseStatusHist() + "<>" + bankStatus:bankStatus));
        }

        if(status!=null) bankPayment.setStatus(status);

        bankPayment.setModifyDate(new Date(System.currentTimeMillis()));
        bankPayment.setModifyBy(Utils.getCurrentUserId());

        if(bankResponse!=null) bankPayment.setBankResponse((bankPayment.getBankResponse()!=null? bankPayment.getBankResponse() + "<>" + bankResponse:bankResponse));

        if(orderId!=null) bankPayment.setOrderId(orderId);

        bankPaymentRepository.save(bankPayment);
    }

    @Override
    public List<BankPayment> getBankPayments(Integer status, Integer start, Integer count) {
        return bankPaymentRepository.findBankPayments(status,gotoPage(start,count));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GeneralBankObject createBankPaymentRequest(GeneralBankObject generalBankObject, Double payAmount,EBank eBank,String referenceNumber,Map<String,Object> additional){
        //Long requestId,Long orderId,String shipmentPlanIds
        if (additional==null) additional=new HashMap<>();

        Long accountId=additional.get("accountId")!=null ? accountingService.getAccountInfoById((Long)additional.get("accountId"),false).getId():accountingService.getMainPersonalAccountInfoByUserId(BaseUtils.getCurrentUserId()).getId();
        Long operationRequestId=additional.get("operationRequestId")!=null ? (Long)additional.get("operationRequestId"):null;
        Long orderId=additional.get("orderId")!=null ? (Long)additional.get("orderId"):null;
        String shipmentPlanIds=additional.get("shipmentPlanIds")!=null ? (String)additional.get("shipmentPlanIds"):null;

        String returnUrl=additional.get("returnUrl")!=null ? (String) additional.get("returnUrl"):null;
        String paymentMethod=additional.get("paymentMethod")!=null ? (String) additional.get("paymentMethod"):null;
        String currency=additional.get("currency")!=null ? (String) additional.get("currency"):null;
        String notifyUrl=additional.get("notifyUrl")!=null ? (String) additional.get("notifyUrl"):null;

        BankPayment bankPayment = this.createBankResponse(generalBankObject.getUserId(),accountId,payAmount, eBank, null, 0, null, null, null, null, referenceNumber, orderId,shipmentPlanIds,operationRequestId);
        generalBankObject.setBankRequestNumber(bankPayment.getId().toString());
        PaymentService paymentService = paymentServiceFactory.getPaymentService(eBank);
        generalBankObject.setEBank(eBank);

        Map<String,String> paymentParam=new HashMap<>();
        paymentParam.put("title", additional.get("title")!=null ? (String) additional.get("title") :BaseUtils.getMessageResource("eTargetTypes.order"));
        if (userService.isAuthUser()) {
            paymentParam.put("email", userService.getCurrentUser().getUser().getEmail());
            paymentParam.put("firstName", additional.get("firstName") != null ? (String) additional.get("firstName") : (userService.getCurrentUser().getFirstName() != null ? userService.getCurrentUser().getFirstName() : ""));
            paymentParam.put("lastName", additional.get("lastName") != null ? (String) additional.get("lastName") : (userService.getCurrentUser().getLastName() != null ? userService.getCurrentUser().getLastName() : ""));
            paymentParam.put("phone", additional.get("phone") != null ? (String) additional.get("phone") : (userService.getCurrentUser().getUser().getMobileNumber()));
        }
        paymentParam.put("address", additional.get("address")!=null ? (String) additional.get("address"):"");
        paymentParam.put("zip", additional.get("zip")!=null ? (String) additional.get("zip"):"");
        paymentParam.put("country", additional.get("title")!=null ? (String) additional.get("title") :"");
        paymentParam.put("city", additional.get("title")!=null ? (String) additional.get("title") :"");
        //paymentParam.put("orderId", orderId.toString());
        //paymentParam.put("requestId", requestId.toString());

        generalBankObject.setBankInfo(paymentService.getBankPaymentParams(generalBankObject.getUserId(),payAmount,referenceNumber, bankPayment.getId().toString(),returnUrl , paymentMethod, currency, notifyUrl ,paymentParam));
        //preFinalOrderWrapper.setStatus(true);
        generalBankObject.setMessage(Utils.getMessageResource("global.redirectToBank"));

        return generalBankObject;
    }

    @Override
    public ResultListPageable<BankPaymentWrapper> getBankPaymentWrappers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(BANK_PAYMENT_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "bankPaymentWrapperMapping");
        ResultListPageable<BankPaymentWrapper> externalApiCallGeneral=this.getBankPaymentGeneral(requestParams);
        return externalApiCallGeneral;
    }


    @Override
    public BankPaymentWrapper getBankPaymentWrapperInfo(Long bankPaymentId) {
        if(bankPaymentId==null)
            throw new InvalidDataException("Invalid Data", "common.bankPayment.id_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("id",bankPaymentId);
        requestParams.put("start",0);
        requestParams.put("count",1);
        requestParams.put("resultSetMapping", "bankPaymentWrapperMapping");

        ResultListPageable<BankPaymentWrapper> result=this.getBankPaymentGeneral(requestParams);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);

        throw new ResourceNotFoundException(bankPaymentId.toString(), "common.bankPayment.id_notFound");
    }

    private String getBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return "select b.bpy_id as id , b.bpy_bnk_id as bankId, k.bnk_name as bankName, b.bpy_acc_id as accountId,  b.bpy_my_reference_number as myReferenceNumber,  b.bpy_amount as amount,\n" +
                "      b.bpy_bank_reference_number as bankReferenceNumber,   b.bpy_bank_response_last_status as bankResponseLastStatus, b.bpy_bank_response_status_hist as bankResponseStatusHist,\n" +
                "      b.bpy_bank_response_transaction_ref as bankResponseTransactionRef, "+ (loadDetails ? "b.bpy_bank_response" : "''") + "  as bankResponse,\n" +
                "      b.bpy_ord_id as orderId, b.bpy_shp_ids as shpIds , b.bpy_status as status, b.bpy_trn_id as transactionId,  b.bpy_opr_id as operationRequestId,\n" +
                "      b.bpy_create_by as createBy,  b.bpy_create_date as createDate, b.bpy_modify_by as modifyBy,  b.bpy_modify_date as modifyDate\n";
    }

    private String getBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getBaseQueryBody(Map<String, Object> requestParams) {
        String queryString=" from sc_bank_payment b \n" +
                " inner join sc_bank k on (b.bpy_bnk_id=k.bnk_id) \n";
        return  queryString;
    }

    private ResultListPageable<BankPaymentWrapper> getBankPaymentGeneral(Map<String, Object> requestParams) {

        String queryString = this.getBaseQueryHead(requestParams)+ this.getBaseQueryBody(requestParams);
        //Utils.getAsStringFromMap(requestParams, "baseQueryBody", true,this.getBaseQueryBody(requestParams));

        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getBaseCountQueryHead(requestParams)+this.getBaseQueryBody(requestParams);

        return dynamicQueryHelper.getDataGeneral(requestParams,BANK_PAYMENT_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,BankPaymentWrapper.class);
    }

    public List<TypeWrapper> getBankPaymentStatuses() {
        return EBankPaymentStatus.getAllAsObjectWrapper();
    }

    //#endregion Bank Payment


}
