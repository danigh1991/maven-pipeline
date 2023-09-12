package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class CActivity implements Serializable {
    @NotNullStr(message = "{common.activity.title_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String title;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String icon;
    @NotNull(message = "{common.activity.modal_required}")
    private Boolean modal;
    @NotNull(message = "{common.activity.blank_required}")
    private Boolean blank;
    @NotNull(message = "{common.activity.active_required}")
    private Boolean active;
    private List<Long> permissions;
}
