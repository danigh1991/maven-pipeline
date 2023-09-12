package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.util.BaseUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = Notification.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Notification.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "nol_id")),
        @AttributeOverride(name = "version", column = @Column(name = "nol_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "nol_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "nol_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "nol_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "nol_modify_by"))
})
public class Notification extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 4857118082796914475L;
    public static final String TABLE_NAME = "sc_notification_log";

    public static enum Status {
        NEW,
        IN_PROCESS,
        PROCESSED,
        FAILED
    }

    public static enum Medium {
        NONE,
        POPUP,
        EMAIL,
        SMS,
        BASE_SMS
    }


    @Column(name = "nol_notify_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notifyDate;

    @Column(name = "nol_event_group_id")
    private Long eventGroupId;

    @Column(name="nol_subject",nullable = false)
    private String subject;

    @Column(name = "nol_message",nullable = false)
    private String message;

    @Column(name = "nol_status",nullable = false)
    private String status;

    @Column(name = "nol_medium",nullable = false)
    private String medium;

    @Column(name = "nol_target_type_id",nullable = false, updatable = false)
    private Long targrtTypeId;

    @Column(name = "nol_target_id",nullable = false, updatable = false)
    private Long targetId;

    @Column(name = "nol_recipilient_ref",nullable = false)
    private String recipientReference;

    @Column(name="nol_email_replyto")
    private String emailReplyTo;

    @Column(name="nol_email_cc")
    private String emailCC;

    @Column(name="nol_attachment")
    private String attachment;

    @Column(name = "nol_recipient_user_id")
    private Long recipientUserId;

    @Column(name="nol_remarks")
    private String remarks;

    @Column(name="nol_template_name")
    private String templateName;
    @Column(name="nol_template_data")
    private String templateData;


/*    @Transient
    private transient User recipientUser;*/

    @Transient
    private transient Date startDate;

    @Transient
    private transient Date endDate;

    public String getMessageDisplay() {
        String display = message;
        if (medium.equals(Medium.EMAIL)) {
            if (BaseUtils.isStringSafeEmpty(subject))
                display = "No Subject";
            else
                display = subject;
        }
        if (display.length()>70)
            display = display.substring(0, 70) + "...";
        return display;
    }


    /*public String getRecipientDisplay() {
        StringBuffer display = new StringBuffer();
        if (medium == null)
            return "No Medium Indicated";
        if (medium.equals(Medium.POPUP) && recipientUserId!=null)
            display.append("Notify "+ getRecipientUser().getFirstName() + " " +getRecipientUser().getLastName()).append(" ");
        if (medium.equals(Medium.EMAIL))
            display.append("Email to ").append(getRecipientUser().getFirstName() + " " +getRecipientUser().getLastName());
        if (medium.equals(Medium.SMS))
            display.append("SMS to").append(getRecipientUser().getFirstName() + " " +getRecipientUser().getLastName());
        return display.toString();
    }*/

}
