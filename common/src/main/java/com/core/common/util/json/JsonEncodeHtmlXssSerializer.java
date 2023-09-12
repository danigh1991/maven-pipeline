package com.core.common.util.json;

import com.core.util.BaseUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JsonEncodeHtmlXssSerializer {

    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        if (value != null) {
            String encodedValue = BaseUtils.encodeHtml(value);
            jsonGenerator.writeString(encodedValue);
        }
    }

}
