package com.core.accounting.model.wrapper;

import com.core.accounting.model.contextmodel.FinalOperationRequestDto;
import com.core.accounting.model.enums.*;
import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.TransactionRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import com.core.datamodel.util.ShareUtils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class OperationRequestWrapper implements Serializable {

    @JsonView(AccountJsonView.OperationRequestShortList.class)
    private Long id;
    @JsonView(AccountJsonView.OperationRequestShortList.class)
    private String title;
    private Long userId;
    private Integer operationTypeId;
    private Integer sourceType; //1=Wallet , 2=Card
    @JsonView(AccountJsonView.OperationRequestList.class)
    private Double amount;
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    private Double wage;
    private Integer status;
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    private String cardNumber;
    private String details;
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    private String referenceNumber;
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    private String description;
    private Boolean self;
    private Date createDate;
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;

    private FinalOperationRequestDto finalTransactionRequestDto;
    private List<RequestTransactionWrapper> requestTransactionWrappers;

    private String sourceUserName;
    private String sourceUserPersonalName;
    private List<List<GeneralKeyValue>> otherDetails;
    private List<List<GeneralKeyValue>> financialDetails;
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    private List<List<GeneralKeyValue>> additionalDetails=new ArrayList<>();


    public OperationRequestWrapper(Long id, String title, Long userId, Integer operationTypeId, Integer sourceType,Double amount,Double wage, Integer status, String cardNumber, String details, String referenceNumber, String description,Boolean self, Date createDate,Long createBy, Date modifyDate, Long modifyBy) {
        this.id = id;
        this.title=title;
        this.userId = userId;
        this.operationTypeId = operationTypeId;
        this.sourceType = sourceType;
        this.amount= amount;
        this.wage= wage;
        this.status = status;
        this.cardNumber = cardNumber;
        this.details = details;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.self = self;
        this.createDate = createDate;
        this.createBy = createBy;
        this.modifyDate = modifyDate;
        this.modifyBy = modifyBy;
    }

    @JsonView(AccountJsonView.OperationRequestList.class)
    public String  getTrackingCode1() {
        return Utils.getTrackingCode(EOperation.valueOf(this.operationTypeId).getOperationCode().toString(),this.getId());
    }
    @JsonView(AccountJsonView.OperationRequestDetails.class)
    public String getTrackingCode() {
        return ShareUtils.getNormalDateWithoutSlash(this.getCreateDate())+this.getId();
    }

    @JsonView(AccountJsonView.OperationRequestList.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


    public Integer getStatus() {
        if(this.status==0 && (new Date()).getTime()<Utils.addMinuteToDate(this.getCreateDate(),Utils.getOperationRequestWaitMinuteTime()).getTime())
            return 0;
        else if(this.status==0 && (new Date()).getTime()>=Utils.addMinuteToDate(this.getCreateDate(),Utils.getOperationRequestWaitMinuteTime()).getTime())
            return 2;
        else
            return this.status;
    }

    @JsonView(AccountJsonView.OperationRequestShortList.class)
    public String getStatusDesc() {
        if (this.getStatus() == 0)
            return EOperationRequestStatus.AWAITING_PAYMENT.getCaption();
        else if (this.getStatus() == 1)
            return EOperationRequestStatus.SUCCESS.getCaption();
        else
            return EOperationRequestStatus.UN_SUCCESS.getCaption();
    }

    @JsonView(AccountJsonView.OperationRequestShortList.class)
    public Boolean getResultStatus() {
        if (this.getStatus() == 1)
            return true;
        else
            return false;
    }

    /*@JsonView(AccountJsonView.OperationRequestDetails.class)
    public List<TransactionWrapper> getTransactionWrappers() {
        if (transactionWrappers == null) {
            transactionWrappers=((TransactionRepository) AccountingRepositoryFactory.getRepository(ERepository.TRANSACTION)).getTransactionWrappersByOperationRequestId(this.getId());
        }
        return transactionWrappers;
    }*/
    //@JsonView(AccountJsonView.OperationRequestDetails.class)
    public List<RequestTransactionWrapper> getRequestTransactionWrappers() {
        if (this.requestTransactionWrappers == null) {
            this.requestTransactionWrappers=((TransactionRepository) AccountingRepositoryFactory.getRepository(ERepository.TRANSACTION)).getRequestTransactionWrappersByOperationRequestId(this.getId());
        }
        return this.requestTransactionWrappers;
    }

    @JsonView(AccountJsonView.OperationRequestList.class)
    public String getSourceTypeCaption() {
        return ETransactionSourceType.valueOf(this.sourceType).getCaption();
    }

    public FinalOperationRequestDto getFinalTransactionRequestDto() {
        if (this.finalTransactionRequestDto ==null && !Utils.isStringSafeEmpty(this.details))
            this.finalTransactionRequestDto =Utils.readFromJson(this.details,new TypeReference<FinalOperationRequestDto>() {});
        return this.finalTransactionRequestDto;

    }

    @JsonView(AccountJsonView.OperationRequestDetails.class)
    public String getSourceUserName() {
        if(this.sourceUserName==null)
            this.fillDetails();
        return this.sourceUserName;
    }

    @JsonView(AccountJsonView.OperationRequestDetails.class)
    public List<List<GeneralKeyValue>> getOtherDetails() {
        if(otherDetails ==null)
            this.fillDetails();
        return otherDetails;
    }

    @JsonView(AccountJsonView.OperationRequestDetails.class)
    public List<List<GeneralKeyValue>> getFinancialDetails() {
        if(this.financialDetails==null)
            this.fillDetails();
        return this.financialDetails;
    }

    @Deprecated
    private void fillDetails2() {
        this.otherDetails =new ArrayList<>();
        this.financialDetails=new ArrayList<>();


        Map<String, List<RequestTransactionWrapper>> sourceRequestWrapperMap = this.getRequestTransactionWrappers().stream().filter(t-> t.getDebit()>0 ).collect(Collectors.groupingBy(RequestTransactionWrapper::getAccountUserName));
        for (Map.Entry<String,List<RequestTransactionWrapper>> entry : sourceRequestWrapperMap.entrySet()){
            RequestTransactionWrapper tmpWrapper=new RequestTransactionWrapper();
            entry.getValue().forEach(t-> {
                if (t.getDebit() > 0 && (this.sourceUserName == null || this.sourceUserPersonalName == null)) {
                    this.sourceUserName = BaseUtils.unreadableString(t.getAccountUserName(),4,4,'*');
                    this.sourceUserPersonalName =  (!Utils.isStringSafeEmpty(Utils.getNullStringAsEmpty(t.getAccountUserFirstName()) + Utils.getNullStringAsEmpty(t.getAccountUserLastName())) ? (Utils.getNullStringAsEmpty(t.getAccountUserFirstName()) + " " + Utils.getNullStringAsEmpty(t.getAccountUserLastName())) : "");
                }
                if(tmpWrapper.getAccountId()==null){
                    tmpWrapper.setAccountId(t.getAccountId());
                    tmpWrapper.setAccountName(t.getAccountName());
                    tmpWrapper.setAccountUserFirstName(t.getAccountUserFirstName());
                    tmpWrapper.setAccountUserLastName(t.getAccountUserLastName());
                    tmpWrapper.setAccountUserId(t.getAccountUserId());
                    tmpWrapper.setAccountUserName(BaseUtils.unreadableString(t.getAccountUserName(),4,4,'*'));
                    tmpWrapper.setDescription(t.getDescription());
                    tmpWrapper.setCreateDate(t.getCreateDate());
                    tmpWrapper.setCreateBy(t.getCreateBy());
                    tmpWrapper.setModifyDate(t.getModifyDate());
                    tmpWrapper.setModifyBy(t.getModifyBy());
                    tmpWrapper.setOperationTypeId(t.getOperationTypeId());
                    tmpWrapper.setTransactionId(t.getTransactionId());
                    tmpWrapper.setStatus(t.getStatus());
                    tmpWrapper.setUserCreditId(t.getUserCreditId());
                    tmpWrapper.setUserCreditName(t.getUserCreditName());
                }
                tmpWrapper.setDebit(tmpWrapper.getDebit()+t.getDebit());
                List<GeneralKeyValue> result=this.generateFinancialDetailsRow(t);
                if(result!=null && result.size()>0)
                   financialDetails.add(result);
            });
            if(!this.getSelf()) {
                List<GeneralKeyValue> result=this.generateOtherDetailsRow(tmpWrapper);
                if(result!=null && result.size()>0)
                  otherDetails.add(result);
            }
        }


       List<RequestTransactionWrapper> creditList=this.getRequestTransactionWrappers().stream().filter(r->r.getCredit()>0).sorted(Comparator.comparing(RequestTransactionWrapper::getTransactionId)).collect(Collectors.toList());
        creditList.forEach(t->{
/*            if(t.getDebit()>0 && (this.sourceUserName==null || this.sourceUserPersonalName==null)) {
                this.sourceUserName = t.getAccountUserName();
                this.sourceUserPersonalName=(!Utils.isStringSafeEmpty(t.getAccountUserFirstName() + " " + t.getAccountUserLastName())?t.getAccountUserFirstName() + " " + t.getAccountUserLastName():t.getAccountUserName());
            }*/
            if(!this.getSelf()) {
                List<GeneralKeyValue> result=this.generateOtherDetailsRow(t);
                if(result!=null && result.size()>0)
                    otherDetails.add(result);
            }
            List<GeneralKeyValue> result=this.generateFinancialDetailsRow(t);
            if(result!=null && result.size()>0)
              financialDetails.add(result);
        });
    }

    private void fillDetails() {
        this.otherDetails =new ArrayList<>();
        this.financialDetails=new ArrayList<>();

        List<RequestTransactionWrapper> selfCreditList=this.getRequestTransactionWrappers().stream().filter(r-> EOperation.CHARGE.getId()== r.getOperationTypeId() && r.getCredit()>0).sorted(Comparator.comparing(RequestTransactionWrapper::getTransactionId)).collect(Collectors.toList());
        selfCreditList.forEach(t->{
            List<GeneralKeyValue> result=this.generateFinancialDetailsRow(t);
            if(result!=null && result.size()>0)
                financialDetails.add(result);
        });

        Map<String, List<RequestTransactionWrapper>> sourceRequestWrapperMap = this.getRequestTransactionWrappers().stream().filter(t-> t.getDebit()>0 ).collect(Collectors.groupingBy(RequestTransactionWrapper::getAccountUserName));
        for (Map.Entry<String,List<RequestTransactionWrapper>> entry : sourceRequestWrapperMap.entrySet()){
            //RequestTransactionWrapper tmpWrapper=new RequestTransactionWrapper();
            entry.getValue().forEach(t-> {
                if (t.getDebit() > 0 && (this.sourceUserName == null || this.sourceUserPersonalName == null)) {
                    this.sourceUserName = BaseUtils.unreadableString(t.getAccountUserName(),4,4,'*');
                    this.sourceUserPersonalName =  (!Utils.isStringSafeEmpty(Utils.getNullStringAsEmpty(t.getAccountUserFirstName()) + Utils.getNullStringAsEmpty(t.getAccountUserLastName())) ? (Utils.getNullStringAsEmpty(t.getAccountUserFirstName()) + " " + Utils.getNullStringAsEmpty(t.getAccountUserLastName())) : "");
                }
                List<GeneralKeyValue> result=this.generateFinancialDetailsRow(t);
                if(result!=null && result.size()>0)
                    financialDetails.add(result);
            });
        }

        Map<String, List<RequestTransactionWrapper>> destRequestWrapperMap = this.getRequestTransactionWrappers().stream().filter(t-> t.getCredit()>0 && EOperation.CHARGE.getId()!= t.getOperationTypeId()).collect(Collectors.groupingBy(RequestTransactionWrapper::getAccountUserName));
        for (Map.Entry<String,List<RequestTransactionWrapper>> entry : destRequestWrapperMap.entrySet()) {
            RequestTransactionWrapper tmpWrapper=new RequestTransactionWrapper();
            entry.getValue().forEach(t-> {
                EAccountCategory eAccountCategory=EAccountCategory.valueOf(t.getAccountCategoryId().intValue());
                if(eAccountCategory==EAccountCategory.GLOBAL) {
                    if (tmpWrapper.getAccountId() == null) {
                        tmpWrapper.setAccountId(t.getAccountId());
                        tmpWrapper.setAccountName(t.getAccountName());
                        tmpWrapper.setAccountUserFirstName(t.getAccountUserFirstName());
                        tmpWrapper.setAccountUserLastName(t.getAccountUserLastName());
                        tmpWrapper.setAccountUserId(t.getAccountUserId());
                        tmpWrapper.setAccountCategoryId(eAccountCategory.getId().longValue());
                        tmpWrapper.setAccountUserName(BaseUtils.unreadableString(t.getAccountUserName(), 4, 4, '*'));
                        tmpWrapper.setDescription(t.getDescription());
                        tmpWrapper.setCreateDate(t.getCreateDate());
                        tmpWrapper.setCreateBy(t.getCreateBy());
                        tmpWrapper.setModifyDate(t.getModifyDate());
                        tmpWrapper.setModifyBy(t.getModifyBy());
                        tmpWrapper.setOperationTypeId(t.getOperationTypeId());
                        tmpWrapper.setTransactionId(t.getTransactionId());
                        if (t.getTransactionTypeId() != null) {
                            tmpWrapper.setTransactionTypeId(t.getTransactionTypeId());
                            tmpWrapper.setTransactionTypeCaption(t.getTransactionTypeCaption());
                        }
                        tmpWrapper.setStatus(t.getStatus());
                        tmpWrapper.setUserCreditId(t.getUserCreditId());
                        tmpWrapper.setUserCreditName(t.getUserCreditName());
                    }
                    tmpWrapper.setCredit(tmpWrapper.getCredit() + t.getCredit());
                }
                List<GeneralKeyValue> result=this.generateFinancialDetailsRow(t);
                if(result!=null && result.size()>0)
                    financialDetails.add(result);
            });
            if(!this.getSelf() && tmpWrapper!=null && tmpWrapper.getAccountId()!=null) {
                List<GeneralKeyValue> result=this.generateOtherDetailsRow(tmpWrapper);
                if(result!=null && result.size()>0)
                    otherDetails.add(result);
            }
        }
    }
    private  List<GeneralKeyValue> generateOtherDetailsRow(RequestTransactionWrapper rt){
        List<GeneralKeyValue> result=new ArrayList<>();
        if(rt!=null && EAccountCategory.valueOf(rt.getAccountCategoryId().intValue())==EAccountCategory.GLOBAL) {
           String name = (!Utils.isStringSafeEmpty(Utils.getNullStringAsEmpty(rt.getAccountUserFirstName()) + Utils.getNullStringAsEmpty(rt.getAccountUserLastName())) ? (Utils.getNullStringAsEmpty(rt.getAccountUserFirstName()) + " " + Utils.getNullStringAsEmpty(rt.getAccountUserLastName())) : "");
           if (rt.getDebit() > 0) {
               result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.source")).value(/*rt.getUserCreditName()!=null?rt.getUserCreditName():*/rt.getAccountName()).build());
               if(!BaseUtils.isStringSafeEmpty(name) && rt.getUserCreditId()==null)
                  result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.info")).value(!Utils.isStringSafeEmpty(name) ? name : rt.getAccountUserName()).build());
               result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.withdraw_amount")).value(Utils.formatMoney(rt.getDebit(), false, true)).build());
           }else if (rt.getCredit() > 0 /*&& rt.getOperationTypeId()!= EOperation.MOBILE_CHARGE.getId() && rt.getOperationTypeId()!= EOperation.INTERNET_CHARGE.getId()
                         && (rt.getTransactionTypeId()==null || (rt.getTransactionTypeId()!=null && rt.getTransactionTypeId()!= ETransactionType.COMMISSION.getId()))*/) {
               result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.destination")).value(rt.getAccountUserName()).build());
               if(!BaseUtils.isStringSafeEmpty(name))
                   result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.info")).value(!Utils.isStringSafeEmpty(name) ? name : rt.getAccountUserName()).build());
               result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.deposit_amount")).value(Utils.formatMoney(rt.getCredit(), false, true)).build());
           }
        }
        return result;
    }

    private  List<GeneralKeyValue> generateFinancialDetailsRow(RequestTransactionWrapper rt){
        List<GeneralKeyValue> result=new ArrayList<>();
        if(rt!=null && EAccountCategory.valueOf(rt.getAccountCategoryId().intValue())==EAccountCategory.GLOBAL) {
            if (rt.getDebit() > 0) {
                //result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.source_id")).value(rt.getUserCreditId()!=null?rt.getUserCreditId().toString():rt.getAccountId().toString()).build());
                result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.source_name")).value(rt.getUserCreditName()!=null?rt.getUserCreditName():rt.getAccountName()).build());
                result.add(GeneralKeyValue.builder().key((!Utils.isStringSafeEmpty(rt.getTransactionTypeCaption())? rt.getTransactionTypeCaption()+ "-":"") + Utils.getMessageResource("common.operationRequest.withdraw_amount")).value(Utils.formatMoney(rt.getDebit(), false, true)).build());
            }else if (rt.getCredit() > 0  /*&& rt.getOperationTypeId()!= EOperation.MOBILE_CHARGE.getId() && rt.getOperationTypeId()!= EOperation.INTERNET_CHARGE.getId()
                     && (rt.getTransactionTypeId()==null || (rt.getTransactionTypeId()!=null && rt.getTransactionTypeId()== ETransactionType.COMMISSION.getId()))*/) {
                //result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.destination_id")).value(rt.getAccountId().toString()).build());
                result.add(GeneralKeyValue.builder().key(Utils.getMessageResource("common.operationRequest.destination_name")).value(rt.getAccountName()).build());
                result.add(GeneralKeyValue.builder().key(Utils.getMessageResource((EOperation.CHARGE.getId()== rt.getOperationTypeId() ? "eOperation.charge" : "common.operationRequest.deposit_amount"))).value(Utils.formatMoney(rt.getCredit(), false, true)).build());
            }
        }
        return result;
    }
}
