package com.core.common.model.contextmodel;


import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.core.common.util.validator.annotation.Password;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class C2faRequest {

    @NotNullStr(message = "common.user.username_required")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String userName;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String password;

    //@NotNullStr(message = "common.user.contactWay_required")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String uniqueCode;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String affiliateReagent;


}
