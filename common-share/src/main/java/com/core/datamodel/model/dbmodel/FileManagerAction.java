package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = FileManagerAction.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = FileManagerAction.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "fma_id")),
        @AttributeOverride(name = "version", column = @Column(name = "fma_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "fma_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "fma_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "fma_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "fma_modify_by"))
})
public class FileManagerAction extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_fm_action";

    @JsonView({MyJsonView.FileManagerActionList.class})
    @Column(name = "fma_name",nullable = false)
    private String name;

    @JsonView({MyJsonView.FileManagerActionList.class})
    @Column(name = "fma_value")
    private String value;

    @JsonView({MyJsonView.FileManagerActionDetails.class})
    @Column(name = "fma_desc")
    private String description;


}
