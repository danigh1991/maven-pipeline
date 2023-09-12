package com.core.common.util.json;


import com.core.util.BaseUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component("jsonListCleanHtmlAndXssDeserializer")
public class JsonListCleanHtmlAndXssDeserializer extends JsonDeserializer<List<String>>  {


    @Override
    public List<String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        List<String> values = Collections.singletonList(jsonParser.getValueAsString());
        values.forEach(v-> {
            if (BaseUtils.isStringSafeEmpty(v))
                v = v;
            else
                v = BaseUtils.cleanArabic(BaseUtils.cleanHTML(BaseUtils.cleanXSS(v)));
        });
        return values;
    }


}
