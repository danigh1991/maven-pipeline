package com.core.common.util.json;


import com.core.common.util.Utils;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("jsonCleanLinkAndXssDeserializer")
public class JsonCleanLinkAndXssDeserializer extends JsonDeserializer<String>  {


    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();
        if (Utils.isStringSafeEmpty(value) ) return value;
        else{
            String ret = BaseUtils.cleanArabic(BaseUtils.cleanXSS(value));
            if(!Utils.hasRole("supporter") && !Utils.hasRole("chiefEditor")) {//چون ممکن است ادمین بخواهد روی توضیحات لینک سئو ایجاد کند
                ret = BaseUtils.cleanLinks(ret);
                ret = BaseUtils.cleanAreas(ret);
            }

            return ret;
        }
    }


}
