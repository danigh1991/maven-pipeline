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
public class CostShareRequestDetailWrapper implements Serializable {


    @JsonView(AccountJsonView.CostShareRequestDetailList.class)
    private Long id;
    private Long costShareRequestId;
    private Long userId;
    @JsonView(AccountJsonView.CostShareRequestDetailList.class)
    private String targetUser;
    @JsonView(AccountJsonView.CostShareRequestDetailList.class)
    private Double amount;
    @JsonView(AccountJsonView.CostShareRequestDetailDetail.class)
    private Date doneDate;
    @JsonView(AccountJsonView.CostShareRequestDetailDetail.class)
    private Integer notifyCount;
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;

    public CostShareRequestDetailWrapper(Long id, Long costShareRequestId, Long userId, String targetUser, Double amount, Date doneDate, Integer notifyCount, Date createDate, Long createBy, Date modifyDate, Long modifyBy) {
        this.id = id;
        this.costShareRequestId = costShareRequestId;
        this.userId = userId;
        this.targetUser = targetUser;
        this.amount = amount;
        this.doneDate = doneDate;
        this.notifyCount = notifyCount;
        this.createDate = createDate;
        this.createBy = createBy;
        this.modifyDate = modifyDate;
        this.modifyBy = modifyBy;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getDoneDate() {
        return doneDate;
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
