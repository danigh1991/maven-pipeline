package com.core.accounting.model.dbmodel;

import com.core.accounting.model.wrapper.DepositRequestWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
        name="depositRequestWrapperMapping",
        classes={
                @ConstructorResult(
                        targetClass= DepositRequestWrapper.class,
                        columns={
                                @ColumnResult(name="id",type = Long.class),
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
        @NamedNativeQuery(name = "DepositRequest.findWrapperById",
                query = "select d.dpr_id as id, d.dpr_usr_id as userId, d.dpr_title as title, d.dpr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       d.dpr_description as description, d.dpr_active as active, d.dpr_expire_date as expireDate,\n" +
                        "       d.dpr_create_date as createDate, d.dpr_create_by as createBy, d.dpr_modify_date as modifyDate,\n" +
                        "       d.dpr_modify_by as modifyBy from sc_deposit_request d\n" +
                        "inner join sc_account a on (d.dpr_acc_id=a.acc_id)\n" +
                        "where d.dpr_id=:depositRequestId"
                ,resultSetMapping = "depositRequestWrapperMapping"),
        @NamedNativeQuery(name = "DepositRequest.findWrapperByIdAndUserId",
                query = "select d.dpr_id as id, d.dpr_usr_id as userId, d.dpr_title as title, d.dpr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       d.dpr_description as description, d.dpr_active as active, d.dpr_expire_date as expireDate,\n" +
                        "       d.dpr_create_date as createDate, d.dpr_create_by as createBy, d.dpr_modify_date as modifyDate,\n" +
                        "       d.dpr_modify_by as modifyBy from sc_deposit_request d\n" +
                        "inner join sc_account a on (d.dpr_acc_id=a.acc_id)\n" +
                        "where d.dpr_id=:depositRequestId and d.dpr_usr_id=:userId "
                ,resultSetMapping = "depositRequestWrapperMapping"),
        @NamedNativeQuery(name = "DepositRequest.findWrapperByUserId",
                query = "select d.dpr_id as id, d.dpr_usr_id as userId, d.dpr_title as title, d.dpr_acc_id as accountId,a.acc_name accountName,\n" +
                        "       d.dpr_description as description, d.dpr_active as active, d.dpr_expire_date as expireDate,\n" +
                        "       d.dpr_create_date as createDate, d.dpr_create_by as createBy, d.dpr_modify_date as modifyDate,\n" +
                        "       d.dpr_modify_by as modifyBy from sc_deposit_request d\n" +
                        "inner join sc_account a on (d.dpr_acc_id=a.acc_id)\n" +
                        "where d.dpr_usr_id=:userId order by d.dpr_create_date desc"
                ,resultSetMapping = "depositRequestWrapperMapping")
})
@Table(name= DepositRequest.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = DepositRequest.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "dpr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "dpr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "dpr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "dpr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "dpr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "dpr_modify_by"))
})
public class DepositRequest extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_deposit_request";


    @Column(name="dpr_usr_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dpr_acc_id",referencedColumnName = "acc_id", foreignKey = @ForeignKey(name = "FK_dpr_acc_id"))
    private Account account;

    @Column(name="dpr_title")
    private String title;

    @Column(name="dpr_description")
    private String description;

    @Column(name="dpr_active")
    private Boolean active;

    @Column(name="dpr_expire_date")
    private Date expireDate;


    @OneToMany(targetEntity=DepositRequestDetail.class,mappedBy = "depositRequest",fetch =FetchType.LAZY)
    @JsonIgnoreProperties("depositRequest")
    private List<DepositRequestDetail> depositRequestDetails = new ArrayList<>();


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
