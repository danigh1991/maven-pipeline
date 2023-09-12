package com.core.datamodel.model.wrapper;

import com.core.model.annotations.MultiLingual;
import com.core.model.enums.ERepository;
import lombok.Data;
import java.util.Locale;

@Data
public class DomainWrapper extends AbstractMultiLingualWrapper {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String domainUrl;
    private Long siteThemeId;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.SITE_THEME, fieldName = "name", targetIdFieldName = "siteThemeId")
    private String siteThemeName;
    private Long languageId;
    private String languageShortName;
    private Boolean languageLtrDirection;
    private Long countryId;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.COUNTRY, fieldName = "name", targetIdFieldName = "countryId")
    private String countryName;
    private Integer domainOrder;
    private transient String langRoute=null;
    private transient Locale locale=null;
    private transient String angularLangRoute=null;


    public DomainWrapper(Long id, String domainUrl, Long siteThemeId, String siteThemeName, Long languageId, String languageShortName, Boolean languageLtrDirection, Long countryId, String countryName, Integer domainOrder) {
        this.id = id;
        this.domainUrl = domainUrl;
        this.siteThemeId = siteThemeId;
        this.siteThemeName = siteThemeName;
        this.languageId = languageId;
        this.languageShortName = languageShortName;
        this.languageLtrDirection = languageLtrDirection;
        this.countryId=countryId;
        this.countryName=countryName;
        this.domainOrder = domainOrder;

        this.prepareMultiLingual();
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
            if(this.getLanguageShortName()!=null){
                String[] res=this.getLanguageShortName().split("_");
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


