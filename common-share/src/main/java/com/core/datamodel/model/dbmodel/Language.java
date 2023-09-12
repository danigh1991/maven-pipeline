package com.core.datamodel.model.dbmodel;

import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@Entity
@Table(name = Language.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Language.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "lng_id")),
        @AttributeOverride(name = "version", column = @Column(name = "lng_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "lng_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "lng_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "lng_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "lng_modify_by"))
})
public class Language extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_language";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lng_thm_id",nullable = false,referencedColumnName = "thm_id", foreignKey = @ForeignKey(name = "FK_lng_thm_id"))
    @JsonIgnoreProperties("languages")
    private SiteTheme siteTheme;

    //@MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.LANGUAGE)
    @Column(name = "lng_name",nullable = false)
    private String name;

    @Column(name = "lng_short_name",nullable = false)
    private String shortName;

    @Column(name = "lng_domain_url")
    private String domainUrl;

    @Column(name = "lng_active",nullable = false)
    private Boolean active;

    @Column(name = "lng_left_to_right",nullable = false)
    private Boolean leftToRight;

    @Transient
    private transient String langRoute=null;

    @Transient
    private transient Locale locale=null;

    @Transient
    private transient String angularLangRoute=null;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
   public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    public String getLangRoute() {
        if(this.langRoute==null)
            setLocale();
        return langRoute;
    }

    public String getLangInnerRoute() {
       if(this.langRoute==null)
          setLocale();
       return langRoute+"/";
    }

    public String getAngularLangRoute() {
        if(this.angularLangRoute==null)
            setLocale();
        return angularLangRoute+"/";
    }



    public Locale getLocale() {
       if(this.locale==null)
          setLocale();
       return locale;
    }

    private void setLocale(){
        try{
            if(this.getShortName()!=null){
                String[] res=this.getShortName().split("_");
                this.langRoute="/"+res[0].toLowerCase();
                this.locale=new Locale(res[0],res[1]);
                this.angularLangRoute="/"+res[0].trim()+"-"+res[1].trim();
            }
        }catch (Exception e){
            this.langRoute="";
            this.locale=new Locale("en","US");
        }
    }


}
