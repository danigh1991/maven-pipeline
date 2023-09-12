package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.TransactionRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.common.util.Utils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DepositRequestDetailWrapper implements Serializable {


    @JsonView(AccountJsonView.DepositRequestDetailWrapperList.class)
    private Long id;
    private Long depositRequestId;
    private Long userId;
    @JsonView(AccountJsonView.DepositRequestDetailWrapperList.class)
    private String targetUser;
    @JsonView(AccountJsonView.DepositRequestDetailWrapperList.class)
    private Double amount;
    @JsonView(AccountJsonView.DepositRequestDetailWrapperDetail.class)
    private Date doneDate;
    @JsonView(AccountJsonView.DepositRequestDetailWrapperDetail.class)
    private Integer notifyCount;
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;

    public DepositRequestDetailWrapper(Long id, Long depositRequestId, Long userId, String targetUser, Double amount, Date doneDate, Integer notifyCount, Date createDate, Long createBy, Date modifyDate, Long modifyBy) {
        this.id = id;
        this.depositRequestId = depositRequestId;
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

    @JsonView(AccountJsonView.DepositRequestDetailWrapperList.class)
    public String getHint() {
        return userId==null ? Utils.getMessageResourceByKeyArgs("common.targetUser_notRegisterHint"): "";
    }
}
