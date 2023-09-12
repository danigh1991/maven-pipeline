package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = RedirectUrl.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = RedirectUrl.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "rdu_id")),
        @AttributeOverride(name = "version", column = @Column(name = "rdu_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "rdu_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "rdu_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "rdu_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "rdu_modify_by"))
})
public class RedirectUrl extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_redirect_url";

    @Column(name = "rdu_desc",nullable = false)
    private String desc;

    @Column(name = "rdu_from",nullable = false)
    private String from ;

    @Column(name = "rdu_to",nullable = false)
    private String to;

    @Column(name = "rdu_move_type",nullable = false)
    private Integer moveType;

    @Column(name = "rdu_active",nullable = false)
    private Boolean active=false;

}
