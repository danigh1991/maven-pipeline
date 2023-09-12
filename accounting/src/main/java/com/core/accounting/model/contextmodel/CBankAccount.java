package com.core.accounting.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CBankAccount implements Serializable {


    //@NotNull(message = "{common.bank.id_required}")
    private Long bankId;

    private String title;

    //@NotNullStr(message = "{common.account.accNumber_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String accountNumber;

    //@NotNullStr(message = "{common.account.ibanNumber_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String ibanNumber;

    //@NotNullStr(message = "{common.account.cardNumber_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String cardNumber;


    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String swiftNumber;


    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String paypalAccount;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String password;


}
