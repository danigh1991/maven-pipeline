package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name= AccountPolicyProfile.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AccountPolicyProfile.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "apl_id")),
        @AttributeOverride(name = "version", column = @Column(name = "apl_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "apl_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "apl_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "apl_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "apl_modify_by"))
})
public class AccountPolicyProfile extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account_policy_profile";

    @JsonView(AccountJsonView.AccountPolicyProfileDetail.class)
    @Column(name="apl_usr_id")
    private Long userId;
    @JsonView(AccountJsonView.AccountPolicyProfileShortList.class)
    @Column(name="apl_name",nullable = false)
    private String name;
    @JsonView(AccountJsonView.AccountPolicyProfileDetail.class)
    @Column(name="apl_description")
    private String description;
    @JsonView(AccountJsonView.AccountPolicyProfileShortList.class)
    @Column(name="apl_active",nullable = false)
    private Boolean active;

    @JsonView(AccountJsonView.AccountPolicyProfileList.class)
    @OneToMany(targetEntity = AccountPolicyProfileOperationType.class, mappedBy = "accountPolicyProfile", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("accountPolicyProfile")
    private List<AccountPolicyProfileOperationType> accountPolicyProfileOperationTypes = new ArrayList<>();

    @OneToMany(targetEntity = UserAccountPolicyProfile.class, mappedBy = "accountPolicyProfile", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("accountPolicyProfile")
    private List<UserAccountPolicyProfile> userAccountPolicyProfile = new ArrayList<>();

    @OneToMany(targetEntity = AccountType.class, mappedBy = "defaultAccountPolicyProfile" ,fetch =FetchType.LAZY)
    @JsonView(AccountJsonView.AccountList.class)
    @JsonIgnoreProperties("defaultAccountPolicyProfile")
    private List<AccountType> accountTypes;

    @JsonView(AccountJsonView.AccountPolicyProfileDetail.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apl_act_id", referencedColumnName = "act_id", foreignKey = @ForeignKey(name = "FK_apl_act_id"))
    @JsonIgnoreProperties("accountPolicyProfiles")
    private AccountType accountType;


    @OneToMany(targetEntity = AccountCreditDetail.class, mappedBy = "defaultAccountPolicyProfile", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("defaultAccountPolicyProfile")
    private List<AccountCreditDetail> accountCreditDetails = new ArrayList<>();


}
