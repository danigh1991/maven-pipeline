package com.core.datamodel.util.json;

import com.core.services.CalendarService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component("jsonDateTimeSerializer")
public class JsonDateTimeSerializer extends JsonSerializer<Date> {
    @Autowired
    private CalendarService calendarService;

    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        String formattedDate ="Null";
        if (date!=null)
            formattedDate =calendarService.getNormalDateTimeFormat(date);
        gen.writeString(formattedDate.trim());

    }
}
