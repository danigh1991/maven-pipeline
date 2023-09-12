package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name= Feedback.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Feedback.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "fbk_id")),
        @AttributeOverride(name = "version", column = @Column(name = "fbk_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "fbk_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "fbk_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "fbk_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "fbk_modify_by"))
})
public class Feedback extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_feedback";

    @Column(name = "fbk_usr_id")
    @JsonView({MyJsonView.FeedBackDetail.class})
    private Long userId;

    @JsonView({MyJsonView.FeedBackDetail.class})
    @Column(name = "fbk_comment",nullable = false)
    private String comment;

    @JsonView({MyJsonView.FeedBackDetail.class})
    @Column(name = "fbk_ip")
    private String ip;

    @Column(name = "fbk_agent")
    private String agent;

    @Column(name = "fbk_machine_id")
    private String machineId;

    @Column(name = "fbk_lng_id",nullable = false)
    @JsonView({MyJsonView.ContactUsDetails.class})
    private Long languageId;

    @Column(name = "fbk_tracking_code")
    private String trackingCode;

    @JsonView({MyJsonView.ContactUsList.class})
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView({MyJsonView.ContactUsList.class})
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
