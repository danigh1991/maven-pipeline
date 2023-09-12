package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.OperationTypeSummeryWrapper;
import com.core.accounting.model.wrapper.OperationTypeWrapper;
import com.core.common.util.Utils;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "operationTypeWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = OperationTypeWrapper.class,
                                columns = {
                                        @ColumnResult(name="id",type=Long.class),
                                        @ColumnResult(name="name",type=String.class),
                                        @ColumnResult(name="description",type=String.class),
                                        @ColumnResult(name="active",type=Boolean.class),
                                        @ColumnResult(name="code",type=Integer.class),
                                        @ColumnResult(name="operationType",type=char.class),
                                        @ColumnResult(name="minAmount",type=Double.class),
                                        @ColumnResult(name="maxAmount",type=Double.class),
                                        @ColumnResult(name="maxAmountDurationType",type=Integer.class),
                                        @ColumnResult(name="maxAmountDuration",type=Integer.class),
                                        @ColumnResult(name="wageType",type=Integer.class),
                                        @ColumnResult(name="wageRate",type=Double.class),
                                        @ColumnResult(name="wageAmount",type=Double.class),
                                        @ColumnResult(name="sourceType",type=Integer.class),
                                        @ColumnResult(name="defaultAmounts",type=String.class),
                                        @ColumnResult(name="globalMaxDailyAmount",type=Double.class),
                                        @ColumnResult(name="starter",type=Boolean.class),
                                        @ColumnResult(name="ord",type=Integer.class)
                                }
                        )
                }
        ),@SqlResultSetMapping(
        name = "operationTypeSummeryWrapperMapping",
        classes = {
                @ConstructorResult(
                        targetClass = OperationTypeSummeryWrapper.class,
                        columns = {
                                @ColumnResult(name="id",type=Long.class),
                                @ColumnResult(name="name",type=String.class),
                                @ColumnResult(name="description",type=String.class),
                                @ColumnResult(name="sumAmount",type=Double.class)
                        }
                )
        }
)
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "OperationType.findAllOperationTypeWrappers",
                query = "select distinct  opt.opt_id as id,opt.opt_name as name,opt.opt_description as description,opt.opt_active as active,\n" +
                        "         opt.opt_code as code,opt.opt_operation_type as operationType, opt.opt_min_amount as  minAmount,\n" +
                        "         opt.opt_max_amount as maxAmount, opt.opt_max_amount_duration_type as maxAmountDurationType,\n" +
                        "         opt.opt_max_amount_duration as maxAmountDuration, opt.opt_wage_type as wageType,\n" +
                        "         opt.opt_wage_rate as wageRate, opt.opt_wage_amount as wageAmount, opt.opt_source_type as sourceType, opt.opt_default_amounts as defaultAmounts, opt.opt_global_max_daily_amount as globalMaxDailyAmount, opt.opt_starter as starter, opt.opt_order as ord \n" +
                        "  from sc_operation_type opt \n" +
                        " order by opt.opt_order asc",
                resultSetMapping = "operationTypeWrapperMapping"),
        @NamedNativeQuery(name = "OperationType.findAllOperationTypeWrappersBySourceTypeIds",
                query = "select distinct opt.opt_id as id,opt.opt_name as name,opt.opt_description as description,opt.opt_active as active,\n" +
                        "         opt.opt_code as code,opt.opt_operation_type as operationType,opt.opt_min_amount as  minAmount,\n" +
                        "         opt.opt_max_amount as maxAmount,opt.opt_max_amount_duration_type as maxAmountDurationType,\n" +
                        "         opt.opt_max_amount_duration as maxAmountDuration,opt.opt_wage_type as wageType,\n" +
                        "         opt.opt_wage_rate as wageRate,opt.opt_wage_amount as wageAmount, opt.opt_source_type as sourceType, opt.opt_default_amounts as defaultAmounts, opt.opt_global_max_daily_amount as globalMaxDailyAmount, opt.opt_starter as starter, opt.opt_order as ord \n" +
                        "  from sc_operation_type opt\n" +
                        " where opt.opt_source_type in :operationSourceTypes \n" +
                        " order by opt.opt_order asc",
                resultSetMapping = "operationTypeWrapperMapping"),
        @NamedNativeQuery(name = "OperationType.findOperationTypeWrappersByAccountId",
                query = "select distinct  opt.opt_id as id,opt.opt_name as name,opt.opt_description as description,opt.opt_active as active,\n" +
                        "         opt.opt_code as code,opt.opt_operation_type as operationType,nvl(apt.apt_min_amount,opt.opt_min_amount) as  minAmount,\n" +
                        "         nvl(apt.apt_max_amount,opt.opt_max_amount) as maxAmount,nvl(apt.apt_max_amount_duration_type,opt.opt_max_amount_duration_type) as maxAmountDurationType,\n" +
                        "         nvl(apt.apt_max_amount_duration,opt.opt_max_amount_duration) as maxAmountDuration, opt.opt_wage_type as wageType,\n" +
                        "         opt.opt_wage_rate as wageRate, opt.opt_wage_amount as wageAmount, opt.opt_source_type as sourceType, nvl(apt.apt_default_amounts, opt.opt_default_amounts) as defaultAmounts, nvl(apt.apt_global_max_daily_amount ,opt.opt_global_max_daily_amount) as globalMaxDailyAmount, opt.opt_starter as starter, opt.opt_order as ord \n" +
                        "  from sc_operation_type opt\n" +
                        " inner join sc_account_policy_profile_operation_type apt on (opt.opt_id=apt.apt_opt_id)\n" +
                        " inner join sc_account_policy_profile apl on (apl.apl_id=apt.apt_apl_id)\n" +
                        " inner join sc_user_account_policy_profile  uap on (apl.apl_id=uap.uap_apl_id)\n" +
                        " where uap.uap_acc_id=:accountId and uap.uap_usr_id=:userId and opt.opt_source_type=:transactionSourceType \n" +
                        " order by opt.opt_order asc",
                resultSetMapping = "operationTypeWrapperMapping"),
        @NamedNativeQuery(name = "OperationType.findOperationTypeWrappersBySourceTypeId",
                query = "select distinct opt.opt_id as id,opt.opt_name as name,opt.opt_description as description,opt.opt_active as active,\n" +
                        "         opt.opt_code as code,opt.opt_operation_type as operationType,opt.opt_min_amount as  minAmount,\n" +
                        "         opt.opt_max_amount as maxAmount,opt.opt_max_amount_duration_type as maxAmountDurationType,\n" +
                        "         opt.opt_max_amount_duration as maxAmountDuration,opt.opt_wage_type as wageType,\n" +
                        "         opt.opt_wage_rate as wageRate,opt.opt_wage_amount as wageAmount, opt.opt_source_type as sourceType, opt.opt_default_amounts as defaultAmounts, opt.opt_global_max_daily_amount as globalMaxDailyAmount, opt.opt_starter as starter, opt.opt_order as ord \n" +
                        "  from sc_operation_type opt\n" +
                        " where opt.opt_source_type=:operationSourceType and opt.opt_starter>0\n" +
                        " order by opt.opt_order asc",
                resultSetMapping = "operationTypeWrapperMapping"),
        @NamedNativeQuery(name = "OperationType.findActiveOperationTypeWrappersByName",
                query = "select distinct opt.opt_id as id,opt.opt_name as name,opt.opt_description as description,opt.opt_active as active,\n" +
                        "         opt.opt_code as code,opt.opt_operation_type as operationType,opt.opt_min_amount as  minAmount,\n" +
                        "         opt.opt_max_amount as maxAmount,opt.opt_max_amount_duration_type as maxAmountDurationType,\n" +
                        "         opt.opt_max_amount_duration as maxAmountDuration,opt.opt_wage_type as wageType,\n" +
                        "         opt.opt_wage_rate as wageRate,opt.opt_wage_amount as wageAmount, opt.opt_source_type as sourceType, opt.opt_default_amounts as defaultAmounts, opt.opt_global_max_daily_amount as globalMaxDailyAmount, opt.opt_starter as starter, opt.opt_order as ord  \n" +
                        "  from sc_operation_type opt\n" +
                        " where opt_active>0 and opt.opt_name=:operationTypeName" +
                        " order by opt.opt_order asc\n",
                resultSetMapping = "operationTypeWrapperMapping"),
        @NamedNativeQuery(name = "OperationType.findActiveOperationTypeWrappersByCode",
                query = "select distinct opt.opt_id as id,opt.opt_name as name,opt.opt_description as description,opt.opt_active as active,\n" +
                        "         opt.opt_code as code,opt.opt_operation_type as operationType,opt.opt_min_amount as  minAmount,\n" +
                        "         opt.opt_max_amount as maxAmount,opt.opt_max_amount_duration_type as maxAmountDurationType,\n" +
                        "         opt.opt_max_amount_duration as maxAmountDuration,opt.opt_wage_type as wageType,\n" +
                        "         opt.opt_wage_rate as wageRate,opt.opt_wage_amount as wageAmount, opt.opt_source_type as sourceType, opt.opt_default_amounts as defaultAmounts, opt.opt_global_max_daily_amount as globalMaxDailyAmount, opt.opt_starter as starter, opt.opt_order as ord  \n" +
                        "  from sc_operation_type opt\n" +
                        " where opt_active>0 and opt.opt_code=:code" +
                        " order by opt.opt_order asc\n",
                resultSetMapping = "operationTypeWrapperMapping"),
        @NamedNativeQuery(name = "OperationType.findOperationTypeSummery",
                query = "select ot.opt_id as id, ot.opt_name as name,ot.opt_description as description, sum(o.opr_amount) as sumAmount\n" +
                        "from  sc_operation_request o\n" +
                        "inner join sc_operation_type ot  on (o.opr_opt_id=ot.opt_id)\n" +
                        "WHERE date(o.opr_create_date)<=DATE(UTC_TIMESTAMP()) \n" +
                        "group by ot.opt_id,ot.opt_name,ot.opt_description \n" +
                        "union\n" +
                        "select ot.opt_id as id, ot.opt_name as name, ot.opt_description as description , sum(t.trn_debit) as sumAmount\n" +
                        "from sc_transaction t \n" +
                        "inner join sc_operation_type ot  on (t.trn_opt_id=ot.opt_id)\n" +
                        "where ot.opt_id=2 and  DATE(t.trn_create_date)<=DATE(UTC_TIMESTAMP() )\n" +
                        "group by ot.opt_id,ot.opt_name,ot.opt_description \n" +
                        "order by ot.opt_id",
                resultSetMapping = "operationTypeSummeryWrapperMapping")
})
@Table(name = OperationType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = OperationType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "opt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "opt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "opt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "opt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "opt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "opt_modify_by"))
})
public class OperationType extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_operation_type";

    @Column(name = "opt_name",nullable = false)
    @JsonView({AccountJsonView.OperationTypeList.class,MyJsonView.MultiLingual.class})
    private String name;

    @Column(name = "opt_description",nullable = false)
    @JsonView({AccountJsonView.OperationTypeDetails.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.OPERATION_TYPE)
    private String description;

    @Column(name = "opt_active",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Boolean active;

    @Column(name = "opt_code",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer code;


    @Column(name = "opt_operation_type",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private char operationType;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "operationType",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("operationType")
    private List<Transaction> transactions = new ArrayList<Transaction>();

    @OneToMany(targetEntity = AccountPolicyProfileOperationType.class, mappedBy = "operationType", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("operationType")
    private List<AccountPolicyProfileOperationType> accountPolicyProfileOperationTypes = new ArrayList<>();

    @Column(name = "opt_min_amount")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double minAmount;
    @Column(name = "opt_max_amount")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double maxAmount;
    @Column(name = "opt_max_amount_duration_type")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer maxAmountDurationType;
    @Column(name = "opt_max_amount_duration")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer maxAmountDuration;
    @Column(name = "opt_wage_type",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer wageType;
    @Column(name = "opt_wage_rate")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double wageRate;
    @Column(name = "opt_wage_amount")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double wageAmount;
    @Column(name = "opt_source_type",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer sourceType; //1=wallet , 2=card

    @Column(name = "opt_order",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer order;

    @Column(name = "opt_default_amounts",nullable = false)
    private String defaultAmounts;

    @Column(name = "opt_global_max_daily_amount")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double globalMaxDailyAmount;

    @Column(name = "opt_starter")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Boolean starter;

    @Column(name = "opt_notify")
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Boolean notify;


    @Transient
    private transient List<Double> defaultAmountList;

    @JsonView(AccountJsonView.OperationTypeDetails.class)
    public List<Double> getDefaultAmountList() {
        if(defaultAmountList==null) {
            if (Utils.isStringSafeEmpty(this.defaultAmounts))
                defaultAmountList= new ArrayList<>();
            else
                defaultAmountList= Arrays.stream(defaultAmounts.split(",")).map(Double::parseDouble).collect(Collectors.toList());
        }
        return defaultAmountList;
    }

    public Double getWageRate() {
        return wageRate==null ? 0d : wageRate;
    }

    public Double getWageAmount() {
        return wageAmount==null ? 0d : wageAmount;
    }
}
