package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.RequestTransactionWrapper;
import com.core.accounting.model.wrapper.TransactionWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "transactionWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = TransactionWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "accountId", type = Long.class),
                                        @ColumnResult(name = "operationTypeId", type = Long.class),
                                        @ColumnResult(name = "operationTypeName", type = String.class),
                                        @ColumnResult(name = "operationTypeCode", type = Integer.class),
                                        @ColumnResult(name = "operationType", type = Character.class),
                                        @ColumnResult(name = "description", type = String.class),
                                        @ColumnResult(name = "transactionTypeId", type = Integer.class),
                                        @ColumnResult(name = "transactionTypeCaption", type = String.class),
                                        @ColumnResult(name = "debit", type = Double.class),
                                        @ColumnResult(name = "credit", type = Double.class),
                                        @ColumnResult(name = "referenceId", type = String.class),
                                        @ColumnResult(name = "createDate", type = Date.class),
                                        @ColumnResult(name = "createBy", type = Long.class),
                                        @ColumnResult(name = "modifyDate", type = Date.class),
                                        @ColumnResult(name = "status", type = Integer.class),
                                        @ColumnResult(name = "crossTransactionId", type = Long.class),
                                        @ColumnResult(name = "orderId", type = Long.class),
                                        @ColumnResult(name = "userCreditId", type = Long.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = "requestTransactionWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = RequestTransactionWrapper.class,
                                columns = {
                                        @ColumnResult(name = "transactionId", type = Long.class),
                                        @ColumnResult(name = "operationTypeId", type = Integer.class),
                                        @ColumnResult(name = "operationTypeName", type = String.class),
                                        @ColumnResult(name = "operationTypeCode", type = Integer.class),
                                        @ColumnResult(name = "description", type = String.class),
                                        @ColumnResult(name = "transactionTypeId", type = Integer.class),
                                        @ColumnResult(name = "transactionTypeCaption", type = String.class),
                                        @ColumnResult(name = "debit", type = Double.class),
                                        @ColumnResult(name = "credit", type = Double.class),
                                        @ColumnResult(name = "status", type = Integer.class),
                                        @ColumnResult(name = "accountId", type = Long.class),
                                        @ColumnResult(name = "accountName", type = String.class),
                                        @ColumnResult(name = "accountUserId", type = Long.class),
                                        @ColumnResult(name = "accountUserName", type = String.class),
                                        @ColumnResult(name = "accountCategoryId", type = Long.class),
                                        @ColumnResult(name = "accountUserFirstName", type = String.class),
                                        @ColumnResult(name = "accountUserLastName", type = String.class),
                                        @ColumnResult(name = "userCreditId", type = Long.class),
                                        @ColumnResult(name = "userCreditName", type = String.class),
                                        @ColumnResult(name = "crossUserId", type = Long.class),
                                        @ColumnResult(name = "crossUserInfo", type = String.class),
                                        @ColumnResult(name = "createDate", type = Date.class),
                                        @ColumnResult(name = "createBy", type = Long.class),
                                        @ColumnResult(name = "modifyDate", type = Date.class),
                                        @ColumnResult(name = "modifyBy", type = Long.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "Transaction.getTransactionWrapperById",
                query = "SELECT  t.trn_id as id,t.trn_acc_id as accountId,tt.opt_id as operationTypeId,tt.opt_name as operationTypeName,\n" +
                        " tt.opt_code as operationTypeCode,tt.opt_operation_type as operationType,t.trn_description as description,\n" +
                        " t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        " t.trn_debit as debit,t.trn_credit as credit,t.trn_refrence_id as referenceId,t.trn_create_date as createDate,\n" +
                        " t.trn_create_by as createBy,t.trn_modify_date as modifyDate, t.trn_status AS status, \n" +
                        " t.trn_cross_trn_id AS  crossTransactionId , t.trn_ord_id as orderId, t.trn_uac_id as userCreditId \n" +
                        " FROM sc_transaction t \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_id=:transactionId",
                resultSetMapping = "transactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getTransactionWrappersByReferenceId",
                query = "SELECT  t.trn_id as id,t.trn_acc_id as accountId,tt.opt_id as operationTypeId,tt.opt_name as operationTypeName,\n" +
                        " tt.opt_code as operationTypeCode,tt.opt_operation_type as operationType,t.trn_description as description,\n" +
                        " t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        " t.trn_debit as debit,t.trn_credit as credit,t.trn_refrence_id as referenceId,t.trn_create_date as createDate,\n" +
                        " t.trn_create_by as createBy,t.trn_modify_date as modifyDate, t.trn_status AS status, \n" +
                        " t.trn_cross_trn_id AS  crossTransactionId , t.trn_ord_id as orderId, t.trn_uac_id as userCreditId \n" +
                        " FROM sc_transaction t \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_refrence_id=:referenceId\n" +
                        " ORDER BY t.trn_id ASC",
                resultSetMapping = "transactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getAllTransactionWrappersByAccId",
                query = "SELECT  t.trn_id as id,t.trn_acc_id as accountId,tt.opt_id as operationTypeId,tt.opt_name as operationTypeName,\n" +
                        " tt.opt_code as operationTypeCode,tt.opt_operation_type as operationType,t.trn_description as description,\n" +
                        " t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        " t.trn_debit as debit,t.trn_credit as credit,t.trn_refrence_id as referenceId,t.trn_create_date as createDate,\n" +
                        " t.trn_create_by as createBy,t.trn_modify_date as modifyDate, t.trn_status AS status, \n" +
                        " t.trn_cross_trn_id AS  crossTransactionId , t.trn_ord_id as orderId, t.trn_uac_id as userCreditId \n" +
                        " FROM sc_transaction t \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_acc_id=:accountId and (t.trn_uac_id=:userCreditId or -1=:userCreditId)" +
                        " ORDER BY t.trn_create_date DESC , t.trn_id desc",
                resultSetMapping = "transactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getAllTransactionWrappersByAccIdAndUserId",
                query = "SELECT  t.trn_id as id,t.trn_acc_id as accountId,tt.opt_id as operationTypeId,tt.opt_name as operationTypeName,\n" +
                        " tt.opt_code as operationTypeCode,tt.opt_operation_type as operationType,t.trn_description as description,\n" +
                        " t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        " t.trn_debit as debit,t.trn_credit as credit,t.trn_refrence_id as referenceId,t.trn_create_date as createDate,\n" +
                        " t.trn_create_by as createBy,t.trn_modify_date as modifyDate, t.trn_status AS status, \n" +
                        " t.trn_cross_trn_id AS  crossTransactionId , t.trn_ord_id as orderId, t.trn_uac_id as userCreditId \n" +
                        " FROM sc_transaction t \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " INNER JOIN sc_account a ON(t.trn_acc_id=a.acc_id) \n" +
                        " INNER JOIN sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId) \n" +
                        " INNER JOIN sc_account_policy_profile_operation_type apt on (uap.uap_apl_id=apt.apt_apl_id) \n" +
                        " inner join sc_operation_type op on (apt.apt_opt_id=op.opt_id) \n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE op.opt_code=1200 and t.trn_acc_id=:accountId and (a.acc_usr_id=:userId or uap.uap_usr_id=:userId) and (t.trn_uac_id=:userCreditId or -1=:userCreditId) \n" +
                        " ORDER BY t.trn_create_date DESC , t.trn_id desc",
                resultSetMapping = "transactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getTransactionWrappersByOrderId",
                query = "SELECT  t.trn_id as id,t.trn_acc_id as accountId,tt.opt_id as operationTypeId,tt.opt_name as operationTypeName,\n" +
                        " tt.opt_code as operationTypeCode,tt.opt_operation_type as operationType,t.trn_description as description,\n" +
                        " t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        " t.trn_debit as debit,t.trn_credit as credit,t.trn_refrence_id as referenceId,t.trn_create_date as createDate,\n" +
                        " t.trn_create_by as createBy,t.trn_modify_date as modifyDate, t.trn_status AS status, \n " +
                        " t.trn_cross_trn_id AS  crossTransactionId , t.trn_ord_id as orderId, t.trn_uac_id as userCreditId \n" +
                        " FROM sc_transaction t \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_ord_id=:orderId \n" +
                        " ORDER BY t.trn_create_date DESC , t.trn_id desc ",
                resultSetMapping = "transactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getTransactionWrappersByOperationRequestId",
                query = "SELECT  t.trn_id as id,t.trn_acc_id as accountId,tt.opt_id as operationTypeId,tt.opt_name as operationTypeName,\n" +
                        " tt.opt_code as operationTypeCode,tt.opt_operation_type as operationType,t.trn_description as description,\n" +
                        " t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        " t.trn_debit as debit,t.trn_credit as credit,t.trn_refrence_id as referenceId,t.trn_create_date as createDate,\n" +
                        " t.trn_create_by as createBy,t.trn_modify_date as modifyDate, t.trn_status AS status, \n " +
                        " t.trn_cross_trn_id AS  crossTransactionId , t.trn_ord_id as orderId, t.trn_uac_id as userCreditId \n" +
                        " FROM sc_transaction t \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_opr_id=:operationRequestId \n" +
                        " ORDER BY t.trn_create_date DESC , t.trn_id desc ",
                resultSetMapping = "transactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getRequestTransactionWrappersByOperationRequestId",
                query = "SELECT  t.trn_id as transactionId, tt.opt_id as operationTypeId,tt.opt_name as operationTypeName, \n " +
                        "        tt.opt_code as operationTypeCode, t.trn_description as description, \n" +
                        "        t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        "        t.trn_debit as debit,t.trn_credit as credit,t.trn_status as status,\n" +
                        "        a.acc_id as accountId,a.acc_name as accountName,u.usr_id as accountUserId,u.usr_username as accountUserName,\n" +
                        "        a.acc_acg_id as accountCategoryId,nvl(u.usr_first_name,'') as accountUserFirstName, nvl(u.usr_last_name,'') as accountUserLastName, \n " +
                        "        t.trn_uac_id as userCreditId,acd.acd_title as userCreditName,\n" +
                        "        u2.usr_id as crossUserId,nvl(mr.mrc_name,nvl(u2.usr_alias_name,nvl(u2.usr_last_name,u2.usr_username))) as crossUserInfo, \n" +
                        "        t.trn_create_date as createDate, t.trn_create_by as createBy, t.trn_modify_date as modifyDate,t.trn_modify_by as modifyBy \n" +
                        " FROM sc_transaction t  \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " INNER JOIN sc_account a on (a.acc_id=t.trn_acc_id)\n" +
                        " INNER JOIN sc_user u on (a.acc_usr_id=u.usr_id)\n" +
                        " left JOIN sc_operation_request r on (r.opr_id=t.trn_opr_id)\n" +
                        " left JOIN sc_user u2 on (r.opr_usr_id=u2.usr_id)\n" +
                        " left JOIN sc_merchant mr on (mr.mrc_usr_id=u2.usr_id)\n" +
                        " left JOIN  sc_user_account_policy_credit_detail uac on (uac.uac_id=t.trn_uac_id) \n" +
                        " left JOIN  sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id) \n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_opr_id=:operationRequestId\n" +
                        " ORDER BY t.trn_create_date asc , t.trn_id asc",
                resultSetMapping = "requestTransactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getRequestTransactionWrappersByTransactionId",
                query = "SELECT  t.trn_id as transactionId, tt.opt_id as operationTypeId,tt.opt_name as operationTypeName, \n" +
                        "        tt.opt_code as operationTypeCode, t.trn_description as description, \n" +
                        "        t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        "        t.trn_debit as debit,t.trn_credit as credit,t.trn_status as status,\n" +
                        "        a.acc_id as accountId,a.acc_name as accountName,u.usr_id as accountUserId,u.usr_username as accountUserName,\n" +
                        "        a.acc_acg_id as accountCategoryId,nvl(u.usr_first_name,'') as accountUserFirstName, nvl(u.usr_last_name,'') as accountUserLastName, \n " +
                        "        t.trn_uac_id as userCreditId,acd.acd_title as userCreditName, \n " +
                        "        u2.usr_id as crossUserId,nvl(mr.mrc_name,nvl(u2.usr_alias_name,nvl(u2.usr_last_name,u2.usr_username))) as crossUserInfo, \n" +
                        "        t.trn_create_date as createDate, t.trn_create_by as createBy, t.trn_modify_date as modifyDate,t.trn_modify_by as modifyBy \n" +
                        " FROM sc_transaction t  \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " INNER JOIN sc_account a on (a.acc_id=t.trn_acc_id)\n" +
                        " INNER JOIN sc_user u on (a.acc_usr_id=u.usr_id)\n" +
                        " left JOIN sc_operation_request r on (r.opr_id=t.trn_opr_id)\n" +
                        " left JOIN sc_user u2 on (r.opr_usr_id=u2.usr_id)\n" +
                        " left JOIN sc_merchant mr on (mr.mrc_usr_id=u2.usr_id)\n" +
                        " left JOIN  sc_user_account_policy_credit_detail uac on (uac.uac_id=t.trn_uac_id) \n" +
                        " left JOIN  sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id) \n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_id=:transactionId and (a.acc_usr_id=:userId or uap.uap_usr_id=:userId) \n ",
                resultSetMapping = "requestTransactionWrapperMapping"),
        @NamedNativeQuery(name = "Transaction.getRequestTransactionWrappersByTransactionIdAndUserId",
                query = "SELECT  t.trn_id as transactionId, tt.opt_id as operationTypeId,tt.opt_name as operationTypeName, \n" +
                        "        tt.opt_code as operationTypeCode, t.trn_description as description, \n" +
                        "        t.trn_tnt_id as transactionTypeId , tnt.tnt_caption as transactionTypeCaption , \n"+
                        "        t.trn_debit as debit,t.trn_credit as credit,t.trn_status as status,\n" +
                        "        a.acc_id as accountId,a.acc_name as accountName,u.usr_id as accountUserId,u.usr_username as accountUserName,\n" +
                        "        a.acc_acg_id as accountCategoryId,nvl(u.usr_first_name,'') as accountUserFirstName, nvl(u.usr_last_name,'') as accountUserLastName, \n " +
                        "        t.trn_uac_id as userCreditId,acd.acd_title as userCreditName, \n " +
                        "        u2.usr_id as crossUserId,nvl(mr.mrc_name,nvl(u2.usr_alias_name,nvl(u2.usr_last_name,u2.usr_username))) as crossUserInfo, \n" +
                        "        t.trn_create_date as createDate, t.trn_create_by as createBy, t.trn_modify_date as modifyDate,t.trn_modify_by as modifyBy \n" +
                        " FROM sc_transaction t  \n" +
                        " INNER JOIN sc_operation_type tt ON(t.trn_opt_id=tt.opt_id)\n" +
                        " INNER JOIN sc_account a on (a.acc_id=t.trn_acc_id)\n" +
                        " INNER JOIN sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId) \n" +
                        " INNER JOIN sc_user u on (a.acc_usr_id=u.usr_id)\n" +
                        " left JOIN sc_operation_request r on (r.opr_id=t.trn_opr_id)\n" +
                        " left JOIN sc_user u2 on (r.opr_usr_id=u2.usr_id)\n" +
                        " left JOIN sc_merchant mr on (mr.mrc_usr_id=u2.usr_id)\n" +
                        " left JOIN  sc_user_account_policy_credit_detail uac on (uac.uac_id=t.trn_uac_id) \n" +
                        " left JOIN  sc_account_credit_detail acd on (acd.acd_id=uac.uac_acd_id) \n" +
                        " left JOIN sc_transaction_type tnt ON(t.trn_tnt_id=tnt.tnt_id)\n" +
                        " WHERE t.trn_id=:transactionId and (a.acc_usr_id=:userId or uap.uap_usr_id=:userId) \n ",
                resultSetMapping = "requestTransactionWrapperMapping")


})
@Table(name=Transaction.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Transaction.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "trn_id")),
        @AttributeOverride(name = "version", column = @Column(name = "trn_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "trn_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "trn_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "trn_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "trn_modify_by"))
})
public class Transaction extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_transaction";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trn_acc_id",nullable = false,referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "FK_trn_acc_id"))
    @JsonIgnoreProperties("transactions")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trn_opt_id",nullable = false,referencedColumnName = "opt_id", foreignKey = @ForeignKey(name = "FK_trn_opt_id"))
    @JsonView(AccountJsonView.TransactionList.class)
    @JsonIgnoreProperties("transactions")
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trn_tnt_id",referencedColumnName = "tnt_id", foreignKey = @ForeignKey(name = "FK_trn_tnt_id"))
    @JsonView(AccountJsonView.TransactionList.class)
    @JsonIgnoreProperties("transactions")
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trn_uac_id",referencedColumnName = "uac_id", foreignKey = @ForeignKey(name = "FK_trn_uac_id"))
    @JsonView(AccountJsonView.TransactionList.class)
    @JsonIgnoreProperties("transactions")
    private UserAccountPolicyCreditDetail userAccountPolicyCreditDetail;

    @Column(name="trn_description",nullable = false)
    @JsonView(AccountJsonView.TransactionDetails.class)
    private String description;

    @Column(name = "trn_debit",nullable = false)
    @JsonView(AccountJsonView.TransactionList.class)
    private Double debit;

    @Column(name = "trn_credit",nullable = false)
    @JsonView(AccountJsonView.TransactionList.class)
    private Double credit;

    @Column(name = "trn_refrence_id",nullable = false)
    @JsonView(AccountJsonView.TransactionDetails.class)
    private String referenceId;

    @Column(name = "trn_status",nullable = false)
    @JsonView(AccountJsonView.TransactionDetails.class)
    private Integer status;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trn_ord_id",referencedColumnName = "ord_id", foreignKey = @ForeignKey(name = "FK_trn_ord_id"))
    @JsonView(AccountJsonView.TransactionList.class)
    @JsonIgnoreProperties("transactions")
    private Order order;*/

    @Column(name = "trn_ord_id")
    @JsonView(AccountJsonView.TransactionList.class)
    private Long orderId;

    @Column(name = "trn_opr_id")
    @JsonView(AccountJsonView.TransactionList.class)
    private Long operationRequestId;


    @OneToOne
    @JoinColumn(name = "trn_cross_trn_id",referencedColumnName = "trn_id", foreignKey = @ForeignKey(name = "FK_trn_cross_trn_id"))
    @JsonIgnoreProperties({"destination","source"})
    //@JsonView(AccountJsonView.TransactionList.class)
    private Transaction destination;

    @OneToOne(mappedBy = "destination")
    @JsonIgnoreProperties({"destination","source"})
    @JsonView(AccountJsonView.TransactionList.class)
    private Transaction source;


    @OneToOne(mappedBy = "payTransaction")
    private RequestRefundMoney requestRefundMoney;

    @OneToOne(mappedBy = "transaction")
    private ManualTransactionRequest manualTransactionRequest;


    /*@OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "settlTransaction")
    @JsonIgnoreProperties("settlTransaction")
    private ShipmentPlan shipmentPlan;*/


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    public String getStatusDesc() {
        if (this.status==1)
            return BaseUtils.getMessageResource("global.status.permanent");
        else if (this.status==0)
            return BaseUtils.getMessageResource("global.status.temporary");
        else
            return BaseUtils.getMessageResource("global.status.unknown");
    }


    @JsonView(AccountJsonView.TransactionList.class)
    public String getAmountDesc() {
        if (this.getCredit() > 0)
            return BaseUtils.getMessageResource("global.deposit");
        else if (this.getDebit() > 0)
            return BaseUtils.getMessageResource("global.withdraw");
        else
            return "";
    }



}
