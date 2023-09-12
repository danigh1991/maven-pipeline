package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

//import javax.persistence.*;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = UserDeviceMetadata.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserDeviceMetadata.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)

@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "udd_id")),
        @AttributeOverride(name = "version", column = @Column(name = "udd_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "udd_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "udd_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "udd_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "udd_modify_by"))
})
public class UserDeviceMetadata extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_device_metadata";

    @Column(name="udd_usr_id",nullable = false)
    private Long userId;
    @JsonView(MyJsonView.UserDeviceMetadataView.class)
    @Column(name="udd_device_detail",nullable = false)
    private String deviceDetail;
    @JsonView(MyJsonView.UserDeviceMetadataView.class)
    @Column(name="udd_ip",nullable = false)
    private String ip;
    @JsonView(MyJsonView.UserDeviceMetadataView.class)
    @Column(name="udd_location",nullable = false)
    private String location;

    @Column(name="udd_last_logged_date",nullable = false)
    private Date lastLoggedDate;
    @Column(name="udd_last_request_date",nullable = false)
    private Date lastRequestDate;
    @Column(name="udd_expire_date",nullable = false)
    private Date expireDate;

    @Column(name="udd_refresh_token")
    private String refreshToken;


    @JsonView(MyJsonView.UserDeviceMetadataView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getLastLoggedDate() {
        return lastLoggedDate;
    }

    @JsonView(MyJsonView.UserDeviceMetadataView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getLastRequestDate() {
        return lastRequestDate;
    }

    @JsonView(MyJsonView.UserDeviceMetadataView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }
}
