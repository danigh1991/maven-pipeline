package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MerchantLimitWrapper implements Serializable {

    @JsonView(AccountJsonView.MerchantLimitView.class)
    private Long id;
    @JsonView(AccountJsonView.MerchantLimitView.class)
    private Integer type;
    @JsonView(AccountJsonView.MerchantLimitView.class)
    private String userName;
    @JsonView(AccountJsonView.MerchantLimitView.class)
    private String name;
    @JsonView(AccountJsonView.MerchantLimitView.class)
    private String family;
    private Long createBy;
    private Date createDate;
    private Long modifyBy;
    private Date modifyDate;

    public MerchantLimitWrapper(Long id, Integer type, String userName, String name, String family, Long createBy, Date createDate, Long modifyBy, Date modifyDate) {
        this.id = id;
        this.type = type;
        this.userName = userName;
        this.name = name;
        this.family = family;
        this.createBy = createBy;
        this.createDate = createDate;
        this.modifyBy = modifyBy;
        this.modifyDate = modifyDate;
    }

    @JsonView(AccountJsonView.MerchantLimitView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.MerchantLimitView.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
