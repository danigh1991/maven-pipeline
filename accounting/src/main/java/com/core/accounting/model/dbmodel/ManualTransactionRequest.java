package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.ManualTransactionRequestWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
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
@SqlResultSetMapping(
        name="manualTransactionRequestMapping",
        classes={
                @ConstructorResult(
                        targetClass= ManualTransactionRequestWrapper.class,
                        columns={
                                @ColumnResult(name = "id", type =Long.class),
                                @ColumnResult(name = "accountId", type =Long.class),
                                @ColumnResult(name = "accountName", type =String.class),
                                @ColumnResult(name = "accountTypeId", type =Long.class),
                                @ColumnResult(name = "accountTypeDesc", type =String.class),
                                @ColumnResult(name = "userId", type =Long.class),
                                @ColumnResult(name = "userName", type =String.class),
                                @ColumnResult(name = "transactionId", type =Long.class),
                                @ColumnResult(name = "status", type =Integer.class),
                                @ColumnResult(name = "reference", type =String.class),
                                @ColumnResult(name = "referenceDate", type =Date.class),
                                @ColumnResult(name = "description", type =String.class),
                                @ColumnResult(name = "amount", type =Double.class),
                                @ColumnResult(name = "approvedBy", type =Long.class),
                                @ColumnResult(name = "approvedDate", type =Date.class),
                                @ColumnResult(name = "approvedDescription", type =String.class),
                                @ColumnResult(name = "createBy", type =Long.class),
                                @ColumnResult(name = "createDate", type =Date.class),
                                @ColumnResult(name = "modifyBy", type =Long.class),
                                @ColumnResult(name = "modifyDate", type =Date.class)
                        }
                )
        }
)
@Table(name= ManualTransactionRequest.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = ManualTransactionRequest.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "mtr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "mtr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "mtr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "mtr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "mtr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "mtr_modify_by"))
})
public class ManualTransactionRequest extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_manual_transaction";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mtr_acc_id",nullable = false,referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "fk_mtr_acc_id"))
    @JsonIgnoreProperties("manualTransactionRequests")
    private Account account;

    @OneToOne
    @JoinColumn(name = "mtr_trn_id",referencedColumnName = "trn_id", foreignKey = @ForeignKey(name = "fk_mtr_trn_id"))
    @JsonIgnoreProperties("manualTransactionRequests")
    private Transaction transaction;

    @Column(name="mtr_status",nullable = false)
    private Integer status=0;

    @Column(name="mtr_reference")
    private String reference;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @Column(name="mtr_reference_date")
    private Date referenceDate;

    @Column(name="mtr_description")
    private String description;

    @Column(name="mtr_amount",nullable = false)
    private Double amount;

    @Column(name="mtr_approved_by")
    private Long approvedBy;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @Column(name="mtr_approved_date")
    private Date approvedDate;

    @Column(name="mtr_approved_description")
    private String approvedDescription;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
