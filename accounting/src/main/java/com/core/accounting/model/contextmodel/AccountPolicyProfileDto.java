package com.core.accounting.model.contextmodel;

import com.core.accounting.model.dbmodel.AccountPolicyProfileOperationType;
import com.core.accounting.model.dbmodel.AccountType;
import com.core.accounting.model.dbmodel.UserAccountPolicyProfile;
import com.core.accounting.model.view.AccountJsonView;
import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class AccountPolicyProfileDto extends BaseDto {

    private Long userId;
    @NotNull(message = "{common.title_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
    @NotNull(message = "{common.active_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Boolean active;
    @NotNull(message = "{common.accountPolicyProfile.accountTypeId_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Long accountTypeId;

    @Valid
    private List<AccountPolicyProfileOperationTypeDto> accountPolicyProfileOperationTypesDto ;

}
