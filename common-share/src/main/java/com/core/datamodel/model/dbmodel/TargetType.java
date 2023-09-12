package com.core.datamodel.model.dbmodel;


import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = TargetType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = TargetType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "trt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "trt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "trt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "trt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "trt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "trt_modify_by"))
})
public class TargetType extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_target_types";

    @Column(name="trt_name",nullable = false)
    private String name;

    @Column(name = "trt_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.TARGET_TYPE)
    private String description;


}
