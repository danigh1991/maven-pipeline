package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.util.json.JsonDateSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CostDetailWrapper implements Serializable {


    @JsonView(AccountJsonView.CostDetailList.class)
    private Long id;
    @JsonView(AccountJsonView.CostDetailDetail.class)
    private Long costShareRequestId;
    @JsonView(AccountJsonView.CostDetailList.class)
    private String description;
    @JsonView(AccountJsonView.CostDetailList.class)
    private Double amount;
    @JsonView(AccountJsonView.CostDetailList.class)
    private Date effectiveDate;
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;


    public CostDetailWrapper(Long id, Long costShareRequestId, String description, Double amount, Date effectiveDate, Date createDate, Long createBy, Date modifyDate, Long modifyBy) {
        this.id = id;
        this.costShareRequestId = costShareRequestId;
        this.description = description;
        this.amount = amount;
        this.effectiveDate = effectiveDate;
        this.createDate = createDate;
        this.createBy = createBy;
        this.modifyDate = modifyDate;
        this.modifyBy = modifyBy;
    }


    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }






}
