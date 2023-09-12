package com.core.topup.model.wrapper;

import com.core.accounting.model.enums.EOperation;
import com.core.common.util.Utils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.topup.model.enums.ETopUpRequestStatus;
import com.core.topup.model.view.TopUpJsonView;
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
public class TopUpRequestWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private Long id;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private  Integer typeId;
    private Long userId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private String userName;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private Long operationRequestId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private String chargeType;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private Integer packageType;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private Integer productTypeId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private Integer productId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private String description;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private Double amount;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private String phoneNumber;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private String operator;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private String saleDescription;
    private String cardType;
    private String cardNo;
    private String channelId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private String requestIp;
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private Integer status;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private Long reserveId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private Long  saleId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private String operatorTransactionId;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private String  response;
    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    private String  trackingId;
    private Long createBy;

    @JsonSerialize(using= JsonDateTimeSerializer.class)
    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private Date createDate;
    private Long modifyBy;
    private Date modifyDate;


    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    public String getStatusDesc() {
        return ETopUpRequestStatus.valueOf(status).getCaption();
    }

    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    public String  getTrackingCode() {
        return Utils.getTrackingCode((this.getTypeId()==1? EOperation.MOBILE_CHARGE.getOperationCode().toString() : EOperation.INTERNET_CHARGE.getOperationCode().toString()),this.getId());
    }

    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    private String getOperatorName(){
        return this.getOperator().equals("MCI") ? "همراه اول" : this.getOperator().equals("MTN") ? "ایرانسل" : this.getOperator().equals("RAY") ? "رایتل" : "نامعلوم";
    };


}
