package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class AccountDto extends BaseDto {

    @NotNullStr(message = "{common.account.name_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;

    @NotNull(message = "{common.accountType.id_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Long accountTypeId;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNullStr(message = "{common.account.color_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String color;
    private Long theme_id; //theme Id for color and background image , ...
    private Double capacity;
    private Long userId;
    private Long accountPolicyProfileId;

    private Long accountCategoryId;

    //todo implement for share Account
    //private List<CUserAccountPolicyProfile> cUserAccountPolicyProfile;
}
