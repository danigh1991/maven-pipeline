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
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = SiteTheme.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = SiteTheme.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "thm_id")),
        @AttributeOverride(name = "version", column = @Column(name = "thm_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "thm_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "thm_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "thm_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "thm_modify_by"))
})
public class SiteTheme extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_site_theme";


    @Column(name = "thm_name",nullable = false)
    @JsonView(MyJsonView.SiteThemeList.class)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.SITE_THEME)
    private String name;

    @Column(name = "thm_desc")
    @JsonView(MyJsonView.SiteThemeList.class)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.SITE_THEME)
    private String description;

    @Column(name = "thm_css")
    @JsonView(MyJsonView.SiteThemeDetails.class)
    private String css;

    @Column(name = "thm_js")
    @JsonView(MyJsonView.SiteThemeDetails.class)
    private String js;

    @Column(name = "thm_active",nullable = false)
    @JsonView(MyJsonView.SiteThemeList.class)
    private Boolean active=false;

    @Column(name = "thm_default_page_id",nullable = false)
    @JsonView(MyJsonView.SiteThemeList.class)
    private Long defaultPageId;

    @OneToMany(targetEntity=Language.class,mappedBy = "siteTheme",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("siteTheme")
    private List<Language> languages = new ArrayList<>();



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
