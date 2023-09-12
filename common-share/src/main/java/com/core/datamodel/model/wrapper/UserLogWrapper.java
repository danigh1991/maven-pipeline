package com.core.datamodel.model.wrapper;




import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class UserLogWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date loginDate;
    private String ip;
    private String agent;


    public UserLogWrapper(Long id, Long userId, Date loginDate, String ip, String agent) {
        this.id = id;
        this.userId = userId;
        this.loginDate = loginDate;
        this.ip = ip;
        this.agent = agent;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getLoginDate() {
        return loginDate;
    }
}
