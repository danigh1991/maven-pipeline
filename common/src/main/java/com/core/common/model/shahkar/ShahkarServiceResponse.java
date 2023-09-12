package com.core.common.model.shahkar;

import com.core.common.model.sms.MessageInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShahkarServiceResponse implements Serializable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("response")
    private Integer response;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("result")
    private String result;

}
