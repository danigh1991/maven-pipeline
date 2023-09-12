package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.datamodel.util.json.JsonDateShortSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
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
        name="messageBoxWrapperMapping",
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
@NamedNativeQueries({
        @NamedNativeQuery(name = "MessageBox.findNoSeenCountByReceiverId",
                query = "select count(*) from\n" +
                        "  (select 1 as type, m.msg_id as id, m.msg_sender_id as senderUserId, m.msg_receiver_id as targetUserId, m.msg_subject as title, m.msg_seen_date as seenDate,m.msg_create_date as createDate,\n" +
                        "     m.msg_create_by as createBy ,m.msg_modify_date as modifyDate,m.msg_modify_by as modifyBy, '' as additional \n" +
                        "   from sc_message_box m where m.msg_receiver_id=:receiverId and m.msg_seen_date is null\n" +
                        "   union\n" +
                        "   select 2 as type, d.drd_id as id, r.dpr_usr_id as senderUserId, d.drd_usr_id as targetUserId, r.dpr_title as title, d.drd_seen_date as seenDate,d.drd_create_date as createDate,\n" +
                        "     d.drd_create_by as createBy ,d.drd_modify_date as modifyDate,d.drd_modify_by as modifyBy,\n" +
                        "     ''  as additional \n" +
                        "   from sc_deposit_request r \n" +
                        "   inner join sc_deposit_request_detail d on (r.dpr_id=d.drd_dpr_id) \n" +
                        "   where r.dpr_active>0  and d.drd_seen_date is null and d.drd_usr_id=:receiverId \n" +
                        "   union\n" +
                        "   select 3 as type, dr.csd_id as id, cr.csr_usr_id as senderUserId, dr.csd_usr_id as targetUserId, cr.csr_title as title, dr.csd_seen_date as seenDate, dr.csd_create_date as createDate,\n" +
                        "     dr.csd_create_by as createBy , dr.csd_modify_date as modifyDate, dr.csd_modify_by as modifyBy,\n" +
                        "     ''  as additional \n" +
                        "   from sc_cost_share_request cr \n" +
                        "   inner join sc_cost_share_request_detail dr  on (cr.csr_id=dr.csd_csr_id) \n" +
                        "   where cr.csr_active>0  and dr.csd_seen_date is null and dr.csd_usr_id=:receiverId) k" ),
        @NamedNativeQuery(name = "MessageBox.findWrapperByReceiverId",
                query = "select k.id,k.senderUserId, u.usr_username as senderUserName, k.targetUserId,k.type as notifyTargetType,k.title,'' as description,k.seendate,k.createDate,k.createBy,k.modifyDate,k.modifyBy,k.additional from\n" +
                        " (select 1 as type, m.msg_id as id, m.msg_sender_id as senderUserId, m.msg_receiver_id as targetUserId, m.msg_subject as title, m.msg_seen_date as seenDate,m.msg_create_date as createDate," +
                        "   m.msg_create_by as createBy ,m.msg_modify_date as modifyDate,m.msg_modify_by as modifyBy, '' as additional \n" +
                        " from sc_message_box m where m.msg_receiver_id=:receiverId \n" +
                        " union\n" +
                        " select 2 as type, d.drd_id as id, r.dpr_usr_id as senderUserId, d.drd_usr_id as targetUserId, r.dpr_title as title, d.drd_seen_date as seenDate,d.drd_create_date as createDate," +
                        "   d.drd_create_by as createBy ,d.drd_modify_date as modifyDate,d.drd_modify_by as modifyBy,\n" +
                        "   concat('expireDate[-]global.expireDate[-]date[-]show[=]',r.dpr_expire_date,'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when r.dpr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',d.drd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',r.dpr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(d.drd_done_date,''))  as additional \n" +
                        " from sc_deposit_request r \n" +
                        " inner join sc_deposit_request_detail d on (r.dpr_id=d.drd_dpr_id) \n" +
                        " inner join sc_user u on (r.dpr_usr_id=u.usr_id)\n" +
                        "    where r.dpr_active>0 and d.drd_usr_id=:receiverId \n" +
                        " union \n" +
                        " select 3 as type, dr.csd_id as id, cr.csr_usr_id as senderUserId, dr.csd_usr_id as targetUserId, cr.csr_title as title, dr.csd_seen_date as seenDate,dr.csd_create_date as createDate,\n" +
                        "   dr.csd_create_by as createBy ,dr.csd_modify_date as modifyDate,dr.csd_modify_by as modifyBy,\n" +
                        "   concat('expireDate[-]global.expireDate[-]date[-]show[=]',cr.csr_expire_date,'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when cr.csr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',dr.csd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',cr.csr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(dr.csd_done_date,''))  as additional \n" +
                        " from sc_cost_share_request cr \n" +
                        " inner join sc_cost_share_request_detail dr on (cr.csr_id=dr.csd_csr_id) \n" +
                        " inner join sc_user u on (cr.csr_usr_id=u.usr_id)\n" +
                        "    where cr.csr_active>0 and dr.csd_usr_id=:receiverId  ) k\n" +
                        " inner join sc_user u on (k.senderUserId=u.usr_id)\n" +
                        "order by k.createdate desc"
                ,resultSetMapping = "messageBoxWrapperMapping"),
        @NamedNativeQuery(name = "MessageBox.findNoSeenWrapperByReceiverId",
                query = "select k.id,k.senderUserId, u.usr_username as senderUserName, k.targetUserId,k.type as notifyTargetType,k.title,'' as description,k.seendate,k.createDate,k.createBy,k.modifyDate,k.modifyBy,k.additional from\n" +
                        " (select 1 as type, m.msg_id as id, m.msg_sender_id as senderUserId, m.msg_receiver_id as targetUserId, m.msg_subject as title, m.msg_seen_date as seenDate,m.msg_create_date as createDate," +
                        "   m.msg_create_by as createBy ,m.msg_modify_date as modifyDate,m.msg_modify_by as modifyBy, '' as additional \n" +
                        " from sc_message_box m where m.msg_receiver_id=:receiverId and m.msg_seen_date is null\n" +
                        " union\n" +
                        " select 2 as type, d.drd_id as id, r.dpr_usr_id as senderUserId, d.drd_usr_id as targetUserId, r.dpr_title as title, d.drd_seen_date as seenDate,d.drd_create_date as createDate," +
                        "   d.drd_create_by as createBy ,d.drd_modify_date as modifyDate,d.drd_modify_by as modifyBy,\n" +
                        "   concat('expireDate[-]global.expireDate[-]date[-]show[=]',r.dpr_expire_date,'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when r.dpr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',d.drd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',r.dpr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(d.drd_done_date,''))  as additional \n" +
                        " from sc_deposit_request r \n" +
                        " inner join sc_deposit_request_detail d on (r.dpr_id=d.drd_dpr_id) \n" +
                        " inner join sc_user u on (r.dpr_usr_id=u.usr_id)\n" +
                        "    where r.dpr_active>0 and d.drd_seen_date is null and d.drd_usr_id=:receiverId \n" +
                        " union \n" +
                        " select 3 as type, dr.csd_id as id, cr.csr_usr_id as senderUserId, dr.csd_usr_id as targetUserId, cr.csr_title as title, dr.csd_seen_date as seenDate,dr.csd_create_date as createDate,\n" +
                        "   dr.csd_create_by as createBy ,dr.csd_modify_date as modifyDate,dr.csd_modify_by as modifyBy,\n" +
                        "   concat('expireDate[-]global.expireDate[-]date[-]show[=]',cr.csr_expire_date,'[,]isExpired[-]common.expired[-]boolean[-]notShow[=]',case when cr.csr_expire_date>=UTC_TIMESTAMP() then '0' else '1' end,'[,]amount[-]global.amount[-]money[-]show[=]',dr.csd_amount,'[,]accountId[-]accountId[-]long[-]notShow[=]',cr.csr_acc_id,'[,]doneDate[-]global.doneDate[-]datetime[-]show[=]',ifnull(dr.csd_done_date,''))  as additional\n" +
                        " from sc_cost_share_request cr \n" +
                        " inner join sc_cost_share_request_detail dr on (cr.csr_id=dr.csd_csr_id) \n" +
                        "    where cr.csr_active>0 and dr.csd_seen_date is null and dr.csd_usr_id=:receiverId ) k\n" +
                        " inner join sc_user u on (k.senderUserId=u.usr_id)\n" +
                        "order by k.createdate desc\n"
                ,resultSetMapping = "messageBoxWrapperMapping")
})
@Table(name = MessageBox.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = MessageBox.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "msg_id")),
        @AttributeOverride(name = "version", column = @Column(name = "msg_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "msg_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "msg_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "msg_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "msg_modify_by"))
})
public class MessageBox extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_message_box";

    @Column(name = "msg_mst_id",nullable = false)
    private Long messageType;

    @Column(name = "msg_subject",nullable = false)
    private String subject;

    @Column(name = "msg_body")
    private String body;

    @Column(name = "msg_sender_id",nullable = false)
    private Long senderId;

    @Column(name = "msg_receiver_id",nullable = false)
    private Long receiverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msg_reply_to")
    @JsonIgnoreProperties("replies")
    private MessageBox replyTo;

    @OneToMany(targetEntity = MessageBox.class, mappedBy = "replyTo", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("replyTo")
    private List<MessageBox> replies=new ArrayList<>();

    @Column(name = "msg_seen_date")
    private Date seenDate;



    @JsonSerialize(using = JsonDateShortSerializer.class)
    public Date getSeenDate() {
        return seenDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }



}
