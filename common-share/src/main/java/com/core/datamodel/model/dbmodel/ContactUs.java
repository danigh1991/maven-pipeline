package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.enums.EContactTarget;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name=ContactUs.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = ContactUs.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cnu_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cnu_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cnu_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cnu_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cnu_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cnu_modify_by"))
})
public class ContactUs extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_contact_us";


    @Column(name = "cnu_contact_Target",nullable = false)
    @JsonView({MyJsonView.ContactUsList.class})
    private Long contactTarget;

    @Column(name = "cnu_usr_id")
    @JsonView({MyJsonView.ContactUsDetails.class})
    private Long userId;

    @Column(name = "cnu_comment",nullable = false)
    @JsonView({MyJsonView.ContactUsList.class})
    private String comment;

    @Column(name = "cnu_email")
    @JsonView({MyJsonView.ContactUsDetails.class})
    private String email;

    @Column(name = "cnu_mobile_number")
    @JsonView({MyJsonView.ContactUsDetails.class})
    private String mobileNumber;

    @Column(name = "cnu_name")
    @JsonView({MyJsonView.ContactUsList.class})
    private String name;

    @Column(name = "cnu_ip")
    private String ip;

    @Column(name = "cnu_agent")
    private String agent;

    @Column(name = "cnu_machine_id")
    private String machineId;

    @Column(name = "cnu_reply")
    @JsonView({MyJsonView.ContactUsDetails.class})
    private String reply;

    @Column(name = "cnu_reply_date")
    @JsonView({MyJsonView.ContactUsDetails.class})
    private Date replyDate;

    @Column(name = "cnu_lng_id",nullable = false)
    @JsonView({MyJsonView.ContactUsDetails.class})
    private Long languageId;

    @Column(name = "cnu_tracking_code")
    private String trackingCode;


    @JsonView({MyJsonView.ContactUsList.class})
    public String  getContactTargetCaption() {
        return EContactTarget.captionOf(getContactTarget().intValue());
    }

    @JsonView({MyJsonView.ContactUsList.class})
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getCommentDate() {
        return this.createDate;
    }

    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getReplyDate() {
        return replyDate;
    }

}
