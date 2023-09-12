package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserGroupDto extends BaseDto {

    @NotNullStr(message = "{common.userGroupDto.name_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNull(message = "{common.userGroupDto.active_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Boolean active;

    private Long userId;
    //private Long storeBranchId;

}
