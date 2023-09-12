package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = Permission.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Permission.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "prm_id")),
        @AttributeOverride(name = "version", column = @Column(name = "prm_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "prm_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "prm_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "prm_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "prm_modify_by"))
})
public class Permission extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_permission";


    @JsonView(MyJsonView.PermissionList.class)
    @Column(name="prm_name",nullable = false)
    private String name;

    @JsonView(MyJsonView.PermissionList.class)
    @Column(name="prm_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PERMISSION)
    private String description;


    @ManyToMany(mappedBy = "permissions")
    @JsonIgnoreProperties("permissions")
    private List<Activity> activities = new ArrayList<>();

}
