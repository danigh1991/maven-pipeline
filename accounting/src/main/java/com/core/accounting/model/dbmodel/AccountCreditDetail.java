package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.util.json.JsonDateShortSerializer;
import com.core.model.annotations.MultiLingual;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name= AccountCreditDetail.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AccountCreditDetail.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "acd_id")),
        @AttributeOverride(name = "version", column = @Column(name = "acd_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "acd_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "acd_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "acd_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "acd_modify_by"))
})
public class AccountCreditDetail extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account_credit_detail";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acd_acc_id",referencedColumnName = "acc_id",nullable = false,foreignKey = @ForeignKey(name = "FK_acd_acc_id"))
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    @JsonIgnoreProperties("accountCreditDetails")
    private Account account;

    @Column(name="acd_title",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualAccount,targetType = ERepository.ACCOUNT_CREDIT)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private String title;

    @Column(name="acd_description",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualAccount,targetType = ERepository.ACCOUNT_CREDIT)
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACD_DEFAULT_APL_ID",referencedColumnName = "APL_ID", foreignKey = @ForeignKey(name = "FK_ACD_DEFAULT_APL_ID"))
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private AccountPolicyProfile defaultAccountPolicyProfile;

    @Column(name="acd_credit_type",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Integer  creditType;

    @Column(name="acd_view_type",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Integer viewType=1;//1=single view,2=group view

    @Column(name="acd_settlement_period")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Integer settlementPeriod;
    @Column(name="acd_settlement_period_type")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Integer settlementPeriodType;

    @Column(name="acd_credit_amount",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Double creditAmount=0d;

    @Column(name="acd_credit_assign",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Double creditAssign=0d;

    @Column(name="acd_credit_amount_per_user",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Double creditAmountPerUser;

    @Column(name="acd_min_credit_amount",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Double minCreditAmount;

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    @Column(name="acd_block",nullable = false)
    private Double block=0d;

    @Column(name="acd_active",nullable = false)
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Boolean active;

    @Column(name="acd_expire_date")
    @JsonView(AccountJsonView.AccountCreditDetailList.class)
    private Date expireDate;

    @Column(name="acd_spending_restrictions")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Boolean spendingRestrictions;

    @Column(name="acd_rate_restrictions")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Double rateRestrictions;

    @Column(name="acd_max_amount_restrictions")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Double maxAmountRestrictions;

    @Column(name="acd_interest_rate")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Double interestRate;

    @Column(name="acd_forfeit_rate")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private Double forfeitRate;

    @Column(name="acd_issuer")
    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    private String issuer;

    @OneToMany(targetEntity = UserAccountPolicyCreditDetail.class, mappedBy = "accountCreditDetail", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("accountCreditDetail")
    private List<UserAccountPolicyCreditDetail> userAccountPolicyCreditDetails = new ArrayList<>();

    @OneToMany(targetEntity = AccountCreditMerchantLimit.class, mappedBy = "accountCreditDetail", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("accountCreditDetail")
    private List<AccountCreditMerchantLimit> accountCreditMerchantLimits = new ArrayList<>();

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    @JsonSerialize(using = JsonDateShortSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    public Double getRemainCreditAmount(){
        return this.getCreditAmount() - this.getCreditAssign();
    }

    public String getFormatRemainCreditAmount(){
        return BaseUtils.formatMoney(this.getRemainCreditAmount());
    }

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    public String getFormatCreditAmount(){
        return BaseUtils.formatMoney(this.getCreditAmount());
    }

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    public String getFormatCreditAmountPerUser(){
        return BaseUtils.formatMoney(this.getCreditAmountPerUser());
    }

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    public String getFormatCreditAssign(){
        return BaseUtils.formatMoney(this.getCreditAssign());
    }

    @JsonView(AccountJsonView.AccountCreditDetailDetails.class)
    public String getFormatMinCreditAmount(){
        return BaseUtils.formatMoney(this.getMinCreditAmount());
    }
}
