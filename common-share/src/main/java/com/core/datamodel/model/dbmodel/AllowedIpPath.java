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
@Table(name= AllowedIpPath.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = AllowedIpPath.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "aip_id")),
        @AttributeOverride(name = "version", column = @Column(name = "aip_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "aip_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "aip_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "aip_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "aip_modify_by"))
})
public class AllowedIpPath extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_allowed_ip_path";

    @Column(name = "aip_ip",nullable = false)
    private String ip;

    @Column(name = "aip_path",nullable = false)
    private String path;

    @Column(name = "aip_active",nullable = false)
    private Boolean active;

    @Column(name = "aip_description")
    private String description;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }
}
