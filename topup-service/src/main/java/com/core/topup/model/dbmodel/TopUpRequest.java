package com.core.topup.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.topup.model.wrapper.TopUpRequestWrapper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="topUpRequestWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= TopUpRequestWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="typeId",type = Integer.class),
                                        @ColumnResult(name="userId",type = Long.class),
                                        @ColumnResult(name="userName",type = String.class),
                                        @ColumnResult(name="operationRequestId",type = Long.class),
                                        @ColumnResult(name="chargeType",type = String.class),
                                        @ColumnResult(name="packageType",type = Integer.class),
                                        @ColumnResult(name="productTypeId",type = Integer.class),
                                        @ColumnResult(name="productId",type = Integer.class),
                                        @ColumnResult(name="description",type = String.class),
                                        @ColumnResult(name="amount",type = Double.class),
                                        @ColumnResult(name="phoneNumber",type = String.class),
                                        @ColumnResult(name="operator",type = String.class),
                                        @ColumnResult(name="saleDescription",type = String.class),
                                        @ColumnResult(name="cardType",type = String.class),
                                        @ColumnResult(name="cardNo",type = String.class),
                                        @ColumnResult(name="channelId",type = String.class),
                                        @ColumnResult(name="requestIp",type = String.class),
                                        @ColumnResult(name="status",type = Integer.class),
                                        @ColumnResult(name="reserveId",type = Long.class),
                                        @ColumnResult(name="saleId",type = Long.class),
                                        @ColumnResult(name="operatorTransactionId",type = String.class),
                                        @ColumnResult(name="response",type = String.class),
                                        @ColumnResult(name="trackingId",type = String.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class)
                                }
                        )
                }
        )})
@Table(name= TopUpRequest.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = TopUpRequest.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "tpr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "tpr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "tpr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "tpr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "tpr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "tpr_modify_by"))
})
public class TopUpRequest extends AbstractBaseEntity<Long> {
private static final long serialVersionUID = 1L;
public static final String TABLE_NAME = "sc_topup_request";

    @Column(name="tpr_usr_id",nullable = false)
    private Long userId;

    @Column(name="tpr_type_id")
    private Integer typeId;  //1=Charge, 2=Internet Package

    @Column(name="tpr_opr_id")
    private Long operationRequestId;

    @Column(name="tpr_charge_type")
    private String chargeType;

    @Column(name="tpr_package_type")
    private Integer packageType;

    @Column(name="tpr_product_type_id")
    private Integer productTypeId;

    @Column(name="tpr_product_id")
    private Integer productId;

    @Column(name="tpr_description",nullable = false)
    private String description;


    @Column(name="tpr_amount",nullable = false)
    private Double amount;

    @Column(name="tpr_phone_number",nullable = false)
    private String phoneNumber;

    @Column(name="tpr_operator",nullable = false)
    private String operator;

    @Column(name="tpr_sale_description",nullable = false)
    private String saleDescription;

    @Column(name="tpr_card_type",nullable = false)
    private String cardType;

    @Column(name="tpr_card_no",nullable = false)
    private String cardNo;

    @Column(name="tpr_channel_id",nullable = false)
    private String channelId;

    @Column(name="tpr_request_ip",nullable = false)
    private String requestIp;

    @Column(name="tpr_status",nullable = false)
    private Integer status=0;

    @Column(name="tpr_reserve_id")
    private Long reserveId;

    @Column(name="tpr_sale_id")
    private Long saleId;

    @Column(name="tpr_operator_transaction_id")
    private String operatorTransactionId;

    @Column(name="tpr_response")
    private String response;

    @Column(name="tpr_tracking_id")
    private String trackingId;

}
