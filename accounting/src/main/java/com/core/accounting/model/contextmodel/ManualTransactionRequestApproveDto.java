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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ManualTransactionRequestApproveDto extends BaseDto {

    @NotNull(message = "{common.manualTransactionRequest.status_required}", groups ={EditValidationGroup.class})
    private Integer status;

    //@NotNullStr(message = "{common.manualTransactionRequest.approvedDescription_required}", groups ={EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String approvedDescription;




}
