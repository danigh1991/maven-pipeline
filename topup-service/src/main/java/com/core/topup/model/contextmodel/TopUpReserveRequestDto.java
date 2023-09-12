package com.core.topup.model.contextmodel;

import com.core.accounting.model.contextmodel.OperationRequestDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUpReserveRequestDto extends OperationRequestDto {
    @NotNullStr(message = "{common.topUp.chargeType_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String chargeType;

    private Integer packageType;
    @NotNull(message = "{common.amount_required}", groups ={CreateValidationGroup.class})
    private Double  amount;
    @NotNullStr(message = "{common.topUp.phoneNumber_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String phoneNumber;
    @NotNull(message = "{common.topUp.phoneNumber_required}", groups ={CreateValidationGroup.class})
    private Integer operatorId;
    /*@JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String saleDescription;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String extSaleID;*/

/*    @NotNullStr(message = "{common.topUp.cardType_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String cardType;
    @NotNullStr(message = "{common.topUp.cardNo_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String cardNo;
    @NotNullStr(message = "{common.topUp.channelId_required}", groups ={CreateValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String channelId;*/


    private Integer productTypeId;
    private Integer productId;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String requesterNumber;
    private Integer sms;
    private Integer voice;
    private Integer gprs;

}
