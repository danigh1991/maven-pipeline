package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.wrapper.UserLogWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "userLogWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = UserLogWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "userId", type = Long.class),
                                        @ColumnResult(name = "loginDate", type = Date.class),
                                        @ColumnResult(name = "ip", type = String.class),
                                        @ColumnResult(name = "agent", type = String.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "UserLog.findWrapperByUserId",
                query = "SELECT ul.usl_id AS id, ul.usl_usr_id AS userId, ul.usl_create_date AS loginDate,ul.usl_ip AS ip,ul.usl_agent as agent FROM sc_user_log ul\n" +
                        "where ul.usl_usr_id=:userId ORDER BY ul.usl_create_date desc limit 15",
                resultSetMapping = "userLogWrapperMapping")
})

@Table(name = UserLog.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserLog.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "usl_id")),
        @AttributeOverride(name = "version", column = @Column(name = "usl_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "usl_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "usl_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "usl_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "usl_modify_by"))
})
public class UserLog extends AbstractBaseEntity<Long> {
	private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_log";

    @Column(name = "usl_usr_id",nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "usl_ual_id",nullable = false,referencedColumnName = "ual_id", foreignKey = @ForeignKey(name = "FK_usl_ual_id"))
    @JsonIgnoreProperties("userLogs")
    private UserActions userAction;

    @Column(name = "usl_ip")
    private String ip;

    @Column(name = "usl_machine_id")
    private String machineId;

    @Column(name = "usl_agent")
    private String agent;

    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getLogDate() {
        return createDate;
    }

}

