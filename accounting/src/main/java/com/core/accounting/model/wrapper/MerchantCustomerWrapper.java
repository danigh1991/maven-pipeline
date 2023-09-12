package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MerchantCustomerWrapper implements Serializable {

    @JsonView(AccountJsonView.MerchantList.class)
    private Long merchantCustomerId;
    private Long merchantId;
    @JsonView(AccountJsonView.MerchantList.class)
    private Long merchantUserId;
    @JsonView(AccountJsonView.MerchantList.class)
    private Long customerUserId;
    private String customerUserName;
    private String firstName;
    private String lastName;
    private Long score;
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
    public String getFullName() {
        return Utils.getFullName(this.getFirstName(),this.getLastName());
    }

    @JsonView(AccountJsonView.MerchantList.class)
    public String getMaskCustomerUserName() {
        return BaseUtils.unreadableString(this.getCustomerUserName(),4,4,'*');
    }
}
