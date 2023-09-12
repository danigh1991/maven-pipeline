package com.core.common.model.contextmodel;


import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.core.common.util.validator.annotation.Password;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CUser implements Serializable {
    //@Length(message = "{common.user.username_len}", min = 10, max = 11)//Should Check From User name Type : Email Or mobile
    @NotNullStr(message = "{common.user.username_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String username;

    @NotNullStr(message = "{common.user.password_required}")
    @Password(message = "{common.user.password_check}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String password;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String uniqueCode;

    private Long cityId;

    @NotNull(message = "{common.user.gender_required}")
    private Integer gender;

    @Email(message = "{common.user.email_check}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String email;

    //@Length(message = "{common.user.username_len}", min = 10, max = 11)//Should Validate From Mobile Pattern Setting And Fill By UserName Setting Field Or User Send Value
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String mobile;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String affiliateReagent;

    private Boolean siteRoleConfirm = false;


}

