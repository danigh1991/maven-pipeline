package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class TokenDto {
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String data;
}
