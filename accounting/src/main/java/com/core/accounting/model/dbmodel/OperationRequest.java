package com.core.accounting.model.dbmodel;

import com.core.accounting.model.contextmodel.FinalOperationRequestDto;
import com.core.accounting.model.wrapper.OperationRequestWrapper;
import com.core.common.util.Utils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
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
                name = "operationRequestWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = OperationRequestWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "title", type = String.class),
                                        @ColumnResult(name = "userId", type = Long.class),
                                        @ColumnResult(name = "operationTypeId", type = Integer.class),
                                        @ColumnResult(name = "sourceType", type = Integer.class),
                                        @ColumnResult(name = "amount", type = Double.class),
                                        @ColumnResult(name = "wage", type = Double.class),
                                        @ColumnResult(name = "status", type = Integer.class),
                                        @ColumnResult(name = "cardNumber", type = String.class),
                                        @ColumnResult(name = "details", type = String.class),
                                        @ColumnResult(name = "referenceNumber", type = String.class),
                                        @ColumnResult(name = "description", type = String.class),
                                        @ColumnResult(name = "self", type = Boolean.class),
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
        @NamedNativeQuery(name = "OperationRequest.findOperationRequestWrapperById",
                query = "select o.opr_id as id, o.opr_title as title, o.opr_usr_id as userId, o.opr_opt_id as operationTypeId, o.opr_source_type as sourceType,\n" +
                        "       o.opr_amount as amount, o.opr_wage as wage, \n" +
                        "       o.opr_status as status, o.opr_card_number as cardNumber, o.opr_details as details,\n" +
                        "       o.opr_reference_number as referenceNumber, o.opr_description  as description, opr_self as self, \n" +
                        "       o.opr_create_date as createDate,  o.opr_create_by as createBy, \n" +
                        "       o.opr_modify_date as modifyDate,o.opr_modify_by as modifyBy       \n" +
                        "from sc_operation_request o  where o.opr_id=:operationRequestId",
                resultSetMapping = "operationRequestWrapperMapping"),
        @NamedNativeQuery(name = "OperationRequest.findOperationRequestWrapperByIdAndUserId",
                query = "select o.opr_id as id, o.opr_title as title, o.opr_usr_id as userId, o.opr_opt_id as operationTypeId, o.opr_source_type as sourceType,\n" +
                        "       o.opr_amount as amount, o.opr_wage as wage, \n" +
                        "       o.opr_status as status, o.opr_card_number as cardNumber, o.opr_details as details,\n" +
                        "       o.opr_reference_number as referenceNumber, o.opr_description  as description, opr_self as self, \n" +
                        "       o.opr_create_date as createDate,  o.opr_create_by as createBy, \n" +
                        "       o.opr_modify_date as modifyDate,o.opr_modify_by as modifyBy       \n" +
                        "from sc_operation_request o  where o.opr_id=:operationRequestId and o.opr_usr_id=:userId",
                resultSetMapping = "operationRequestWrapperMapping"),
        @NamedNativeQuery(name = "OperationRequest.findOperationRequestWrapperByReferenceNumber",
                query = "select o.opr_id as id, o.opr_title as title, o.opr_usr_id as userId, o.opr_opt_id as operationTypeId, o.opr_source_type as sourceType,\n" +
                        "       o.opr_amount as amount, o.opr_wage as wage, \n" +
                        "       o.opr_status as status, o.opr_card_number as cardNumber, o.opr_details as details,\n" +
                        "       o.opr_reference_number as referenceNumber, o.opr_description  as description, opr_self as self, \n" +
                        "       o.opr_create_date as createDate,  o.opr_create_by as createBy, \n" +
                        "       o.opr_modify_date as modifyDate,o.opr_modify_by as modifyBy       \n" +
                        "from sc_operation_request o  where o.opr_reference_number=:referenceNumber",
                resultSetMapping = "operationRequestWrapperMapping"),
        @NamedNativeQuery(name = "OperationRequest.findOperationRequestWrapperByReferenceNumberAndUserId",
                query = "select o.opr_id as id, o.opr_title as title, o.opr_usr_id as userId, o.opr_opt_id as operationTypeId, o.opr_source_type as sourceType,\n" +
                        "       o.opr_amount as amount, o.opr_wage as wage, \n" +
                        "       o.opr_status as status, o.opr_card_number as cardNumber, o.opr_details as details,\n" +
                        "       o.opr_reference_number as referenceNumber, o.opr_description  as description, opr_self as self, \n" +
                        "       o.opr_create_date as createDate,  o.opr_create_by as createBy, \n" +
                        "       o.opr_modify_date as modifyDate,o.opr_modify_by as modifyBy       \n" +
                        "from sc_operation_request o  where o.opr_reference_number=:referenceNumber and o.opr_usr_id=:userId",
                resultSetMapping = "operationRequestWrapperMapping"),
        @NamedNativeQuery(name = "OperationRequest.findOperationRequestWrapperByUserId",
                query = "select o.opr_id as id, o.opr_title as title, o.opr_usr_id as userId, o.opr_opt_id as operationTypeId, o.opr_source_type as sourceType,\n" +
                        "       o.opr_amount as amount, o.opr_wage as wage, \n" +
                        "       o.opr_status as status, o.opr_card_number as cardNumber, o.opr_details as details,\n" +
                        "       o.opr_reference_number as referenceNumber, o.opr_description  as description, opr_self as self, \n" +
                        "       o.opr_create_date as createDate,  o.opr_create_by as createBy, \n" +
                        "       o.opr_modify_date as modifyDate,o.opr_modify_by as modifyBy       \n" +
                        "from sc_operation_request o  where o.opr_usr_id=:userId  order by o.opr_id desc",
                resultSetMapping = "operationRequestWrapperMapping")
})

@Table(name= OperationRequest.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = OperationRequest.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "opr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "opr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "opr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "opr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "opr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "opr_modify_by"))
})
public class OperationRequest extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_operation_request";

    @Column(name="opr_title",nullable = false)
    private String title;

    @Column(name="opr_usr_id",nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opr_opt_id",nullable = false,referencedColumnName = "opt_id", foreignKey = @ForeignKey(name = "FK_opr_opt_id"))
    @JsonIgnoreProperties("operations")
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opr_tnt_id",referencedColumnName = "tnt_id", foreignKey = @ForeignKey(name = "FK_opr_tnt_id"))
    @JsonIgnoreProperties("operations")
    private TransactionType transactionType;

    /*@OneToOne
    @JoinColumn(name = "OPR_PARENT_ID",referencedColumnName = "opr_id", foreignKey = @ForeignKey(name = "FK_OPR_PARENT_ID"))
    @JsonIgnoreProperties("childOperations")
    private OperationRequest parentOperation;*/

    @Column(name="opr_source_type",nullable = false)
    private Integer sourceType; //1=Wallet , 2=Card

    @Column(name="opr_status",nullable = false)
    private Integer status=0;

    @Column(name="opr_card_number")
    private String cardNumber;

    @Column(name="opr_details")
    private String details;

    @Column(name="opr_reference_number")
    private String referenceNumber;

    @Column(name="opr_master")
    private Boolean master;

    @Column(name="opr_description")
    private String description;

    @Column(name="opr_amount")
    private Double amount;

    @Column(name="opr_wage")
    private Double wage;

    @Column(name="opr_self")
    private Boolean self;

    @Column(name="opr_platform")
    private String platform;

    @Column(name="opr_ord_id")
    private Long orderId;


    @Transient
    private transient FinalOperationRequestDto finalTransactionRequestDto;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    public FinalOperationRequestDto getFinalOperationRequestDto() {
        if (this.finalTransactionRequestDto ==null && !Utils.isStringSafeEmpty(this.details))
            finalTransactionRequestDto =Utils.readFromJson(this.details,new TypeReference<FinalOperationRequestDto>() {});
        return finalTransactionRequestDto;
    }

    public void setFinalOperationRequestDto(FinalOperationRequestDto finalTransactionRequestDto) {
        this.finalTransactionRequestDto = finalTransactionRequestDto;
        this.details= Utils.getAsJson(finalTransactionRequestDto);
    }

    public Integer getStatus() {
        if(this.status==0 && (new Date()).getTime()<Utils.addMinuteToDate(this.getCreateDate(),Utils.getOperationRequestWaitMinuteTime()).getTime())
            return 0;
        else if(this.status==0 && (new Date()).getTime()>=Utils.addMinuteToDate(this.getCreateDate(),Utils.getOperationRequestWaitMinuteTime()).getTime())
            return 2;
        else
           return this.status;
    }

    public String getStatusDesc() {
        if (this.getStatus() == 0)
            return BaseUtils.getMessageResource("factor.awaitingPayment");
        else if (this.getStatus() == 1)
            return BaseUtils.getMessageResource("global.status.success");
        else
            return BaseUtils.getMessageResource("global.status.unSuccess");
    }


}
