package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.MerchantLimitWrapper;
import com.core.accounting.model.wrapper.UserAccountPolicyCreditDetailWrapper;
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
                name = "accountMerchantLimitWrapperMapping",
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
        @NamedNativeQuery(name = "AccountMerchantLimit.findAccountMerchantLimitWrappersById",
                query = "select aml.aml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user_group g on (aml.aml_type=1 and aml.aml_target_id=g.usg_id)\n" +
                        "where  aml.aml_id=:accountMerchantLimitId\n" +
                        "union \n" +
                        "select aml.aml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate \n" +
                        "from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user u on (aml.aml_type=2 and aml.aml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id)\n" +
                        "where aml.aml_id=:accountMerchantLimitId",
                resultSetMapping = "accountMerchantLimitWrapperMapping"),
        @NamedNativeQuery(name = "AccountMerchantLimit.findAccountMerchantLimitWrappersByIdAndUserId",
                query = "select aml.aml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user_group g on (aml.aml_type=1 and aml.aml_target_id=g.usg_id)\n" +
                        "where  aml.aml_id=:accountMerchantLimitId and a.acc_usr_id=:userId\n" +
                        "union \n" +
                        "select aml.aml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate \n" +
                        "from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user u on (aml.aml_type=2 and aml.aml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id)\n" +
                        "where  aml.aml_id=:accountMerchantLimitId and a.acc_usr_id=:userId",
                resultSetMapping = "accountMerchantLimitWrapperMapping"),
        @NamedNativeQuery(name = "AccountMerchantLimit.findAccountMerchantLimitWrappersByAccountId",
                query = "select aml.aml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user_group g on (aml.aml_type=1 and aml.aml_target_id=g.usg_id)\n" +
                        "where a.acc_id=:accountId\n" +
                        "union \n" +
                        "select aml.aml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate \n" +
                        "from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user u on (aml.aml_type=2 and aml.aml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id)\n" +
                        "where a.acc_id=:accountId",
                resultSetMapping = "accountMerchantLimitWrapperMapping"),
        @NamedNativeQuery(name = "AccountMerchantLimit.findAccountMerchantLimitWrappersByAccountIdAndUserId",
                query = "select aml.aml_id as id,1 as type,null as userName,g.usg_name as name,null as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user_group g on (aml.aml_type=1 and aml.aml_target_id=g.usg_id)\n" +
                        "where a.acc_id=:accountId and a.acc_usr_id=:userId \n" +
                        "union \n" +
                        "select aml.aml_id as id,2 as type,u.usr_username as userName, null as name, m.mrc_name as family,\n" +
                        "       aml.aml_create_by as createBy,aml.aml_create_date as createDate,\n" +
                        "       aml.aml_modify_by as modifyBy,aml.aml_modify_date as modifyDate \n" +
                        "from sc_account_merchant_limit aml\n" +
                        "inner join sc_account a on (a.acc_id=aml.aml_acc_id) \n" +
                        "inner join sc_user u on (aml.aml_type=2 and aml.aml_target_id=u.usr_id)\n" +
                        "inner join sc_merchant m on (m.mrc_usr_id=u.usr_id)\n" +
                        "where a.acc_id=:accountId and a.acc_usr_id=:userId",
                resultSetMapping = "accountMerchantLimitWrapperMapping")
})
@Table(name= AccountMerchantLimit.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AccountMerchantLimit.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "aml_id")),
        @AttributeOverride(name = "version", column = @Column(name = "aml_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "aml_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "aml_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "aml_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "aml_modify_by"))
})
public class AccountMerchantLimit extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account_merchant_limit";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aml_acc_id",nullable = false,referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "FK_aml_acc_id"))
    @JsonIgnoreProperties("accountMerchantLimits")
    private Account account;

    @JsonView(AccountJsonView.MerchantLimitView.class)
    @Column(name="aml_type",nullable = false)
    private Integer type ; //1=Group , 2=User

    @JsonView(AccountJsonView.MerchantLimitView.class)
    @Column(name="aml_target_id",nullable = false)
    private Long targetId;

}
