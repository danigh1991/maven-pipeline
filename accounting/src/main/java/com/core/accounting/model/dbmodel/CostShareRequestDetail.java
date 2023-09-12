package com.core.accounting.model.dbmodel;

import com.core.accounting.model.wrapper.CostShareRequestDetailWrapper;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
                name="costShareRequestDetailWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= CostShareRequestDetailWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="costShareRequestId",type = Long.class),
                                        @ColumnResult(name="userId",type = Long.class),
                                        @ColumnResult(name="targetUser",type = String.class),
                                        @ColumnResult(name="amount",type = Double.class),
                                        @ColumnResult(name="doneDate",type = Date.class),
                                        @ColumnResult(name="notifyCount",type = Integer.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name="costShareRequestDetailMessageWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= MessageBoxWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="senderUserId",type = Long.class),
                                        @ColumnResult(name="senderUserName",type = String.class),
                                        @ColumnResult(name="targetUserId",type = Long.class),
                                        @ColumnResult(name="notifyTargetType",type = Integer.class),
                                        @ColumnResult(name="title",type = String.class),
                                        @ColumnResult(name="description",type = String.class),
                                        @ColumnResult(name="seenDate",type = Date.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class),
                                        @ColumnResult(name="additional",type = String.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "CostShareRequestDetail.findWrapperById",
                query = "select c.csd_id as id, c.csd_csr_id as costShareRequestId,c.csd_usr_id as userId,u.usr_username as targetUser , c.csd_amount as amount,\n" +
                        "       c.csd_done_date as doneDate, c.csd_notify_count as notifyCount,\n" +
                        "       c.csd_create_date as createDate, c.csd_create_by as createBy, c.csd_modify_date as modifyDate,\n" +
                        "       c.csd_modify_by as modifyBy  from sc_cost_share_request_detail c\n" +
                        "inner join sc_user u on (c.csd_usr_id=u.usr_id)\n" +
                        "where c.csd_id=:costShareRequestDetailId"
                ,resultSetMapping = "costShareRequestDetailWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequestDetail.findWrapperByIdAndUserId",
                query = "select c.csd_id as id, c.csd_csr_id as costShareRequestId,c.csd_usr_id as userId,u.usr_username as targetUser , c.csd_amount as amount,\n" +
                        "       c.csd_done_date as doneDate, c.csd_notify_count as notifyCount,\n" +
                        "       c.csd_create_date as createDate, c.csd_create_by as createBy, c.csd_modify_date as modifyDate,\n" +
                        "       c.csd_modify_by as modifyBy  from sc_cost_share_request_detail c\n" +
                        "inner join sc_cost_share_request r on(c.csd_csr_id=r.csr_id) \n" +
                        "inner join sc_user u on (c.csd_usr_id=u.usr_id)\n" +
                        "where c.csd_id=:costShareRequestDetailId and r.csr_usr_id=:userId"
                ,resultSetMapping = "costShareRequestDetailWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequestDetail.findWrappersByCostShareRequestId",
                query = "select c.csd_id as id, c.csd_csr_id as costShareRequestId,c.csd_usr_id as userId,u.usr_username as targetUser , c.csd_amount as amount,\n" +
                        "       c.csd_done_date as doneDate, c.csd_notify_count as notifyCount,\n" +
                        "       c.csd_create_date as createDate, c.csd_create_by as createBy, c.csd_modify_date as modifyDate,\n" +
                        "       c.csd_modify_by as modifyBy  from sc_cost_share_request_detail c\n" +
                        "inner join sc_user u on (c.csd_usr_id=u.usr_id)\n" +
                        "where c.csd_csr_id=:costShareRequestId order by c.csd_id asc"
                ,resultSetMapping = "costShareRequestDetailWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequestDetail.findMessageBoxWrappersByCostShareRequestDetailId",
                query = "select c.csd_id as id, r.csr_usr_id as senderUserId, u.usr_username as senderUserName, c.csd_usr_id as targetUserId,3 as notifyTargetType, r.csr_title as title, r.csr_description as description, c.csd_seen_date as seenDate,\n" +
                        "       c.csd_create_date as createDate, c.csd_create_by as createBy ,c.csd_modify_date as modifyDate,c.csd_modify_by as modifyBy,\n" +
                        "       concat('expireDate[-]global.expireDate[-]date[-]show[=]',DATE_FORMAT(r.csr_expire_date ,'%d-%m-%Y %T'),'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when r.csr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',c.csd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',r.csr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(c.csd_done_date,''), '[,]costShareType[-]global.topic[-]string[-]show[=]',nvl(t.cst_title,''))  as additional \n" +
                        "   from sc_cost_share_request r\n" +
                        "   inner join sc_cost_share_type t on (r.csr_cst_id=t.cst_id)\n" +
                        "   inner join sc_cost_share_request_detail c on (r.csr_id=c.csd_csr_id)\n" +
                        "   inner join sc_user u on (r.csr_usr_id=u.usr_id)\n" +
                        "  where r.csr_active>0 and c.csd_id=:costShareRequestDetailId"
                ,resultSetMapping = "costShareRequestDetailMessageWrapperMapping"),
        @NamedNativeQuery(name = "CostShareRequestDetail.findMessageBoxWrappersByCostShareRequestDetailIdAndUserId",
                query = "select c.csd_id as id, r.csr_usr_id as senderUserId, u.usr_username as senderUserName, c.csd_usr_id as targetUserId,3 as notifyTargetType, r.csr_title as title, r.csr_description as description, c.csd_seen_date as seenDate,\n" +
                        "       c.csd_create_date as createDate, c.csd_create_by as createBy ,c.csd_modify_date as modifyDate,c.csd_modify_by as modifyBy,\n" +
                        "       concat('expireDate[-]global.expireDate[-]date[-]show[=]',DATE_FORMAT(r.csr_expire_date ,'%d-%m-%Y %T'),'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when r.csr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',c.csd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',r.csr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(c.csd_done_date,''),'[,]costShareType[-]global.topic[-]string[-]show[=]',nvl(t.cst_title,''))  as additional \n" +
                        "   from sc_cost_share_request r\n" +
                        "   inner join sc_cost_share_type t on (r.csr_cst_id=t.cst_id)\n" +
                        "   inner join sc_cost_share_request_detail c on (r.csr_id=c.csd_csr_id)\n" +
                        "   inner join sc_user u on (r.csr_usr_id=u.usr_id)\n" +
                        "  where r.csr_active>0 and c.csd_id=:costShareRequestDetailId and c.csd_usr_id=:userId"
                ,resultSetMapping = "costShareRequestDetailMessageWrapperMapping")
})
@Table(name= CostShareRequestDetail.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = CostShareRequestDetail.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "csd_id")),
        @AttributeOverride(name = "version", column = @Column(name = "csd_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "csd_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "csd_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "csd_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "csd_modify_by"))
})
public class CostShareRequestDetail extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_cost_share_request_detail";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csd_csr_id",referencedColumnName = "csr_id", foreignKey = @ForeignKey(name = "fk_csd_csr_id"))
    @JsonIgnoreProperties("costShareRequestDetails")
    private CostShareRequest costShareRequest;

    @Column(name="csd_usr_id")
    private Long userId;

    @Column(name="csd_amount")
    private Double amount;

    @Column(name="csd_done_date")
    private Date doneDate;

    @Column(name="csd_notify_count")
    private Integer notifyCount;

    @Column(name="csd_seen_date")
    private Date seenDate;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
