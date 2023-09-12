package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.util.json.JsonDateSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.ColumnResult;
import java.io.Serializable;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MerchantWrapper implements Serializable {

    @JsonView(AccountJsonView.MerchantShortList.class)
    private Long id;
    private Long merchantCategoryId;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String merchantCategoryName;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private Integer merchantCategoryCode;
    @JsonView(AccountJsonView.MerchantList.class)
    private Long userId;
    @JsonView(AccountJsonView.MerchantList.class)
    private String userName;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String name;
    @JsonView(AccountJsonView.MerchantDetail.class)
    private String description;
    @JsonView(AccountJsonView.MerchantList.class)
    private Boolean active;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String address;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private Double lat;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private Double lan;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String postalCode;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String email;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String mobileNumber;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String phoneNumber;
    @JsonView(AccountJsonView.MerchantDetail.class)
    private Long cityId;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String cityName;
    @JsonView(AccountJsonView.MerchantDetail.class)
    private Long regionId;
    @JsonView(AccountJsonView.MerchantShortList.class)
    private String regionName;
    @JsonView(AccountJsonView.MerchantList.class)
    private Boolean wallet;
    @JsonView(AccountJsonView.MerchantList.class)
    private Boolean card;
    @JsonView(AccountJsonView.MerchantList.class)
    private String cardNumber;
    @JsonView(AccountJsonView.MerchantDetail.class)
    private Integer otherMerchantViewPolicy;

    private Long createBy;
    private Date createDate;
    private Long modifyBy;
    private Date modifyDate;

    @JsonView(AccountJsonView.MerchantList.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @JsonView(AccountJsonView.MerchantList.class)
    public String getStatusDesc() {
        if (this.active)
            return BaseUtils.getMessageResource("global.status.enable");
        else
            return BaseUtils.getMessageResource("global.status.disable");
    }
}
