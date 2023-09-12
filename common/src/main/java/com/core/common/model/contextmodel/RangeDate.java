package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;


public class RangeDate implements Serializable {
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String fromDate;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String toDate;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
