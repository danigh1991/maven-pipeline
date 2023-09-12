package com.core.accounting.model.dbmodel;

import com.core.datamodel.model.dbmodel.Bank;
import com.core.accounting.model.wrapper.RequestRefundMoneyWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMapping(
        name="requestRefundMoneyMapping",
        classes={
                @ConstructorResult(
                        targetClass=RequestRefundMoneyWrapper.class,
                        columns={
                                @ColumnResult(name="id",type = Long.class),
                                @ColumnResult(name="accountId",type = Long.class),
                                @ColumnResult(name="accountName",type = String.class),
                                @ColumnResult(name="status",type = Integer.class),
                                @ColumnResult(name="reqUserId",type = Long.class),
                                @ColumnResult(name="reqUserName",type = String.class),
                                @ColumnResult(name="reqDesc",type = String.class),
                                @ColumnResult(name="reqAmount",type = Double.class),
                                @ColumnResult(name="createDate",type = Date.class),
                                @ColumnResult(name="modifyDate",type = Date.class),
                                @ColumnResult(name="payUserId",type = Long.class),
                                @ColumnResult(name="payUserName",type = String.class),
                                @ColumnResult(name="payDate",type = Date.class),
                                @ColumnResult(name="payBankRef",type = String.class),
                                @ColumnResult(name="payDesc",type = String.class),
                                @ColumnResult(name="financeDestName",type = String.class),
                                @ColumnResult(name="financeDestCaption",type = String.class),
                                @ColumnResult(name="financeDestValue",type = String.class),
                                @ColumnResult(name="refundTypeId",type = Integer.class),
                                @ColumnResult(name="refundForId",type = Long.class),
                                @ColumnResult(name="payBankId",type = Long.class),
                                @ColumnResult(name="payBankName",type = String.class),
                                @ColumnResult(name="toBankId",type = Long.class),
                                @ColumnResult(name="toBankName",type = String.class)
                        }
                )
        }
)
@NamedNativeQueries({
        @NamedNativeQuery(name = "RequestRefundMoney.findAllByUserId",
                query = "SELECT r.rrm_id as id, a.acc_id as accountId, a.acc_name as accountName, r.rrm_status as status, r.rrm_req_usr_id as reqUserId, r.rrm_req_dsc as reqDesc, r.rrm_amount as reqAmount, r.rrm_create_date as createDate \n" +
                        ", r.rrm_modify_date as modifyDate, r.rrm_pay_usr_id as payUserId, r.rrm_pay_date as payDate, r.rrm_pay_bnk_ref as payBankRef, r.rrm_pay_desc as payDesc \n" +
                        ", r.rrm_fdn_name as financeDestName, r.rrm_fdn_caption as financeDestCaption,r. rrm_fdn_value as financeDestValue, r.rrm_refund_type_id as refundTypeId, r.rrm_refund_for_id as refundForId \n" +
                        ", r.rrm_pay_bnk_id as payBankId, pay_b.bnk_name as payBankName, r.rrm_to_bnk_id as toBankId, to_b.bnk_name as toBankName\n" +
                        ", nvl(concat(concat(ur.usr_last_name,'-'),ur.usr_username),ur.usr_username) as reqUserName, nvl(concat(concat(up.usr_last_name,'-'),up.usr_username),up.usr_username) as payUserName\n" +
                        " FROM sc_request_refund_money r  \n" +
                        " inner JOIN sc_user ur on r.rrm_req_usr_id=ur.usr_id  \n" +
                        " left  JOIN sc_user up on r.rrm_pay_usr_id=up.usr_id  \n" +
                        " inner JOIN sc_account a on a.acc_id=r.rrm_acc_id  \n" +
                        " LEFT OUTER JOIN sc_bank pay_b on r.rrm_pay_bnk_id = pay_b.bnk_id  \n" +
                        " LEFT OUTER JOIN sc_bank to_b on r.rrm_to_bnk_id = to_b.bnk_id  \n" +
                        " WHERE r.rrm_req_usr_id=:userId  \n" +
                        " order by CASE WHEN (r.rrm_status = 0) THEN 1 ELSE 2 END asc ,r.rrm_create_date desc"
                ,resultSetMapping = "requestRefundMoneyMapping"),
        @NamedNativeQuery(name = "RequestRefundMoney.findAllRequest",
                query = "SELECT r.rrm_id as id, a.acc_id as accountId, a.acc_name as accountName, r.rrm_status as status, r.rrm_req_usr_id as reqUserId, r.rrm_req_dsc as reqDesc, r.rrm_amount as reqAmount, r.rrm_create_date as createDate \n" +
                        ", r.rrm_modify_date as modifyDate, r.rrm_pay_usr_id as payUserId, r.rrm_pay_date as payDate, r.rrm_pay_bnk_ref as payBankRef, r.rrm_pay_desc as payDesc \n" +
                        ", r.rrm_fdn_name as financeDestName, r.rrm_fdn_caption as financeDestCaption,r. rrm_fdn_value as financeDestValue, r.rrm_refund_type_id as refundTypeId, r.rrm_refund_for_id as refundForId \n" +
                        ", r.rrm_pay_bnk_id as payBankId, pay_b.bnk_name as payBankName, r.rrm_to_bnk_id as toBankId, to_b.bnk_name as toBankName\n" +
                        ", nvl(concat(concat(ur.usr_last_name,'-'),ur.usr_username),ur.usr_username) as reqUserName, nvl(concat(concat(up.usr_last_name,'-'),up.usr_username),up.usr_username) as payUserName\n" +
                        " FROM sc_request_refund_money r  \n" +
                        " inner JOIN sc_user ur on r.rrm_req_usr_id=ur.usr_id  \n" +
                        " left  JOIN sc_user up on r.rrm_pay_usr_id=up.usr_id  \n" +
                        " inner JOIN sc_account a on a.acc_id=r.rrm_acc_id  \n" +
                        " LEFT OUTER JOIN sc_bank pay_b on r.rrm_pay_bnk_id = pay_b.bnk_id  \n" +
                        " LEFT OUTER JOIN sc_bank to_b on r.rrm_to_bnk_id = to_b.bnk_id  \n" +
                        " order by CASE WHEN (r.rrm_status = 0) THEN 1 ELSE 2 END asc ,r.rrm_create_date desc"
                ,resultSetMapping = "requestRefundMoneyMapping"),
        @NamedNativeQuery(name = "RequestRefundMoney.findWrapperById",
                query = "SELECT r.rrm_id as id, a.acc_id as accountId, a.acc_name as accountName, r.rrm_status as status, r.rrm_req_usr_id as reqUserId, r.rrm_req_dsc as reqDesc, r.rrm_amount as reqAmount, r.rrm_create_date as createDate \n" +
                        ", r.rrm_modify_date as modifyDate, r.rrm_pay_usr_id as payUserId, r.rrm_pay_date as payDate, r.rrm_pay_bnk_ref as payBankRef, r.rrm_pay_desc as payDesc \n" +
                        ", r.rrm_fdn_name as financeDestName, r.rrm_fdn_caption as financeDestCaption,r. rrm_fdn_value as financeDestValue, r.rrm_refund_type_id as refundTypeId, r.rrm_refund_for_id as refundForId \n" +
                        ", r.rrm_pay_bnk_id as payBankId, pay_b.bnk_name as payBankName, r.rrm_to_bnk_id as toBankId, to_b.bnk_name as toBankName\n" +
                        ", nvl(concat(concat(ur.usr_last_name,'-'),ur.usr_username),ur.usr_username) as reqUserName, nvl(concat(concat(up.usr_last_name,'-'),up.usr_username),up.usr_username) as payUserName\n" +
                        " FROM sc_request_refund_money r  \n" +
                        " inner JOIN sc_user ur on r.rrm_req_usr_id=ur.usr_id  \n" +
                        " left  JOIN sc_user up on r.rrm_pay_usr_id=up.usr_id  \n" +
                        " inner JOIN sc_account a on a.acc_id=r.rrm_acc_id  \n" +
                        " LEFT OUTER JOIN sc_bank pay_b on r.rrm_pay_bnk_id = pay_b.bnk_id  \n" +
                        " LEFT OUTER JOIN sc_bank to_b on r.rrm_to_bnk_id = to_b.bnk_id  \n" +
                        " WHERE r.rrm_id = :id  \n" +
                        " order by CASE WHEN (r.rrm_status = 0) THEN 1 ELSE 2 END asc ,r.rrm_create_date desc"
                ,resultSetMapping = "requestRefundMoneyMapping"),
        @NamedNativeQuery(name = "RequestRefundMoney.findWrapperByIdAndUserId",
                query = "SELECT r.rrm_id as id, a.acc_id as accountId, a.acc_name as accountName, r.rrm_status as status, r.rrm_req_usr_id as reqUserId, r.rrm_req_dsc as reqDesc, r.rrm_amount as reqAmount, r.rrm_create_date as createDate \n" +
                        ", r.rrm_modify_date as modifyDate, r.rrm_pay_usr_id as payUserId, r.rrm_pay_date as payDate, r.rrm_pay_bnk_ref as payBankRef, r.rrm_pay_desc as payDesc \n" +
                        ", r.rrm_fdn_name as financeDestName, r.rrm_fdn_caption as financeDestCaption,r. rrm_fdn_value as financeDestValue, r.rrm_refund_type_id as refundTypeId, r.rrm_refund_for_id as refundForId \n" +
                        ", r.rrm_pay_bnk_id as payBankId, pay_b.bnk_name as payBankName, r.rrm_to_bnk_id as toBankId, to_b.bnk_name as toBankName\n" +
                        ", nvl(concat(concat(ur.usr_last_name,'-'),ur.usr_username),ur.usr_username) as reqUserName, nvl(concat(concat(up.usr_last_name,'-'),up.usr_username),up.usr_username) as payUserName\n" +
                        " FROM sc_request_refund_money r  \n" +
                        " inner JOIN sc_user ur on r.rrm_req_usr_id=ur.usr_id  \n" +
                        " left  JOIN sc_user up on r.rrm_pay_usr_id=up.usr_id  \n" +
                        " inner JOIN sc_account a on a.acc_id=r.rrm_acc_id  \n" +
                        " LEFT OUTER JOIN sc_bank pay_b on r.rrm_pay_bnk_id = pay_b.bnk_id  \n" +
                        " LEFT OUTER JOIN sc_bank to_b on r.rrm_to_bnk_id = to_b.bnk_id  \n" +
                        " WHERE r.rrm_id = :id AND r.rrm_req_usr_id=:userId  \n" +
                        " order by CASE WHEN (r.rrm_status = 0) THEN 1 ELSE 2 END asc ,r.rrm_create_date desc"
                ,resultSetMapping = "requestRefundMoneyMapping"),
})
@Table(name=RequestRefundMoney.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = RequestRefundMoney.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "rrm_id")),
        @AttributeOverride(name = "version", column = @Column(name = "rrm_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "rrm_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "rrm_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "rrm_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "rrm_modify_by"))
})
public class RequestRefundMoney extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_request_refund_money";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rrm_acc_id",nullable = false,referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "FK_rrm_acc_id"))
    @JsonIgnoreProperties("requestRefunds")
    private Account account;

    @Column(name="rrm_status",nullable = false)
    private Integer status;

    @Column(name="rrm_req_usr_id",nullable = false)
    private Long reqUserId;

    @Column(name="rrm_req_dsc")
    private String reqDesc;

    @Column(name="rrm_amount",nullable = false)
    private Double reqAmount;

    @Column(name="rrm_pay_usr_id")
    private Long payUserId;

    @Column(name="rrm_pay_date")
    private Date payDate;

    @OneToOne
    @JoinColumn(name = "rrm_pay_trn_id",referencedColumnName = "trn_id", foreignKey = @ForeignKey(name = "FK_rrm_pay_trn_id"))
    @JsonIgnoreProperties("requestRefundMoney")
    private Transaction payTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rrm_pay_bnk_id",referencedColumnName = "bnk_id", foreignKey = @ForeignKey(name = "FK_rrm_pay_bnk_id"))
    private Bank payBank;

    @Column(name="rrm_pay_bnk_ref")
    private String payBankRef;

    @Column(name="rrm_pay_desc")
    private String payDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rrm_to_bnk_id",referencedColumnName = "bnk_id", foreignKey = @ForeignKey(name = "FK_rrm_to_bnk_id"))
    private Bank toBank;

    @Column(name="rrm_fdn_name")
    private String financeDestName ;

    @Column(name="rrm_fdn_caption")
    private String financeDestCaption ;

    @Column(name="rrm_fdn_value")
    private String financeDestValue ;

    @Column(name="rrm_refund_type_id",nullable = false)
    private Integer refundTypeId;

    @Column(name="rrm_refund_for_id")
    private Long refundForId;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
