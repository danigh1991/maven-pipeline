package com.core.common.model.contextmodel;



import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.Length;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class EUser implements Serializable {
    @NotNull(message = "{common.id_required}")
    private Long id;
    @NotNullStr(message = "{common.user.firstName_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String firstName;
    @NotNullStr(message = "{common.user.lastName_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String lastName;
    @NotNullStr(message = "{common.user.mobileNumber_required}")
    /*@Length(message = "{common.user.mobileNumber_len}", min = 10, max = 11)*///Should Validate From Mobile Pattern Setting And Fill By UserName Setting Field Or User Send Value
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String mobileNumber;
    @Email(message = "{common.user.email_check}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String email;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String address;
    private Long cityId;

    @NotNull(message = "{common.user.gender_required}")
    private Integer gender;

    //@NotNull(message = "{common.user.sendNotify_required}")
    private Boolean sendNotify;

    //@NotNull(message = "{common.user.sendSms_required}")
    private Boolean sendSms;

    //@NotNull(message = "{common.user.sendEmail_required}")
    private Boolean sendEmail;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String aliasName;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String affiliateReagent;


}
