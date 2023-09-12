package com.core.common.util.json;


import com.core.util.BaseUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("jsonCleanHtmlAndXssDeserializer")
public class JsonCleanHtmlAndXssDeserializer extends JsonDeserializer<String>  {


    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();
        if (BaseUtils.isStringSafeEmpty(value) )
            return value;
        else                             {
            return  BaseUtils.cleanArabic(BaseUtils.cleanHTML(BaseUtils.cleanXSS(value)));
        }
    }


}
