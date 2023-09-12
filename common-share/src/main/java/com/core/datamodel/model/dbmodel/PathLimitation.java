package com.core.datamodel.model.dbmodel;

import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = PathLimitation.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = PathLimitation.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "pli_id")),
        @AttributeOverride(name = "version", column = @Column(name = "pli_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "pli_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "pli_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "pli_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "pli_modify_by"))
})
public class PathLimitation extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_path_limitation";

    @Column(name = "pli_name",nullable = false)
    private String name;

    @Column(name = "pli_path",nullable = false)
    private String path;

    @Column(name = "pli_use_cookie",nullable = false)
    private Boolean useCookie;

    @Column(name = "pli_visible_captcha_count",nullable = false)
    private Integer visibleCaptchaCount	;

    @Column(name = "pli_add_to_warning_count",nullable = false)
    private Integer addToWarningCount;

    @Column(name = "pli_add_to_block_count",nullable = false)
    private Integer addToBlockCount	;

    @Column(name = "pli_time_size",nullable = false)
    private Integer timeSize	;

    @Column(name = "pli_active",nullable = false)
    private Boolean active;

    @Column(name = "pli_description")
    private String description;

    @Column(name = "pli_priority",nullable = false)
    private Integer priority;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
