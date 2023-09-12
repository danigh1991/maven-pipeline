package com.core.common.model.metadata;

import com.core.common.util.Utils;
import com.core.datamodel.model.wrapper.AddressWrapper;
import com.core.util.BaseUtils;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TrackingData {
    private String trackingCode=null;

    public TrackingData(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public Boolean hasTrackingCode(){
        return !BaseUtils.isStringSafeEmpty(this.getTrackingCode());
    }

}
