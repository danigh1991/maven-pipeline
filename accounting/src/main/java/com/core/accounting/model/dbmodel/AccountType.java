package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
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
@Table(name = AccountType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AccountType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "act_id")),
        @AttributeOverride(name = "version", column = @Column(name = "act_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "act_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "act_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "act_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "act_modify_by"))
})
public class AccountType extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account_type";

    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(MyJsonView.AccountTypeList.class)
    @Column(name = "act_id")
    private Long id;*/

    @Column(name = "act_name",nullable = false)
    @JsonView({AccountJsonView.AccountTypeList.class,AccountJsonView.MultiLingual.class})
    private String name;

    @Column(name = "act_desc")
    @JsonView({AccountJsonView.AccountTypeList.class,AccountJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.ACCOUNT_TYPE)
    private String description;

    @Column(name = "act_nature",nullable = false)
    @JsonView(AccountJsonView.AccountTypeList.class)
    private Integer nature;

    @Column(name = "act_active",nullable = false)
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private Boolean active;

    @Column(name = "act_code_str")
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private String codeStr;


    @Column(name = "act_calc_on_settlement",nullable = false)
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private Boolean calcOnSettlement;


    @OneToMany(targetEntity = Account.class, mappedBy = "accountType",fetch =FetchType.LAZY)
    @JsonIgnoreProperties("accountType")
    private List<Account> accounts = new ArrayList<>();


    @Column(name = "act_max_count",nullable = false)
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private Integer maxCount;

    @Column(name = "act_max_amount",nullable = false)
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private Double maxAmount;

    @Column(name = "act_usage_type",nullable = false)
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private Long usageType;  //	1=self, 2=other, 3=both

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_apl_id",nullable = false,referencedColumnName = "apl_id", foreignKey = @ForeignKey(name = "FK_act_apl_id"))
    @JsonIgnoreProperties("accountTypes")
    private AccountPolicyProfile defaultAccountPolicyProfile;

    @OneToMany(targetEntity = AccountPolicyProfile.class, mappedBy = "accountType" ,fetch =FetchType.LAZY)
    @JsonIgnoreProperties("accountType")
    private List<AccountPolicyProfile> accountPolicyProfiles = new ArrayList<>();

    /*@Column(name = "act_base_type",nullable = false)
    @JsonView(AccountJsonView.AccountTypeDetails.class)
    private Long baseType;  //	1=PERSONAL, 2=CREDITABLE, 3=SHAREABLE*/

}
