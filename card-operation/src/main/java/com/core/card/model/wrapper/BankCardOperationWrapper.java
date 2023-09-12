package com.core.card.model.wrapper;

import com.core.accounting.model.enums.EOperation;
import com.core.card.model.dbmodel.BankCard;
import com.core.card.model.enums.ECardOperationStatus;
import com.core.card.model.view.CardJsonView;
import com.core.common.util.Utils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.util.BaseUtils;
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
public class BankCardOperationWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    private Long id;
    private Integer operationTypeId;
    private Long bankCardId;
    @JsonView({CardJsonView.BankCardOperationWrapperDetail.class, CardJsonView.BankCardOperationWrapperListAdmin.class})
    private String  sourceCard;
    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    private String  sourceCardName;
    @JsonView({CardJsonView.BankCardOperationWrapperDetail.class, CardJsonView.BankCardOperationWrapperListAdmin.class})
    private String targetCard;
    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    private String targetCardOwner;
    private Integer status;
    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    private Date lastStatusDate;
    private String billId;
    private String billPay;
    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    private Double amount;
    @JsonView(CardJsonView.BankCardOperationWrapperDetail.class)
    private String description;
    @JsonView(CardJsonView.BankCardOperationWrapperDetailAdmin.class)
    private String trackingId;
    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    private Date shpRegistrationDate;
    @JsonView(CardJsonView.BankCardOperationWrapperDetailAdmin.class)
    private String shpTransactionId;
    private Date shpTransactionDate;
    @JsonView(CardJsonView.BankCardOperationWrapperDetailAdmin.class)
    private Integer shpStan;
    @JsonView(CardJsonView.BankCardOperationWrapperDetailAdmin.class)
    private String shpRrn;
    @JsonView(CardJsonView.BankCardOperationWrapperDetailAdmin.class)
    private String shpApprovalCode;
    @JsonView(CardJsonView.BankCardOperationWrapperDetail.class)
    private String errorDescription;
    private Long userId;

    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    public String getOperationTypeCaption() {
        return EOperation.valueOf(operationTypeId).getCaption();
    }

    public String getFormatAmount(){
        return BaseUtils.formatMoney(this.getAmount());
    }

    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    public String getStatusDesc() {
        if (this.status==0)
            return ECardOperationStatus.CARD_HOLDER_INQUIRY.getCaption();
        else if (this.status==1)
             return ECardOperationStatus.OTP_REQUEST.getCaption();
        else if (this.status==2)
             return ECardOperationStatus.SUCCESS.getCaption();
        else
            return ECardOperationStatus.UN_SUCCESS.getCaption();
    }


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getLastStatusDate() {
        return lastStatusDate;
    }

    @JsonView({CardJsonView.BankCardOperationWrapperDetail.class, CardJsonView.BankCardOperationWrapperListAdmin.class})
    public String  getTrackingCode() {
        return Utils.getTrackingCode(EOperation.valueOf(this.operationTypeId).getOperationCode().toString(),this.getId());
    }


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getShpRegistrationDate() {
        return shpRegistrationDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getShpTransactionDate() {
        return shpTransactionDate;
    }

    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    public Boolean getResultStatus() {
        return this.status==2 ? true : false;
    }
}
