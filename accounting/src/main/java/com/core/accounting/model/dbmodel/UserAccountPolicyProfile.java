package com.core.accounting.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name= UserAccountPolicyProfile.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserAccountPolicyProfile.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "uap_id")),
        @AttributeOverride(name = "version", column = @Column(name = "uap_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "uap_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "uap_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "uap_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "uap_modify_by"))
})
public class UserAccountPolicyProfile extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_account_policy_profile";

    @Column(name="uap_usr_id",nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uap_acc_id",nullable = false,referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "FK_uap_acc_id"))
    @JsonIgnoreProperties("userAccountPolicyProfile")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uap_apl_id",nullable = false,referencedColumnName = "apl_id", foreignKey = @ForeignKey(name = "FK_uap_apl_id"))
    @JsonIgnoreProperties("userAccountPolicyProfile")
    private AccountPolicyProfile accountPolicyProfile;

    @OneToMany(targetEntity = UserAccountPolicyCreditDetail.class, mappedBy = "userAccountPolicyProfile", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("userAccountPolicyProfile")
    private List<UserAccountPolicyCreditDetail> userAccountPolicyCreditDetails = new ArrayList<>();

}
