package com.core.accounting.model.contextmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.common.model.contextmodel.BaseDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class MerchantDto extends BaseDto {


    @NotNull(message = "{common.merchantCategory.id_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Long merchantCategoryId;

    @NotNullStr(message = "{common.merchant.name_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String name;

    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String description;

    @NotNullStr(message = "{common.merchant.address_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String address;

    @NotNull(message = "{common.merchant.lat_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Double lat;

    @NotNull(message = "{common.merchant.lan_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Double lan;

    @NotNullStr(message = "{common.merchant.postalCode_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String postalCode;

    @NotNullStr(message = "{common.merchant.email_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @Email(message = "{common.contactUs.email_inValid}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String email;

    @NotNullStr(message = "{common.merchant.mobileNumber_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String mobileNumber;

    @NotNullStr(message = "{common.merchant.phoneNumber_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String phoneNumber;

    @NotNull(message = "{common.merchant.cityId_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Long cityId;

    @NotNull(message = "{common.merchant.regionId_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Long regionId;

    private Long userId;

    @NotNull(message = "{common.merchant.wallet_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Boolean wallet;
    @NotNull(message = "{common.merchant.card_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Boolean card;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String cardNumber;

    @NotNull(message = "{common.merchant.otherMerchantViewPolicy_required}", groups ={CreateValidationGroup.class, EditValidationGroup.class})
    private Integer otherMerchantViewPolicy;

}
