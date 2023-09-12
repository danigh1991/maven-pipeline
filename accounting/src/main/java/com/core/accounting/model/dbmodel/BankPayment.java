package com.core.accounting.model.dbmodel;

import com.core.accounting.model.enums.EBankPaymentStatus;
import com.core.accounting.model.wrapper.BankPaymentWrapper;
import com.core.datamodel.model.dbmodel.Bank;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractEntity;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="bankPaymentWrapperMapping",
                classes={
                        @ConstructorResult  (
                                targetClass= BankPaymentWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="bankId",type = Long.class),
                                        @ColumnResult(name="bankName",type = String.class),
                                        @ColumnResult(name="accountId",type = Long.class),
                                        @ColumnResult(name="myReferenceNumber",type = String.class),
                                        @ColumnResult(name="amount",type = Double.class),
                                        @ColumnResult(name="bankReferenceNumber",type = String.class),
                                        @ColumnResult(name="bankResponseLastStatus",type = String.class),
                                        @ColumnResult(name="bankResponseStatusHist",type = String.class),
                                        @ColumnResult(name="bankResponseTransactionRef",type = String.class),
                                        @ColumnResult(name="bankResponse",type = String.class),
                                        @ColumnResult(name="orderId",type = Long.class),
                                        @ColumnResult(name="shpIds",type = String.class),
                                        @ColumnResult(name="status",type = Integer.class),
                                        @ColumnResult(name="transactionId",type = Long.class),
                                        @ColumnResult(name="operationRequestId",type = Long.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class)
                                }
                        )
                }
        )})
@Table(name=BankPayment.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = BankPayment.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
/*@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bpy_id")),
        @AttributeOverride(name = "version", column = @Column(name = "bpy_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "bpy_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "bpy_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "bpy_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "bpy_modify_by"))
})*/
public class BankPayment extends AbstractEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_bank_payment";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = AbstractBaseEntity.DEFAULT_SEQ_GEN)
    @Column(name="bpy_id",unique = true, nullable = false)
    @JsonView(MyJsonView.BankPaymentList.class)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bpy_bnk_id",nullable = false,referencedColumnName = "bnk_id", foreignKey = @ForeignKey(name = "FK_bpy_bnk_id"))
    @JsonView(MyJsonView.BankPaymentList.class)
    @JsonIgnoreProperties("bankPayments")
    private Bank bank;

    @Column(name = "bpy_acc_id",nullable = false)
    @JsonView(MyJsonView.BankPaymentList.class)
    private Long accountId;

    @Column(name = "bpy_amount",nullable = false)
    @JsonView(MyJsonView.BankPaymentList.class)
    private Double amount;

    @Column(name = "bpy_create_date",nullable = false,updatable = false)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    @CreationTimestamp
    private Date createDate;

    @Column(name = "bpy_create_by",nullable = false)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long createBy;

    @Column(name = "bpy_trn_id")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long transactionId;

    @Column(name = "bpy_status",nullable = false)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Integer status;

    @Column(name = "bpy_modify_date",nullable = false)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    @UpdateTimestamp
    private Date modifyDate;

    @Column(name = "bpy_modify_by",nullable = false)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long modifyBy;

    @Column(name = "bpy_bank_reference_number")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankReferenceNumber;

    @Column(name = "bpy_bank_response_transaction_ref")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankTransactionRef	;

    @Column(name = "bpy_bank_response_last_status")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankResponseLastStatus;

    @Column(name = "bpy_bank_response_status_hist")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankResponseStatusHist;

    @Column(name = "bpy_bank_response")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String bankResponse;

    @Column(name = "bpy_my_reference_number",nullable = false)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private String myReferenceNumber;

    @Column(name = "bpy_ord_id")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long orderId;

    @Column(name = "bpy_shp_ids")
    private String shipmentIds;

    @Column(name = "bpy_opr_id")
    @JsonView(MyJsonView.BankPaymentDetails.class)
    private Long operationRequestId;

    @Transient
    private List<Long> shipmentIdList=null;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonView(MyJsonView.BankPaymentDetails.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @JsonView(MyJsonView.BankPaymentDetails.class)
    public String statusDesc(){
       return EBankPaymentStatus.valueOf(this.getStatus()).getCaption();
    }

    public List<Long> getShipmentIdList() {
        if(shipmentIdList==null && !BaseUtils.isStringSafeEmpty(this.getShipmentIds())){
            String[] tmpIds= this.getShipmentIds().split(",");
            shipmentIdList= Arrays.stream(tmpIds).mapToLong(b-> Long.valueOf(b)).boxed().collect(Collectors.toList());
        }
        return shipmentIdList;
    }

}
