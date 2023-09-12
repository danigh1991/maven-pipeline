package com.core.datamodel.model.wrapper;


import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVisitWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String ip;
    private Long trtId;
    private String trtTitle;
    private Long targetId;
    private String targetTitle;
    private String referer;
    private String deviceType;
    private String osName;
    private String browserType;
    private Date visitDate;


    public UserVisitWrapper(Long id, Long userId, String ip, Long trtId, String trtTitle, Long targetId, String targetTitle, String referer, String deviceType, String osName, String browserType,Date visitDate) {
        this.id = id;
        this.userId = userId;
        this.ip = ip;
        this.trtId = trtId;
        this.trtTitle = trtTitle;
        this.targetId = targetId;
        this.targetTitle = targetTitle;
        this.referer = referer;
        this.deviceType = deviceType;
        this.osName = osName;
        this.browserType = browserType;
        this.visitDate = visitDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getVisitDate() {
        return visitDate;
    }
}
