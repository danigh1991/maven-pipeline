package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.model.wrapper.ActivityWrapper;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="panelMenuWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= ActivityWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="title",type = String.class),
                                        @ColumnResult(name="icon",type = String.class),
                                        @ColumnResult(name="description",type = String.class),
                                        @ColumnResult(name="url",type = String.class),
                                        @ColumnResult(name="modal",type = Boolean.class),
                                        @ColumnResult(name="blank",type = Boolean.class),
                                        @ColumnResult(name="passParameter",type = String.class),
                                        @ColumnResult(name="panelType",type = Integer.class),
                                        @ColumnResult(name="actOrder",type = Integer.class)
                                }
                        )
                }
        )})
@NamedNativeQueries({
        @NamedNativeQuery(name = "Activity.findPanelMenuByRoleIds",
                query = "select distinct p.act_id as id, p.act_title as title,p.act_icon as icon, p.act_desc as description ,p.act_url as url, p.act_modal as modal , p.act_blank as blank \n" +
                        "  ,p.act_pass_parameter as passParameter , p.act_panel_type as paneltype, p.act_order as actOrder  \n" +
                        "from sc_activity p INNER JOIN sc_role_activity ra on (p.act_id=ra.rla_act_id)  where p.act_active=true and p.act_panel_menu=true and p.act_panel_type =:panelType AND p.act_parent_id is null and  ra.rla_rol_id  in :roleIds order by p.act_order",
                resultSetMapping = "panelMenuWrapperMapping"),
        @NamedNativeQuery(name = "Activity.findChildPanelMenuByRoleIds",
                query = "select distinct p.act_id as id, p.act_title as title,p.act_icon as icon, p.act_desc as description ,p.act_url as url, p.act_modal as modal , p.act_blank as blank \n" +
                        "  ,p.act_pass_parameter as passParameter , p.act_panel_type as paneltype, p.act_order as actOrder  \n" +
                        "from sc_activity p INNER JOIN sc_role_activity ra on (p.act_id=ra.rla_act_id)  where p.act_active=true and p.act_panel_menu=true and p.act_panel_type =:panelType AND p.act_parent_id =:parentId and  ra.rla_rol_id  in :roleIds order by p.act_order",
                resultSetMapping = "panelMenuWrapperMapping")

})
@Table(name = Activity.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Activity.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "act_id")),
        @AttributeOverride(name = "version", column = @Column(name = "act_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "act_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "act_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "act_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "act_modify_by"))
})
public class Activity extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_activity";

    @JsonView(MyJsonView.ActivityList.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_parent_id",referencedColumnName = "act_id", foreignKey = @ForeignKey(name = "FK_act_parent_id"))
    @JsonIgnoreProperties("childes")
    private Activity parent;

    @JsonView(MyJsonView.ActivityList.class)
    @JsonProperty("menuEntries")
    @OneToMany(targetEntity = Activity.class, mappedBy = "parent", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnoreProperties("parent")
    private List<Activity> childes;

    @JsonView(MyJsonView.ActivityList.class)
    @Column(name = "act_name",nullable = false)
    private String name;

    @JsonView(MyJsonView.ActivityList.class)
    @JsonProperty("text")
    @Column(name = "act_title",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PANEL_MENU)
    private String title;

    @JsonView(MyJsonView.ActivityDetail.class)
    @Column(name = "act_icon",nullable = false)
    private String  icon  ;

    @JsonView(MyJsonView.ActivityDetail.class)
    @Column(name = "act_desc")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PANEL_MENU)
    private String description;

    @JsonView(MyJsonView.ActivityDetail.class)
    @Column(name = "act_url")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PANEL_MENU)
    private String url;

    @JsonView(MyJsonView.ActivityDetail.class)
    @JsonProperty("isModal")
    @Column(name = "act_modal",nullable = false)
    private Boolean modal;

    @JsonView(MyJsonView.ActivityDetail.class)
    @JsonProperty("isBlank")
    @Column(name = "act_blank",nullable = false)
    private Boolean blank;

    @JsonView(MyJsonView.ActivityDetail.class)
    @Column(name = "act_pass_parameter")
    private String passParameter;

    @JsonView(MyJsonView.ActivityDetail.class)
    @Column(name = "act_panel_menu",nullable = false)
    private Boolean panelMenu;

    @JsonView(MyJsonView.ActivityList.class)
    @Column(name = "act_panel_type")
    private Integer panelType;

    @JsonView(MyJsonView.ActivityList.class)
    @Column(name = "act_active",nullable = false)
    private Boolean active;

    @JsonView(MyJsonView.ActivityDetail.class)
    @Column(name = "act_order",nullable = false)
    private Integer order;

    @JsonView(MyJsonView.ActivityDetail.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("activities")
    @JoinTable(name = "sc_activity_permission",
            joinColumns = @JoinColumn(name = "acp_act_id",nullable = false, referencedColumnName = "act_id", foreignKey = @ForeignKey(name = "FK_acp_act_id")),
            inverseJoinColumns = @JoinColumn(name = "acp_prm_id",nullable = false,  referencedColumnName = "prm_id", foreignKey = @ForeignKey(name = "FK_acp_prm_id")))
    private List<Permission> permissions;

    @ManyToMany(mappedBy = "activities")
    @JsonIgnoreProperties("activities")
    private List<Role> roles = new ArrayList<>();

    @JsonProperty("isGroup")
    @JsonView(MyJsonView.PanelMenu.class)
    public Boolean isGroup(){
        if (this.childes!=null && this.childes.size()>0)
            return true;
        else
            return false;
    }

}
