package com.core.card.model.dbmodel;

import com.core.accounting.model.enums.EOperation;
import com.core.card.model.wrapper.BankCardOperationWrapper;
import com.core.common.util.Utils;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "bankCardOperationWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = BankCardOperationWrapper.class,
                                columns = {
                                        @ColumnResult(name="id",type= Long.class),
                                        @ColumnResult(name="operationTypeId",type = Integer.class),
                                        @ColumnResult(name="bankCardId",type = Long.class),
                                        @ColumnResult(name="sourceCard",type = String.class),
                                        @ColumnResult(name="sourceCardName",type = String.class),
                                        @ColumnResult(name="targetCard",type = String.class),
                                        @ColumnResult(name="targetCardOwner",type = String.class),
                                        @ColumnResult(name="status",type = Integer.class),
                                        @ColumnResult(name="lastStatusDate",type = Date.class),
                                        @ColumnResult(name="billId",type = String.class),
                                        @ColumnResult(name="billPay",type = String.class),
                                        @ColumnResult(name="amount",type = Double.class),
                                        @ColumnResult(name="description",type = String.class),
                                        @ColumnResult(name="trackingId",type = String.class),
                                        @ColumnResult(name="shpRegistrationDate",type = Date.class),
                                        @ColumnResult(name="shpTransactionId",type = String.class),
                                        @ColumnResult(name="shpTransactionDate",type = Date.class),
                                        @ColumnResult(name="shpStan",type = Integer.class),
                                        @ColumnResult(name="shpRrn",type = String.class),
                                        @ColumnResult(name="shpApprovalCode",type = String.class),
                                        @ColumnResult(name="errorDescription",type= String.class),
                                        @ColumnResult(name="userId",type= Long.class)
                                }
                        )
                }
        )
})
@Table(name= BankCardOperation.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = BankCardOperation.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bco_id")),
        @AttributeOverride(name = "version", column = @Column(name = "bco_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "bco_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "bco_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "bco_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "bco_modify_by"))
})
public class BankCardOperation extends AbstractBaseEntity<Long> {
private static final long serialVersionUID = 1L;
public static final String TABLE_NAME = "sc_bank_card_operation";

    @Column(name="bco_opt_id" ,nullable = false)
    private Integer operationTypeId;

    @Column(name="bco_usr_id" ,nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bco_bcr_id",nullable = false,referencedColumnName = "bcr_id", foreignKey = @ForeignKey(name = "FK_bco_bcr_id"))
    @JsonIgnoreProperties("bankCardOperations")
    private BankCard bankCard;

    @Column(name="bco_status",nullable = false)
    private Integer status=0;

    @Column(name="bco_last_status_date")
    private Date lastStatusDate;

    @Column(name="bco_target_card")
    private String targetCard;

    @Column(name="bco_bill_id")
    private String billId;

    @Column(name="bco_bill_pay")
    private String billPay;

    @Column(name="bco_amount")
    private Double amount;

    @Column(name="bco_description")
    private String description;

    @Column(name="bco_tracking_id")
    private String trackingId;

    @Column(name="bco_shp_card_holder_name")
    private String shpCardHolderName;


    @Column(name="bco_shp_registration_date")
    private Date shpRegistrationDate;

    @Column(name="bco_shp_transaction_id")
    private String shpTransactionId;

    @Column(name="bco_shp_transaction_date")
    private Date shpTransactionDate;

    @Column(name="bco_shp_stan")
    private Integer shpStan;

    @Column(name="bco_shp_rrn")
    private String shpRrn;

    @Column(name="bco_shp_approvalCode")
    private String shpApprovalCode;

    @Column(name="bco_error_description")
    private String errorDescription;

    public String  getTrackingCode() {
        return Utils.getTrackingCode(EOperation.valueOf(this.operationTypeId).getOperationCode().toString(),this.getId());
    }

}
