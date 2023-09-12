package com.core.accounting.model.contextmodel;

import com.core.accounting.model.dbmodel.Account;
import com.core.accounting.model.dbmodel.Transaction;
import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ManualTransactionRequestDto extends BaseDto {

    @NotNull(message = "{common.manualTransactionRequest.accountId_required}", groups ={CreateValidationGroup.class})
    private Long accountId;

    @NotNullStr(message = "{common.manualTransactionRequest.reference_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String reference;

    @NotNullStr(message = "{common.manualTransactionRequest.referenceDate_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String referenceDate;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNull(message = "{common.manualTransactionRequest.amount_required}")
    private Double amount;






}
