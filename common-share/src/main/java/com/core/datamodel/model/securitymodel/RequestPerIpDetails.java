package com.core.datamodel.model.securitymodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestPerIpDetails implements Serializable {
    List<Long> requestsPerIp=new ArrayList<>();
    private long lastEpochMinute=0;

    public RequestPerIpDetails() {

    }

    public List<Long> getRequestsPerIp() {
        return requestsPerIp;
    }

    public void setRequestsPerIp(List<Long> requestsPerIp) {
        this.requestsPerIp = requestsPerIp;
    }

    public long getLastEpochMinute() {
        return lastEpochMinute;
    }

    public void setLastEpochMinute(long lastEpochMinute) {
        this.lastEpochMinute = lastEpochMinute;
    }
}
