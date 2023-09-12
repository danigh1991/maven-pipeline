package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EAccountType;
import com.core.accounting.model.enums.ETransactionSourceType;
import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.OperationTypeRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.datamodel.model.wrapper.AbstractMultiLingualWrapper;
import com.core.datamodel.util.json.JsonDateSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.annotations.MultiLingual;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AccountWrapper extends AbstractMultiLingualWrapper {

    @JsonView(AccountJsonView.AccountList.class)
    private Long id;
    @JsonView(AccountJsonView.AccountList.class)
    private String name;
    @JsonView(AccountJsonView.AccountDetails.class)
    private Long userId;
    @JsonView(AccountJsonView.AccountList.class)
    private Boolean main;
    @JsonView(AccountJsonView.AccountList.class)
    private Long accountTypeId;
    @JsonView(AccountJsonView.AccountList.class)
    private String accountTypeName;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.ACCOUNT_TYPE,fieldName = "description", targetIdFieldName ="accountTypeId")
    private String accountTypeDesc;
    @JsonView(AccountJsonView.AccountList.class)
    private Integer status;
    @JsonView(AccountJsonView.AccountList.class)
    private Double balance;
    @JsonView(AccountJsonView.AccountList.class)
    private Double block;
    @JsonView(AccountJsonView.AccountDetails.class)
    private Double capacity;
    @JsonView(AccountJsonView.AccountList.class)
    private String description;
    @JsonView(AccountJsonView.AccountDetails.class)
    private String color;
    @JsonView(AccountJsonView.AccountDetails.class)
    private Long theme_id;
    @JsonView(AccountJsonView.AccountDetails.class)
    private Date createDate;
    @JsonView(AccountJsonView.AccountDetails.class)
    private Date modifyDate;
    private Long accountCreditId;
    @MultiLingual(destinationTable = ERepository.MultiLingualAccount,targetType = ERepository.ACCOUNT_CREDIT,fieldName = "title", targetIdFieldName ="accountCreditId")
    private String accountCreditTitle;
    @MultiLingual(destinationTable = ERepository.MultiLingualAccount,targetType = ERepository.ACCOUNT_CREDIT,fieldName = "description", targetIdFieldName ="accountCreditId")
    private String accountCreditDescription;
    private Integer  creditType;
    private Double creditRate;
    private Double creditAmount;
    private Boolean creditActive;
    private Date expireDate;
    @JsonView(AccountJsonView.AccountDetails.class)
    private Long accountPolicyProfileId;
    private Integer viewType;
    @JsonView(AccountJsonView.AccountList.class)
    private Long userCreditId;
    private Long version;
    private List<OperationTypeWrapper>  operations=null;
    private List<Long>  merchantLimits;

    @JsonView(AccountJsonView.AccountList.class)
    private List<AccountWrapper> innerAccount=new ArrayList<>();

    public AccountWrapper(Long id, String name, Long userId,Boolean main, Long accountTypeId, String accountTypeName, String accountTypeDesc, Integer status, Double balance, Double block,Double capacity, String description, String color, Long theme_id, Date createDate, Date modifyDate, Long accountCreditId, String accountCreditTitle, String accountCreditDescription, Integer creditType, Double creditRate, Double creditAmount, Boolean creditActive, Date expireDate,Long accountPolicyProfileId,Integer viewType,Long userCreditId,Long version) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.main = main;
        this.accountTypeId = accountTypeId;
        this.accountTypeName = accountTypeName;
        this.accountTypeDesc = accountTypeDesc;
        this.status = status;
        this.balance = balance;
        this.block = block;
        this.capacity = capacity;
        this.description = description;
        this.color = color;
        this.theme_id = theme_id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.accountCreditId = accountCreditId;
        this.accountCreditTitle = accountCreditTitle;
        this.accountCreditDescription = accountCreditDescription;
        this.creditType = creditType;
        this.creditRate = creditRate;
        this.creditAmount = creditAmount;
        this.creditActive = creditActive;
        this.expireDate = expireDate;
        this.accountPolicyProfileId = accountPolicyProfileId;
        this.viewType=viewType;
        this.userCreditId=userCreditId;
        this.version=version;

        this.prepareMultiLingual();
    }


    @JsonView(AccountJsonView.AccountList.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.AccountList.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @JsonView(AccountJsonView.AccountList.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }

    @JsonView(AccountJsonView.AccountList.class)
    public String getStatusDesc() {
        if (this.status == 1)
            return BaseUtils.getMessageResource("global.status.enable");
        else
            return BaseUtils.getMessageResource("global.status.disable");
    }

    public List<OperationTypeWrapper> getOperations() {
        if (operations == null) {
            operations=((OperationTypeRepository) AccountingRepositoryFactory.getRepository(ERepository.OPERATION_TYPE)).findOperationTypeWrappersByAccountId(this.getId(),BaseUtils.getCurrentUserId(), ETransactionSourceType.WALLET.getId());
        }
        return operations;
    }

    @JsonView(AccountJsonView.AccountList.class)
    public String getAccountTypeCaption(){
        return EAccountType.valueOf(this.getAccountTypeId()).getCaption();
    }

    @JsonView(AccountJsonView.AccountList.class)
    public Double getAvailableBalance() {
        return this.getBalance()-this.getBlock();
    }


}
