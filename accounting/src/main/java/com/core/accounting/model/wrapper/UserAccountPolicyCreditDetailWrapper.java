package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.ETransactionSourceType;
import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.MerchantRepository;
import com.core.accounting.repository.OperationTypeRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.common.util.Utils;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.util.json.JsonDateSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserAccountPolicyCreditDetailWrapper implements Serializable {

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private Long id;
    private Long userAccountPolicyProfileId;
    private Long accountCreditDetailId;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private Long userId;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private String userName;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private String name;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private String family;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private Double creditAmount;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private Double usedAmount;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private Double blockAmount;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private Boolean active;
    private Date expireDate;
    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    private String issuerName;
    private Long createBy;
    private Date createDate;
    private Long modifyBy;
    private Date modifyDate;

    private List<MerchantWrapper> merchantWrappers=null;


    public UserAccountPolicyCreditDetailWrapper(Long id, Long userAccountPolicyProfileId, Long accountCreditDetailId, Long userId, String userName, String name, String family, Double creditAmount, Double usedAmount, Double blockAmount, Boolean active,Date expireDate,String issuerName, Long createBy, Date createDate, Long modifyBy, Date modifyDate) {
        this.id = id;
        this.userAccountPolicyProfileId = userAccountPolicyProfileId;
        this.accountCreditDetailId = accountCreditDetailId;
        this.userId = userId;
        this.userName = userName;
        this.name = name;
        this.family = family;
        this.creditAmount = creditAmount;
        this.usedAmount = usedAmount;
        this.blockAmount = blockAmount;
        this.active = active;
        this.expireDate= expireDate;
        this.issuerName= issuerName;
        this.createBy = createBy;
        this.createDate = createDate;
        this.modifyBy = modifyBy;
        this.modifyDate = modifyDate;
    }

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


    public Double getBalance(){
        return (this.getCreditAmount()-this.getUsedAmount());
    }

    public Double getWithdrawBalance(){
        return (this.getBalance()-this.getBlockAmount())>0 ? (this.getBalance()-this.getBlockAmount()): 0d;
    }

    public String getName() {
        return name==null ? "" : name;
    }

    public String getFamily() {
        return family==null ? "" : family;
    }

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    public List<MerchantWrapper> getMerchantWrappers() {
        if(merchantWrappers==null) {
            if(Utils.hasRoleType(ERoleType.ADMIN))
               merchantWrappers = ((MerchantRepository) AccountingRepositoryFactory.getRepository(ERepository.MERCHANT)).findMerchantWrapperInfoByUserAccountPolicyCreditDetailId(this.getId());
            else
               merchantWrappers = ((MerchantRepository) AccountingRepositoryFactory.getRepository(ERepository.MERCHANT)).findMerchantWrapperInfoByUserAccountPolicyCreditDetailIdAndUserId(this.getId(), BaseUtils.getCurrentUserId());
        }
        return merchantWrappers;
    }
}
