package com.core.common.externalapi;

import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Data
public class ExternalApiResponse<T> implements Serializable {
    ExternalApi externalApi;
    T responseEntity;
    Long externalApiCallId;
    ExternalApiCall externalApiCall;
    Boolean fromCache=false;
}
