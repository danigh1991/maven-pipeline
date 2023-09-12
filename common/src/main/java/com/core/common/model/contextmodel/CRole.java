package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.core.datamodel.model.dbmodel.Activity;
import com.core.datamodel.model.dbmodel.FileManagerAction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class CRole implements Serializable {
    @NotNullStr(message = "{common.role.name_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
    @NotNull(message = "{common.role.defaultRole_required}")
    private Boolean defaultRole;
    @NotNull(message = "{common.role.active_required}")
    private Boolean active;
    @NotNull(message = "{common.role.roleType_required}")
    private Integer roleType;
    private List<Long> activities;
    private List<Long> fileManagerActions;
}
