package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.MerchantLimitWrapper;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "accountCreditMerchantLimitWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = MerchantLimitWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type =Long.class),
                                        @ColumnResult(name = "type", type =Integer.class),
                                        @ColumnResult(name = "userName", type =String.class),
                                        @ColumnResult(name = "name", type =String.class),
                                        @ColumnResult(name = "family", type =String.class),
                                        @ColumnResult(name = "createBy", type =Long.class),
                                        @ColumnResult(name = "createDate", type = Date.class),
                                        @ColumnResult(name = "modifyBy", type =Long.class),
                                        @ColumnResult(name = "modifyDate", type =Date.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "AccountCreditMerchantLimit.findAccountCreditMerchantLimitWrappersById",
                query = "select cml.cml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_user_group g on (cml.cml_type=1 and cml.cml_target_id=g.usg_id)\n" +
                        "where cml.cml_id=:accountCreditMerchantLimitId \n" +
                        "union \n" +
                        "select cml.cml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_user u on (cml.cml_type=2 and cml.cml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id)\n" +
                        "where cml.cml_id=:accountCreditMerchantLimitId",
                resultSetMapping = "accountCreditMerchantLimitWrapperMapping"),
        @NamedNativeQuery(name = "AccountCreditMerchantLimit.findAccountCreditMerchantLimitWrappersByIdAndUserId",
                query = "select cml.cml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_account a on (a.acc_id=acd.acd_acc_id) \n" +
                        "inner join sc_user_group g on (cml.cml_type=1 and cml.cml_target_id=g.usg_id)\n" +
                        "where cml.cml_id=:accountCreditMerchantLimitId and a.acc_usr_id=:userId \n" +
                        "union \n" +
                        "select cml.cml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_account a on (a.acc_id=acd.acd_acc_id) \n" +
                        "inner join sc_user u on (cml.cml_type=2 and cml.cml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id)\n" +
                        "where cml.cml_id=:accountCreditMerchantLimitId and a.acc_usr_id=:userId",
                resultSetMapping = "accountCreditMerchantLimitWrapperMapping"),

        @NamedNativeQuery(name = "AccountCreditMerchantLimit.findAccountCreditMerchantLimitWrappersByAccountCreditDetailId",
                query = "select cml.cml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_user_group g on (cml.cml_type=1 and cml.cml_target_id=g.usg_id)\n" +
                        "where acd.acd_id=:accountCreditDetailId\n" +
                        "union \n" +
                        "select cml.cml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_user u on (cml.cml_type=2 and cml.cml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id and m.mrc_active=1)\n" +
                        "where acd.acd_id=:accountCreditDetailId",
                resultSetMapping = "accountCreditMerchantLimitWrapperMapping"),
        @NamedNativeQuery(name = "AccountCreditMerchantLimit.findAccountCreditMerchantLimitWrappersByAccountCreditDetailIdAndUserId",
                query = "select cml.cml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_account a on (a.acc_id=acd.acd_acc_id) \n" +
                        "inner join sc_user_group g on (cml.cml_type=1 and cml.cml_target_id=g.usg_id)\n" +
                        "where acd.acd_id=:accountCreditDetailId  and a.acc_usr_id=:userId \n" +
                        "union \n" +
                        "select cml.cml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       cml.cml_create_by as createBy,cml.cml_create_date as createDate,\n" +
                        "       cml.cml_modify_by as modifyBy,cml.cml_modify_date as modifyDate \n" +
                        "from sc_account_credit_merchant_limit cml\n" +
                        "inner join sc_account_credit_detail acd on (acd.acd_id=cml.cml_acd_id) \n" +
                        "inner join sc_account a on (a.acc_id=acd.acd_acc_id) \n" +
                        "inner join sc_user u on (cml.cml_type=2 and cml.cml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id and m.mrc_active=1)\n" +
                        "where acd.acd_id=:accountCreditDetailId and a.acc_usr_id=:userId",
                resultSetMapping = "accountCreditMerchantLimitWrapperMapping")
})
@Table(name= AccountCreditMerchantLimit.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AccountCreditMerchantLimit.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cml_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cml_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cml_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cml_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cml_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cml_modify_by"))
})
public class AccountCreditMerchantLimit extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account_credit_merchant_limit";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cml_acd_id",nullable = false,referencedColumnName = "acd_id", foreignKey = @ForeignKey(name = "FK_cml_acd_id"))
    @JsonIgnoreProperties("accountMerchantLimits")
    private AccountCreditDetail accountCreditDetail;

    @JsonView(AccountJsonView.MerchantLimitView.class)
    @Column(name="cml_type",nullable = false)
    private Integer type; //1=Group , 2=User

    @JsonView(AccountJsonView.MerchantLimitView.class)
    @Column(name="cml_target_id",nullable = false)
    private Long targetId;


}
