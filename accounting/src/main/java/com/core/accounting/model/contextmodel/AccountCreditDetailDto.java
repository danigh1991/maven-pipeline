package com.core.accounting.model.contextmodel;

import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.InRangeDbl;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import javax.validation.constraints.NotNull;


@Data
public class AccountCreditDetailDto extends BaseDto {

    @NotNull(message = "{common.account.id_required}", groups ={CreateValidationGroup.class})
    private Long accountId;
    @NotNullStr(message = "{common.accountCreditDetail.title_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String title;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;
    @NotNull(message = "{common.creditType.id_required}", groups ={CreateValidationGroup.class})
    private Integer  creditTypeId;
    @NotNull(message = "{common.accountCreditDetail.viewType_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Integer  viewType;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String expireDate;
    @NotNull(message = "{common.accountPolicyProfile.id_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Long defaultAccountPolicyProfileId;
    private Integer settlementPeriod;
    private Integer settlementPeriodType;

    @NotNull(message = "{common.accountPolicyProfile.creditAmount_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Double creditAmount;
    @NotNull(message = "{common.accountPolicyProfile.creditAmountPerUser_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Double creditAmountPerUser;
    @NotNull(message = "{common.accountPolicyProfile.minCreditAmount_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Double minCreditAmount;
    @NotNull(message = "{common.accountCreditDetail.spendingRestrictions_required}", groups ={CreateValidationGroup.class,EditValidationGroup.class})
    private Boolean spendingRestrictions;
    @InRangeDbl(message  = "{common.accountCreditDetail.rateRestrictions_invalid}" , min = 0, max = 99)
    private Double rateRestrictions;
    private Double maxAmountRestrictions;

    @InRangeDbl(message  = "{common.accountCreditDetail.interestRate_invalid}" , min = 0, max = 99)
    private Double interestRate=0d;
    @InRangeDbl(message  = "{common.accountCreditDetail.forfeitRate_invalid}" , min = 0, max = 99)
    private Double forfeitRate=0d;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String issuer;
}
