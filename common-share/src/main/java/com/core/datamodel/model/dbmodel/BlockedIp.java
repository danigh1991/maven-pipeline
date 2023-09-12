package com.core.datamodel.model.dbmodel;

import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name=BlockedIp.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = BlockedIp.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bip_id")),
        @AttributeOverride(name = "version", column = @Column(name = "bip_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "bip_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "bip_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "bip_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "bip_modify_by"))
})
public class BlockedIp extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_blocked_ip";

    @Column(name = "bip_ip",nullable = false)
    private String ip;

    @Column(name = "bip_active",nullable = false)
    private Boolean active;

    @Column(name = "bip_description")
    private String description;

    @Column(name = "bip_last_req_agent")
    private String lastRequestAgent	;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

        @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }
}
