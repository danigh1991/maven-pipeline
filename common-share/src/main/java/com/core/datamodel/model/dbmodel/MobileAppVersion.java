package com.core.datamodel.model.dbmodel;

import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = MobileAppVersion.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = MobileAppVersion.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "mav_id")),
        @AttributeOverride(name = "version", column = @Column(name = "mav_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "mav_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "mav_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "mav_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "mav_modify_by"))
})
public class MobileAppVersion extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_mobile_app_version";

    @Column(name = "mav_app_version",nullable = false)
    private String appVersion;

    @Column(name = "mav_force_update",nullable = false)
    private Boolean forceUpdate;

    @Column(name = "mav_active",nullable = false)
    private Boolean active;

    @Column(name = "mav_ios_path")
    private String iosPath;

    @Column(name = "mav_android_path")
    private String androidPath;

    @Column(name = "mav_web_path")
    private String webPath;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
