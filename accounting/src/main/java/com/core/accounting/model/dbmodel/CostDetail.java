package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.CostDetailWrapper;
import com.core.accounting.model.wrapper.CostShareRequestDetailWrapper;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
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
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="costDetailWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= CostDetailWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="costShareRequestId",type = Long.class),
                                        @ColumnResult(name="description",type = String.class),
                                        @ColumnResult(name="amount",type = Double.class),
                                        @ColumnResult(name="effectiveDate",type = Date.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "CostDetail.findWrapperById",
                query = "select d.cdt_id as id, d.cdt_csr_id as costShareRequestId, d.cdt_description  as description , d.cdt_amount as amount,\n" +
                        "       d.cdt_date  as effectiveDate , d.cdt_create_date as createDate, d.cdt_create_by  as createBy ,\n" +
                        "       d.cdt_modify_date as modifyDate, d.cdt_modify_by as modifyBy    from sc_cost_detail d \n" +
                        " where d.cdt_id=:costDetailId"
                ,resultSetMapping = "costDetailWrapperMapping"),
        @NamedNativeQuery(name = "CostDetail.findWrapperByIdAndUserId",
                query = "select d.cdt_id as id, d.cdt_csr_id as costShareRequestId, d.cdt_description  as description , d.cdt_amount as amount,\n" +
                        "       d.cdt_date  as effectiveDate , d.cdt_create_date as createDate, d.cdt_create_by  as createBy ,\n" +
                        "       d.cdt_modify_date as modifyDate, d.cdt_modify_by as modifyBy    from sc_cost_detail d \n" +
                        " inner join sc_cost_share_request r on (r.csr_id=d.cdt_csr_id) \n" +
                        " where d.cdt_id=:costDetailId and r.csr_usr_id=:userId"
                ,resultSetMapping = "costDetailWrapperMapping"),
        @NamedNativeQuery(name = "CostDetail.findWrapperByCostShareRequestId",
                query = "select d.cdt_id as id, d.cdt_csr_id as costShareRequestId, d.cdt_description  as description , d.cdt_amount as amount,\n" +
                        "       d.cdt_date  as effectiveDate , d.cdt_create_date as createDate, d.cdt_create_by  as createBy ,\n" +
                        "       d.cdt_modify_date as modifyDate, d.cdt_modify_by as modifyBy    from sc_cost_detail d \n" +
                        " where d.cdt_csr_id=:costShareRequestId"
                ,resultSetMapping = "costDetailWrapperMapping"),
        @NamedNativeQuery(name = "CostDetail.findWrapperByCostShareRequestIdAndUserId",
                query = "select d.cdt_id as id, d.cdt_csr_id as costShareRequestId, d.cdt_description  as description , d.cdt_amount as amount,\n" +
                        "       d.cdt_date  as effectiveDate , d.cdt_create_date as createDate, d.cdt_create_by  as createBy ,\n" +
                        "       d.cdt_modify_date as modifyDate, d.cdt_modify_by as modifyBy    from sc_cost_detail d \n" +
                        " inner join sc_cost_share_request r on (r.csr_id=d.cdt_csr_id) \n" +
                        " where d.cdt_csr_id=:costShareRequestId and r.csr_usr_id=:userId"
                ,resultSetMapping = "costDetailWrapperMapping")
})
@Table(name= CostDetail.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = CostDetail.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cdt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cdt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cdt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cdt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cdt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cdt_modify_by"))
})
public class CostDetail extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_cost_detail";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cdt_csr_id",referencedColumnName = "csr_id", foreignKey = @ForeignKey(name = "fk_cdt_csr_id"))
    @JsonIgnoreProperties("costDetails")
    private CostShareRequest costShareRequest;

    @JsonView(AccountJsonView.CostDetailList.class)
    @Column(name="cdt_amount")
    private Double amount;

    @JsonView(AccountJsonView.CostDetailList.class)
    @Column(name="cdt_description")
    private String description;

    @JsonView(AccountJsonView.CostDetailList.class)
    @Column(name="cdt_date")
    private Date effectiveDate;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
