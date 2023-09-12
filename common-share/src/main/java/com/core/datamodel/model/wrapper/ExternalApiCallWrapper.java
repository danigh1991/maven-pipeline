package com.core.datamodel.model.wrapper;

import com.core.datamodel.model.enums.EExternalApiCallStatus;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApiCallWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private Long id;

    private Long apiId;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private String apiName;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private String apiDescription;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private Integer status;

    @JsonView(MyJsonView.ExternalApiCallDetails.class)
    private String address;

    @JsonView(MyJsonView.ExternalApiCallDetails.class)
    private String httpMethod;

    @JsonView(MyJsonView.ExternalApiCallDetails.class)
    private String requestBody ;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private String requestKey;

    @JsonView(MyJsonView.ExternalApiCallDetails.class)
    private String responseBody;

    @JsonSerialize(using= JsonDateTimeSerializer.class)
    @JsonView(MyJsonView.ExternalApiCallList.class)
    private Date responseDate;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private String referenceId;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private String  trackingId;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    private String processId;

    @JsonView(MyJsonView.ExternalApiCallDetails.class)
    private String  transactionId;

    @JsonSerialize(using= JsonDateTimeSerializer.class)
    @JsonView(MyJsonView.ExternalApiCallList.class)
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;

    @JsonView(MyJsonView.ExternalApiCallList.class)
    public String getStatusDesc() {
        return EExternalApiCallStatus.valueOf(status).getCaption();
    }



}
