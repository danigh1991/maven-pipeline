package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.common.util.Utils;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name= AccountPolicyProfileOperationType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AccountPolicyProfileOperationType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "apt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "apt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "apt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "apt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "apt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "apt_modify_by"))
})
public class AccountPolicyProfileOperationType extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account_policy_profile_operation_type";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_apl_id",nullable = false,referencedColumnName = "apl_id", foreignKey = @ForeignKey(name = "FK_apt_apl_id"))
    @JsonIgnoreProperties("accountPolicyProfileOperationTypes")
    private AccountPolicyProfile accountPolicyProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_opt_id",nullable = false,referencedColumnName = "opt_id", foreignKey = @ForeignKey(name = "FK_apt_opt_id"))
    @JsonIgnoreProperties("accountPolicyProfileOperationTypes")
    private OperationType operationType;

    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    @Column(name = "apt_min_amount")
    private Double minAmount;
    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    @Column(name = "apt_max_amount")
    private Double maxAmount;
    @Column(name = "apt_max_amount_duration_type")
    private Integer maxAmountDurationType;
    @Column(name = "apt_max_amount_duration")
    private Integer maxAmountDuration;
    /*@Column(name = "apt_wage_type")
    private Integer wageType;
    @Column(name = "apt_wage_rate")
    private Double wageRate;
    @Column(name = "apt_wage_amount")
    private Double wageAmount;*/
    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    @Column(name = "apt_default_amounts",nullable = false)
    private String defaultAmounts;
    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    @Column(name = "apt_global_max_daily_amount")
    private Double globalMaxDailyAmount;
    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    @Column(name = "apt_order")
    private Integer order;

    @Column(name = "apt_notify")
    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    private Boolean notify;

    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    public Long getOperationTypeId(){
        return this.getOperationType().getId();
    }

    @Transient
    private transient List<Double> defaultAmountList;

    @JsonView(AccountJsonView.AccountPolicyProfileOperationTypeList.class)
    public List<Double> getDefaultAmountList() {
        if(defaultAmountList==null) {
            if (Utils.isStringSafeEmpty(this.defaultAmounts))
                defaultAmountList= new ArrayList<>();
            else
                defaultAmountList= Arrays.stream(defaultAmounts.split(",")).map(Double::parseDouble).collect(Collectors.toList());
        }
        return defaultAmountList;
    }

}
