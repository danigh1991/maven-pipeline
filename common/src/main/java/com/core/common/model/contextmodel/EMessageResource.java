package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.json.JsonCleanLinkAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class EMessageResource implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNullStr(message = "{global.cache.key_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String key;

    @NotNullStr(message = "{global.cache.val_required}")
    @JsonDeserialize(using = JsonCleanLinkAndXssDeserializer.class)
    private String content;

}
