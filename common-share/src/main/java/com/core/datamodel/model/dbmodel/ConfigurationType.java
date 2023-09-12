package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = ConfigurationType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = ConfigurationType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cgt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cgt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cgt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cgt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cgt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cgt_modify_by"))
})
public class ConfigurationType extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_configuration_type";

    @JsonView({MyJsonView.ConfigurationView.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CONFIGURATION_TYPE)
    @Column(name = "cgt_name",nullable = false)
    private String name;

    @Column(name = "cgt_sys_name")
    private String sysName;

    @Where(clause = "cfg_show=1")
    @JsonView(MyJsonView.ConfigurationView.class)
    @OneToMany(targetEntity = Configuration.class, mappedBy = "configurationType", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("configurationType")
    private List<Configuration> configurations;


}
