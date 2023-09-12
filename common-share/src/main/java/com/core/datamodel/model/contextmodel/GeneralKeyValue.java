package com.core.datamodel.model.contextmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;

import java.io.Serializable;

@Builder
public class GeneralKeyValue implements Serializable{
    @JsonView(MyJsonView.GeneralKeyValue.class)
    private String key;
    @JsonView(MyJsonView.GeneralKeyValue.class)
    private String value;

    public GeneralKeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
