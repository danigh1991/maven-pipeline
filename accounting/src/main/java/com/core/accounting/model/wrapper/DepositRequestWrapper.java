package com.core.accounting.model.wrapper;

import com.core.accounting.model.contextmodel.FinalOperationRequestDto;
import com.core.accounting.model.enums.ETransactionSourceType;
import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.DepositRequestDetailRepository;
import com.core.accounting.repository.TransactionRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralKeyValue;
import com.core.datamodel.util.json.JsonDateSerializer;
import com.core.datamodel.util.json.JsonDateShortSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DepositRequestWrapper implements Serializable {


    @JsonView(AccountJsonView.DepositRequestWrapperShortList.class)
    private Long id;
    private Long userId;
    @JsonView(AccountJsonView.DepositRequestWrapperShortList.class)
    private String title;
    private Long  accountId;
    @JsonView(AccountJsonView.DepositRequestWrapperList.class)
    private String accountName;
    @JsonView(AccountJsonView.DepositRequestWrapperDetail.class)
    private String description;
    @JsonView(AccountJsonView.DepositRequestWrapperList.class)
    private Boolean active;
    @JsonView(AccountJsonView.DepositRequestWrapperList.class)
    private Date expireDate;
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;
    @JsonView(AccountJsonView.DepositRequestWrapperDetail.class)
    private List<DepositRequestDetailWrapper> depositRequestDetailWrappers;

    public DepositRequestWrapper(Long id, Long userId, String title, Long accountId, String accountName, String description, Boolean active, Date expireDate, Date createDate, Long createBy, Date modifyDate, Long modifyBy) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.accountId = accountId;
        this.accountName = accountName;
        this.description = description;
        this.active = active;
        this.expireDate = expireDate;
        this.createDate = createDate;
        this.createBy = createBy;
        this.modifyDate = modifyDate;
        this.modifyBy = modifyBy;
    }

    @JsonSerialize(using = JsonDateShortSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }



    @JsonView(AccountJsonView.DepositRequestWrapperList.class)
    public String getActiveDesc() {
        if (this.getActive())
            return BaseUtils.getMessageResource("global.status.active");
        else
            return BaseUtils.getMessageResource("global.status.inActive");
    }


    @JsonView(AccountJsonView.DepositRequestWrapperList.class)
    public Integer getStatus() {
        if (this.getExpireDate().getTime()<(new Date()).getTime())
            return 0;
        else
            return 1;
    }

    @JsonView(AccountJsonView.DepositRequestWrapperList.class)
    public String getStatusDesc() {
        if (this.getExpireDate().getTime()<(new Date()).getTime())
            return BaseUtils.getMessageResource("global.status.done");
        else
            return "";
    }



    public List<DepositRequestDetailWrapper> getDepositRequestDetailWrappers() {
        if (this.depositRequestDetailWrappers == null) {
            this.depositRequestDetailWrappers=((DepositRequestDetailRepository) AccountingRepositoryFactory.getRepository(ERepository.DEPOSIT_REQUEST_DETAIL)).findWrappersByDepositRequestId(this.getId());
        }
        return this.depositRequestDetailWrappers;
    }


}
