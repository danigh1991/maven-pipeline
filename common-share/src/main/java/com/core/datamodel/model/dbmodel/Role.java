package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.ShareUtils;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = Role.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Role.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "rol_id")),
        @AttributeOverride(name = "version", column = @Column(name = "rol_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "rol_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "rol_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "rol_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "rol_modify_by"))
})
public class Role  extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_roles";

    @JsonView(MyJsonView.RoleShortList.class)
    @Column(name="rol_name",nullable = false)
    private String roleName;

    @JsonView(MyJsonView.RoleDetail.class)
    @Column(name="rol_description",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.ROLE)
    private String description;

    @JsonView(MyJsonView.RoleDetail.class)
    @Column(name="rol_is_default",nullable = false)
    private Boolean defaultRole;


    @JsonView(MyJsonView.RoleList.class)
    @Column(name="rol_active",nullable = false)
    private Boolean active;

    @JsonView(MyJsonView.RoleList.class)
    @Column(name="rol_type",nullable = false)
    private Integer roleType; //1=ADMIN , 2=USER


    @JsonView(MyJsonView.RoleDetail.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("activities")
    @JoinTable(name = "sc_role_activity",
            joinColumns = @JoinColumn(name = "rla_rol_id",nullable = false, referencedColumnName = "rol_id", foreignKey = @ForeignKey(name = "FK_rla_rol_id")),
            inverseJoinColumns = @JoinColumn(name = "rla_act_id",nullable = false,  referencedColumnName = "act_id", foreignKey = @ForeignKey(name = "FK_rla_act_id")))
    private List<Activity> activities=new ArrayList<>();

    @JsonView(MyJsonView.RoleDetail.class)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sc_fm_action_role",
            joinColumns = @JoinColumn(name = "fmr_rol_id",nullable = false, referencedColumnName = "rol_id",foreignKey = @ForeignKey(name = "FK_fmr_rol_id")),
            inverseJoinColumns = @JoinColumn(name = "fmr_fma_id",nullable = false, referencedColumnName = "fma_id", foreignKey = @ForeignKey(name = "FK_fmr_fma_id")))
    private List<FileManagerAction> fileManagerActions=new ArrayList<>();;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnoreProperties("roles")
    private List<User> users = new ArrayList<>();

/*    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sc_role_permissions",
            joinColumns = @JoinColumn(name = "rlp_rol_id",nullable = false, referencedColumnName = "rol_id", foreignKey = @ForeignKey(name = "FK_rlp_rol_id")),
            inverseJoinColumns = @JoinColumn(name = "rlp_prm_id",nullable = false,referencedColumnName = "prm_id", foreignKey = @ForeignKey(name = "FK_rlp_prm_id")))
    private List<Permission> permissions;*/

    @JsonView(MyJsonView.RoleList.class)
    public String getRoleTypeDesc(){
        if(this.getRoleType().equals(1))
            return ShareUtils.getMessageResource("common.roleType.admin");
        else if(this.getRoleType().equals(2))
            return ShareUtils.getMessageResource("common.roleType.user");
        else
            return ShareUtils.getMessageResource("global.status.unknown");
    }

}
