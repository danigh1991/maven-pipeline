package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = Configuration.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Configuration.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)

/*@SqlResultSetMapping(name = "configurationMapping",
               entities =  @EntityResult(
                entityClass = Configuration.class,
                fields = {
                        @FieldResult(name = "id", column = "cfg_id"),
                        @FieldResult(name = "createDate", column = "cfg_create_date"),
                        @FieldResult(name = "createBy", column = "cfg_create_by"),
                        @FieldResult(name = "modifyDate", column = "cfg_modify_date"),
                        @FieldResult(name = "modifyBy", column = "cfg_modify_by"),
                        @FieldResult(name = "version", column = "cfg_version"),
                        @FieldResult(name = "multiLingualVersion", column = "mlt_v") }))*/
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cfg_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cfg_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cfg_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cfg_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cfg_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cfg_modify_by"))

})
public class Configuration extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_configuration";

    @JsonView(MyJsonView.ConfigurationView.class)
    @Column(name = "cfg_name",nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cfg_cgt_id",nullable = false,referencedColumnName = "cgt_id", foreignKey = @ForeignKey(name = "FK_cfg_cgt_id"))
    @JsonIgnoreProperties("configurations")
    private ConfigurationType configurationType;

    @JsonView(MyJsonView.ConfigurationView.class)
    @Column(name = "cfg_num_value")
    private Long numValue;

    @JsonView(MyJsonView.ConfigurationView.class)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CONFIGURATION)
    @Column(name = "cfg_chr_value")
    private String chrValue;

    @JsonView({MyJsonView.ConfigurationView.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CONFIGURATION)
    @Column(name = "cfg_desc")
    private String desc;

    @JsonView(MyJsonView.ConfigurationView.class)
    @Column(name = "cfg_active ",nullable = false)
    private Boolean active=true;

    @Column(name = "cfg_encrypted",nullable = false)
    private Boolean encrypted;

    @JsonView(MyJsonView.ConfigurationView.class)
    @Column(name = "cfg_edit",nullable = false)
    private Boolean edit=true;

    @Column(name = "cfg_show",nullable = false)
    private Boolean show=true;

    @JsonView(MyJsonView.ConfigurationView.class)
    @Column(name = "cfg_ui_component")
    private String uiComponent;

    @JsonView(MyJsonView.ConfigurationView.class)
    @Column(name = "cfg_valid_values")
    private String validValues;


    public String getVersionString(){
        if (this.modifyDate==null)
            return "?v=1";
        return  "?v=" + String.valueOf(this.modifyDate.getTime()-createDate.getTime()==0?1:this.modifyDate.getTime()-createDate.getTime());
    }

    public Long getVersionNumber(){
        if (this.modifyDate==null)
            return 1l;
        return  this.modifyDate.getTime()-createDate.getTime()==0?1:this.modifyDate.getTime()-createDate.getTime();
    }


}
