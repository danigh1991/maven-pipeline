package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.AccountWrapper;
import com.core.accounting.model.wrapper.UserAccountPolicyCreditDetailWrapper;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "userAccountPolicyCreditDetailWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = UserAccountPolicyCreditDetailWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type =Long.class),
                                        @ColumnResult(name = "userAccountPolicyProfileId", type =Long.class),
                                        @ColumnResult(name = "accountCreditDetailId", type =Long.class),
                                        @ColumnResult(name = "userId", type =Long.class),
                                        @ColumnResult(name = "userName", type =String.class),
                                        @ColumnResult(name = "name", type =String.class),
                                        @ColumnResult(name = "family", type =String.class),
                                        @ColumnResult(name = "creditAmount", type =Double.class),
                                        @ColumnResult(name = "usedAmount", type =Double.class),
                                        @ColumnResult(name = "blockAmount", type =Double.class),
                                        @ColumnResult(name = "active", type =Boolean.class),
                                        @ColumnResult(name = "expireDate", type =Date.class),
                                        @ColumnResult(name = "issuerName", type =String.class),
                                        @ColumnResult(name = "createBy", type =Long.class),
                                        @ColumnResult(name = "createDate", type =Date.class),
                                        @ColumnResult(name = "modifyBy", type =Long.class),
                                        @ColumnResult(name = "modifyDate", type =Date.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "UserAccountPolicyCreditDetail.findUserAccountPolicyCreditDetailWrappersById",
                query = "select uac.uac_id as id,uac.uac_uap_id as userAccountPolicyProfileId, uac.uac_acd_id as accountCreditDetailId, \n" +
                        "       u.usr_id as userId, u.usr_username as userName,u.usr_first_name as name,u.usr_last_name as family,\n" +
                        "       acd.acd_credit_amount_per_user as creditAmount,uac.uac_used_amount as usedAmount,uac.uac_block as blockAmount,\n" +
                        "       uac.uac_active as active ,acd.acd_expire_date as expireDate, nvl(m.mrc_name,nvl(mu.usr_last_name,mu.usr_username)) as issuerName,\n" +
                        "       uac.uac_create_by as createBy, uac.uac_create_date as createDate,uac.uac_modify_by as modifyBy ,uac.uac_modify_date as modifyDate \n" +
                        "from sc_user_account_policy_credit_detail uac \n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id)\n" +
                        "inner join sc_account a on (acd.acd_acc_id=a.acc_id)  \n"+
                        "inner join sc_user mu on (mu.usr_id=a.acc_usr_id) \n"+
                        "Left join sc_merchant m on (mu.usr_id=m.mrc_usr_id and m.mrc_active=1) \n"+
                        "inner join sc_user_account_policy_profile uap on (uac.uac_uap_id=uap.uap_id)\n" +
                        "inner join sc_user u on (u.usr_id=uap.uap_usr_id)\n" +
                        "where uac.uac_id=:userAccountPolicyCreditDetailId",
                resultSetMapping = "userAccountPolicyCreditDetailWrapperMapping"),
        @NamedNativeQuery(name = "UserAccountPolicyCreditDetail.findUserAccountPolicyCreditDetailWrappersByIdAndUserId",
                query = "select uac.uac_id as id,uac.uac_uap_id as userAccountPolicyProfileId, uac.uac_acd_id as accountCreditDetailId, \n" +
                        "       u.usr_id as userId, u.usr_username as userName,u.usr_first_name as name,u.usr_last_name as family,\n" +
                        "       acd.acd_credit_amount_per_user as creditAmount,uac.uac_used_amount as usedAmount,uac.uac_block as blockAmount,\n" +
                        "       uac.uac_active as active ,acd.acd_expire_date as expireDate , nvl(m.mrc_name,nvl(mu.usr_last_name,mu.usr_username)) as issuerName,\n" +
                        "       uac.uac_create_by as createBy, uac.uac_create_date as createDate,uac.uac_modify_by as modifyBy ,uac.uac_modify_date as modifyDate \n" +
                        "from sc_user_account_policy_credit_detail uac \n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id)\n" +
                        "inner join sc_account a on (acd.acd_acc_id=a.acc_id)  \n"+
                        "inner join sc_user mu on (mu.usr_id=a.acc_usr_id) \n"+
                        "Left join sc_merchant m on (mu.usr_id=m.mrc_usr_id and m.mrc_active=1) \n"+
                        "inner join sc_user_account_policy_profile uap on (uac.uac_uap_id=uap.uap_id)\n" +
                        "inner join sc_user u on (u.usr_id=uap.uap_usr_id)\n" +
                        "where uac.uac_id=:userAccountPolicyCreditDetailId and uap.uap_usr_id=:userId",
                resultSetMapping = "userAccountPolicyCreditDetailWrapperMapping"),
        @NamedNativeQuery(name = "UserAccountPolicyCreditDetail.findUserAccountPolicyCreditDetailWrappersByAccountCreditDetailId",
                query = "select uac.uac_id as id,uac.uac_uap_id as userAccountPolicyProfileId, uac.uac_acd_id as accountCreditDetailId, \n" +
                        "       u.usr_id as userId, u.usr_username as userName,u.usr_first_name as name,u.usr_last_name as family,\n" +
                        "       acd.acd_credit_amount_per_user as creditAmount,uac.uac_used_amount as usedAmount,uac.uac_block as blockAmount,\n" +
                        "       uac.uac_active as active ,acd.acd_expire_date as expireDate , nvl(m.mrc_name,nvl(mu.usr_last_name,mu.usr_username)) as issuerName,\n" +
                        "       uac.uac_create_by as createBy, uac.uac_create_date as createDate, uac.uac_modify_by as modifyBy ,uac.uac_modify_date as modifyDate \n" +
                        "from sc_user_account_policy_credit_detail uac \n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id)\n" +
                        "inner join sc_account a on (acd.acd_acc_id=a.acc_id)  \n"+
                        "inner join sc_user mu on (mu.usr_id=a.acc_usr_id) \n"+
                        "Left  join sc_merchant m on (mu.usr_id=m.mrc_usr_id and m.mrc_active=1) \n"+
                        "inner join sc_user_account_policy_profile uap on (uac.uac_uap_id=uap.uap_id)\n" +
                        "inner join sc_user u on (u.usr_id=uap.uap_usr_id)\n" +
                        "where acd.acd_id=:accountCreditDetailId",
                resultSetMapping = "userAccountPolicyCreditDetailWrapperMapping"),
        @NamedNativeQuery(name = "UserAccountPolicyCreditDetail.findUserAccountPolicyCreditDetailWrappersByAccountCreditDetailIdAndUserId",
                query = "select uac.uac_id as id,uac.uac_uap_id as userAccountPolicyProfileId, uac.uac_acd_id as accountCreditDetailId, \n" +
                        "       u.usr_id as userId, u.usr_username as userName,u.usr_first_name as name,u.usr_last_name as family,\n" +
                        "       acd.acd_credit_amount_per_user as creditAmount,uac.uac_used_amount as usedAmount,uac.uac_block as blockAmount,\n" +
                        "       uac.uac_active as active ,acd.acd_expire_date as expireDate , nvl(m.mrc_name,nvl(mu.usr_last_name,mu.usr_username)) as issuerName,\n" +
                        "       uac.uac_create_by as createBy, uac.uac_create_date as createDate, uac.uac_modify_by as modifyBy ,uac.uac_modify_date as modifyDate \n " +
                        "from sc_user_account_policy_credit_detail uac \n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id)\n" +
                        "inner join sc_account a on (acd.acd_acc_id=a.acc_id)  \n"+
                        "inner join sc_user mu on (mu.usr_id=a.acc_usr_id) \n"+
                        "Left join sc_merchant m on (mu.usr_id=m.mrc_usr_id and m.mrc_active=1) \n"+
                        "inner join sc_user_account_policy_profile uap on (uac.uac_uap_id=uap.uap_id)\n" +
                        "inner join sc_user u on (u.usr_id=uap.uap_usr_id)\n" +
                        "where acd.acd_id=:accountCreditDetailId and a.acc_usr_id=:userId",
                resultSetMapping = "userAccountPolicyCreditDetailWrapperMapping")
})
@Table(name= UserAccountPolicyCreditDetail.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserAccountPolicyCreditDetail.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "uac_id")),
        @AttributeOverride(name = "version", column = @Column(name = "uac_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "uac_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "uac_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "uac_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "uac_modify_by"))
})
public class UserAccountPolicyCreditDetail extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_account_policy_credit_detail";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uac_uap_id",nullable = false,referencedColumnName = "uap_id", foreignKey = @ForeignKey(name = "FK_uac_uap_id"))
    @JsonIgnoreProperties("userAccountPolicyCreditDetails")
    private UserAccountPolicyProfile userAccountPolicyProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uac_acd_id",nullable = false,referencedColumnName = "acd_id", foreignKey = @ForeignKey(name = "FK_uac_acd_id"))
    @JsonIgnoreProperties("userAccountPolicyCreditDetails")
    private AccountCreditDetail accountCreditDetail;

/*    @Column(name="uac_usr_id",nullable = false)
    private Long userId;*/

    /*@Column(name="uac_credit_amount",nullable = false)
    private Double creditAmount;

    @Column(name="uac_expire_date")
    private Date expireDate;*/

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    @Column(name="uac_used_amount",nullable = false)
    private Double usedAmount=0d;

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    @Column(name="uac_active",nullable = false)
    private Boolean active=true;

    @JsonView(AccountJsonView.UserAccountPolicyCreditDetailView.class)
    @Column(name="uac_block",nullable = false)
    private Double block=0d;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "userAccountPolicyCreditDetail",fetch =FetchType.LAZY )
    @JsonIgnoreProperties("userAccountPolicyCreditDetail")
    private List<Transaction> transactions = new ArrayList<Transaction>();

    public Double getCreditAmount() {
        return this.getAccountCreditDetail().getCreditAmountPerUser();
    }


    public Double getBalance(){
        return (this.getCreditAmount()-this.getUsedAmount());
    }

    public Double getWithdrawBalance(){
        return (this.getBalance()-this.getBlock())>0 ? (this.getBalance()-this.getBlock()): 0d;
    }

}
