package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CConfiguration implements Serializable {

    @NotNullStr(message = "{common.configuration.name_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;

    /*@JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)*/
    private String chrVal;

    private Long numVal;

    private Boolean active;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String desc;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChrVal() {
        return chrVal;
    }

    public void setChrVal(String chrVal) {
        this.chrVal = chrVal;
    }

    public Long getNumVal() {
        return numVal;
    }

    public void setNumVal(Long numVal) {
        this.numVal = numVal;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
