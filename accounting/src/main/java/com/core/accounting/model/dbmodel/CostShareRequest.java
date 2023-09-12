package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.CostShareRequestWrapper;
import com.core.accounting.model.wrapper.DepositRequestWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@SqlResultSetMapping(
        name="costShareRequestWrapperMapping",
        classes={
                @ConstructorResult(
                        targetClass= CostShareRequestWrapper.class,
                        columns={
                                @ColumnResult(name="id",type = Long.class),
                                @ColumnResult(name="costShareTypeId",type = Long.class),
                                @ColumnResult(name="costShareTypeTitle",type = String.class),
                                @ColumnResult(name="userId",type = Long.class),
                                @ColumnResult(name="title",type = String.class),
                                @ColumnResult(name="accountId",type = Long.class),
                                @ColumnResult(name="accountName",type = String.class),
                                @ColumnResult(name="description",type = String.class),
                                @ColumnResult(name="active",type = Boolean.class),
                                @ColumnResult(name="expireDate",type = Date.class),
                                @ColumnResult(name="createDate",type = Date.class),
                                @ColumnResult(name="createBy",type = Long.class),
                                @ColumnResult(name="modifyDate",type = Date.class),
                                @ColumnResult(name="modifyBy",type = Long.class)
                        }
                )
        }
)
@NamedNativeQueries({
        @NamedNativeQuery(name = "CostShareRequest.findWrapperById",
                query = "select c.csr_id as id, t.cst_id as costShareTypeId,t.cst_title as costShareTypeTitle, c.csr_usr_id as userId, c.csr_title as title, c.csr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       c.csr_description as description, c.csr_active as active, c.csr_expire_date as expireDate,\n" +
                        "       c.csr_create_date as createDate, c.csr_create_by as createBy, c.csr_modify_date as modifyDate,\n" +
                        "       c.csr_modify_by as modifyBy from sc_cost_share_request c\n" +
                        "inner join sc_cost_share_type t on (c.csr_cst_id=t.cst_id)\n" +
                        "inner join sc_account a on (c.csr_acc_id=a.acc_id)\n" +
                        "where c.csr_id=:costShareRequestId"
                ,resultSetMapping = "costShareRequestWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequest.findWrapperByIdAndUserId",
                query = "select c.csr_id as id, t.cst_id as costShareTypeId,t.cst_title as costShareTypeTitle, c.csr_usr_id as userId, c.csr_title as title, c.csr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       c.csr_description as description, c.csr_active as active, c.csr_expire_date as expireDate,\n" +
                        "       c.csr_create_date as createDate, c.csr_create_by as createBy, c.csr_modify_date as modifyDate,\n" +
                        "       c.csr_modify_by as modifyBy from sc_cost_share_request c\n" +
                        "inner join sc_cost_share_type t on (c.csr_cst_id=t.cst_id)\n" +
                        "inner join sc_account a on (c.csr_acc_id=a.acc_id)\n" +
                        "where c.csr_id=:costShareRequestId and c.csr_usr_id=:userId "
                ,resultSetMapping = "costShareRequestWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequest.findWrapperByUserId",
                query = "select c.csr_id as id, t.cst_id as costShareTypeId,t.cst_title as costShareTypeTitle, c.csr_usr_id as userId, c.csr_title as title, c.csr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       c.csr_description as description, c.csr_active as active, c.csr_expire_date as expireDate,\n" +
                        "       c.csr_create_date as createDate, c.csr_create_by as createBy, c.csr_modify_date as modifyDate,\n" +
                        "       c.csr_modify_by as modifyBy from sc_cost_share_request c\n" +
                        "inner join sc_cost_share_type t on (c.csr_cst_id=t.cst_id)\n" +
                        "inner join sc_account a on (c.csr_acc_id=a.acc_id)\n" +
                        "where c.csr_usr_id=:userId order by c.csr_create_date desc"
                ,resultSetMapping = "costShareRequestWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequest.findWrapperByUserIdAndTypeId",
                query = "select c.csr_id as id, t.cst_id as costShareTypeId,t.cst_title as costShareTypeTitle, c.csr_usr_id as userId, c.csr_title as title, c.csr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       c.csr_description as description, c.csr_active as active, c.csr_expire_date as expireDate,\n" +
                        "       c.csr_create_date as createDate, c.csr_create_by as createBy, c.csr_modify_date as modifyDate,\n" +
                        "       c.csr_modify_by as modifyBy from sc_cost_share_request c\n" +
                        "inner join sc_cost_share_type t on (c.csr_cst_id=t.cst_id)\n" +
                        "inner join sc_account a on (c.csr_acc_id=a.acc_id)\n" +
                        "where t.cst_id=:typeId and c.csr_usr_id=:userId order by c.csr_create_date desc"
                ,resultSetMapping = "costShareRequestWrapperMapping")
})
@Table(name= CostShareRequest.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = CostShareRequest.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "csr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "csr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "csr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "csr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "csr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "csr_modify_by"))
})
public class CostShareRequest extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_cost_share_request";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csr_cst_id",referencedColumnName = "cst_id", foreignKey = @ForeignKey(name = "FK_csr_cst_id"))
    @JsonIgnoreProperties("costShareRequests")
    private CostShareType costShareType;


    @Column(name="csr_usr_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csr_acc_id",referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "fk_csr_acc_id"))
    private Account account;

    @JsonView(AccountJsonView.CostShareRequestList.class)
    @Column(name="csr_title")
    private String title;

    @JsonView(AccountJsonView.CostShareRequestDetail.class)
    @Column(name="csr_description")
    private String description;

    @JsonView(AccountJsonView.CostShareRequestList.class)
    @Column(name="csr_active")
    private Boolean active;

    @JsonView(AccountJsonView.CostShareRequestDetail.class)
    @Column(name="csr_expire_date")
    private Date expireDate;

    @OneToMany(targetEntity=CostShareRequestDetail.class,mappedBy = "costShareRequest",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("costShareRequest")
    private List<CostShareRequestDetail> costShareRequestDetails = new ArrayList<>();

    @OneToMany(targetEntity=CostDetail.class,mappedBy = "costShareRequest",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("costShareRequest")
    private List<CostDetail> costDetails = new ArrayList<>();


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
