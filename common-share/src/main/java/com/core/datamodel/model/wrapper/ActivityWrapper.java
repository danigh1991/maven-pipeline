package com.core.datamodel.model.wrapper;


import com.core.datamodel.repository.factory.ShareRepositoryFactory;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.repository.ActivityRepository;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ActivityWrapper extends AbstractMultiLingualWrapper {
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonProperty("menuEntries")
    @JsonView(MyJsonView.PanelMenu.class)
    private List<ActivityWrapper> childes;

    @JsonProperty("text")
    @JsonView(MyJsonView.PanelMenu.class)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PANEL_MENU,targetIdFieldName = "id")
    private String title;

    @JsonView(MyJsonView.PanelMenu.class)
    private String  icon  ;

    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PANEL_MENU,targetIdFieldName = "id")
    private String description;

    @JsonView(MyJsonView.PanelMenu.class)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PANEL_MENU,targetIdFieldName = "id")
    private String url;

    @JsonProperty("isModal")
    @JsonView(MyJsonView.PanelMenu.class)
    private Boolean modal;

    @JsonProperty("isBlank")
    @JsonView(MyJsonView.PanelMenu.class)
    private Boolean blank;

    @JsonView(MyJsonView.PanelMenu.class)
    private String passParameter;

    private Integer panelType;

    private Integer actOrder;



    private List<Long> roleFilterIds=new ArrayList<>();



    public ActivityWrapper(Long id, String title, String icon, String description, String url, Boolean modal, Boolean blank, String passParameter, Integer panelType,Integer actOrder) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.description = description;
        this.url = url;
        this.modal = modal;
        this.blank = blank;
        this.passParameter = passParameter;
        this.panelType=panelType;
        this.actOrder=actOrder;

        this.prepareMultiLingual();
        
        roleFilterIds.add(0l);
    }


    public void setRoleFilterIds(List<Long> roleFilterIds) {
        this.roleFilterIds = roleFilterIds;
        this.getChildes();
    }

    public List<ActivityWrapper> getChildes() {
        if (childes==null ) {
            childes = ((ActivityRepository) ShareRepositoryFactory.getRepository(ERepository.PANEL_MENU)).findChildPanelMenuByRoleIds(this.getId(), this.getPanelType(), this.getRoleFilterIds());
            childes.forEach(pm -> { pm.setRoleFilterIds(this.getRoleFilterIds());});
        }
        return childes;
    }

    @JsonProperty("isGroup")
    @JsonView(MyJsonView.PanelMenu.class)
    public Boolean isGroup(){
        if (this.getChildes()!=null && this.getChildes().size()>0)
            return true;
        else
            return false;
    }


}
