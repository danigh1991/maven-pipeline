package com.core.accounting.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;

@Data
public class EBankAccount extends CBankAccount {

    //todo uncomment after list implementation
    //@NotNull(message = "{common.id_required}")
    private Long id;


}
