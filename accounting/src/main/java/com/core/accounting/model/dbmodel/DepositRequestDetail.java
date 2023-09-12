package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.DepositRequestDetailWrapper;
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
                name="depositRequestDetailWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= DepositRequestDetailWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="depositRequestId",type = Long.class),
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
                name="depositRequestDetailMessageWrapperMapping",
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
        @NamedNativeQuery(name = "DepositRequestDetail.findWrapperById",
                query = "select d.drd_id as id, d.drd_dpr_id as depositRequestId,d.drd_usr_id as userId,nvl(u.usr_username,d.drd_target_user) as targetUser , d.drd_amount as amount,\n" +
                        "       d.drd_done_date as doneDate, d.drd_notify_count as notifyCount,\n" +
                        "       d.drd_create_date as createDate, d.drd_create_by as createBy, d.drd_modify_date as modifyDate,\n" +
                        "       d.drd_modify_by as modifyBy  from sc_deposit_request_detail d\n" +
                        "left join sc_user u on (d.drd_usr_id=u.usr_id)\n" +
                        "where d.drd_id=:depositRequestDetailId"
                ,resultSetMapping = "depositRequestDetailWrapperMapping"),
        @NamedNativeQuery(name = "DepositRequestDetail.findWrapperByIdAndUserId",
                query = "select d.drd_id as id, d.drd_dpr_id as depositRequestId,d.drd_usr_id as userId,nvl(u.usr_username,d.drd_target_user) as targetUser , d.drd_amount as amount,\n" +
                        "       d.drd_done_date as doneDate, d.drd_notify_count as notifyCount,\n" +
                        "       d.drd_create_date as createDate, d.drd_create_by as createBy, d.drd_modify_date as modifyDate,\n" +
                        "       d.drd_modify_by as modifyBy  from sc_deposit_request_detail d\n" +
                        "inner join sc_deposit_request r on(d.drd_dpr_id=r.dpr_id) \n" +
                        "left join sc_user u on (d.drd_usr_id=u.usr_id)\n" +
                        "where d.drd_id=:depositRequestDetailId and r.dpr_usr_id=:userId"
                ,resultSetMapping = "depositRequestDetailWrapperMapping"),
        @NamedNativeQuery(name = "DepositRequestDetail.findWrappersByDepositRequestId",
                query = "select d.drd_id as id, d.drd_dpr_id as depositRequestId,d.drd_usr_id as userId,nvl(u.usr_username,d.drd_target_user) as targetUser , d.drd_amount as amount,\n" +
                        "       d.drd_done_date as doneDate, d.drd_notify_count as notifyCount,\n" +
                        "       d.drd_create_date as createDate, d.drd_create_by as createBy, d.drd_modify_date as modifyDate,\n" +
                        "       d.drd_modify_by as modifyBy  from sc_deposit_request_detail d\n" +
                        "left join sc_user u on (d.drd_usr_id=u.usr_id)\n" +
                        "where d.drd_dpr_id=:depositRequestId order by d.drd_id asc"
                ,resultSetMapping = "depositRequestDetailWrapperMapping"),
        @NamedNativeQuery(name = "DepositRequestDetail.findMessageBoxWrappersByDepositRequestDetailId",
                query = "select d.drd_id as id, r.dpr_usr_id as senderUserId, u.usr_username as senderUserName, d.drd_usr_id as targetUserId,2 as notifyTargetType, r.dpr_title as title, r.dpr_description as description, d.drd_seen_date as seenDate,\n" +
                        "       d.drd_create_date as createDate, d.drd_create_by as createBy ,d.drd_modify_date as modifyDate,d.drd_modify_by as modifyBy,\n" +
                        "       concat('expireDate[-]global.expireDate[-]date[-]show[=]',DATE_FORMAT(r.dpr_expire_date ,'%d-%m-%Y %T'),'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when r.dpr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',d.drd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',r.dpr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(d.drd_done_date,''))  as additional \n" +
                        "   from sc_deposit_request r\n" +
                        "   inner join sc_deposit_request_detail d on (r.dpr_id=d.drd_dpr_id)\n" +
                        "   inner join sc_user u on (r.dpr_usr_id=u.usr_id)\n" +
                        "  where r.dpr_active>0 and d.drd_id=:depositRequestDetailId"
                ,resultSetMapping = "depositRequestDetailMessageWrapperMapping"),
        @NamedNativeQuery(name = "DepositRequestDetail.findMessageBoxWrappersByDepositRequestDetailIdAndUserId",
                query = "select d.drd_id as id, r.dpr_usr_id as senderUserId, u.usr_username as senderUserName, d.drd_usr_id as targetUserId,2 as notifyTargetType, r.dpr_title as title, r.dpr_description as description, d.drd_seen_date as seenDate,\n" +
                        "       d.drd_create_date as createDate, d.drd_create_by as createBy ,d.drd_modify_date as modifyDate,d.drd_modify_by as modifyBy,\n" +
                        "       concat('expireDate[-]global.expireDate[-]date[-]show[=]',DATE_FORMAT(r.dpr_expire_date ,'%d-%m-%Y %T'),'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when r.dpr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',d.drd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',r.dpr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(d.drd_done_date,''))  as additional \n" +
                        "   from sc_deposit_request r\n" +
                        "   inner join sc_deposit_request_detail d on (r.dpr_id=d.drd_dpr_id)\n" +
                        "   inner join sc_user u on (r.dpr_usr_id=u.usr_id)\n" +
                        "  where r.dpr_active>0 and d.drd_id=:depositRequestDetailId and d.drd_usr_id=:userId"
                ,resultSetMapping = "depositRequestDetailMessageWrapperMapping")
})
@Table(name= DepositRequestDetail.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = DepositRequestDetail.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "drd_id")),
        @AttributeOverride(name = "version", column = @Column(name = "drd_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "drd_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "drd_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "drd_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "drd_modify_by"))
})
public class DepositRequestDetail extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_deposit_request_detail";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drd_dpr_id",referencedColumnName = "dpr_id", foreignKey = @ForeignKey(name = "FK_drd_dpr_id"))
    @JsonIgnoreProperties("depositRequestDetails")
    private DepositRequest depositRequest;

    @Column(name="drd_usr_id")
    private Long userId;

    @Column(name="drd_amount")
    private Double amount;

    @Column(name="drd_done_date")
    private Date doneDate;

    @Column(name="drd_notify_count")
    private Integer notifyCount;

    @Column(name="drd_seen_date")
    private Date seenDate;

    @Column(name="drd_target_user")
    private String targetUser;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
