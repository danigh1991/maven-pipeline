package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.CostShareRequestDetailRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.datamodel.util.json.JsonDateShortSerializer;
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
public class CostShareRequestWrapper implements Serializable {


    @JsonView(AccountJsonView.CostShareRequestShortList.class)
    private Long id;
    @JsonView(AccountJsonView.CostShareRequestList.class)
    private Long costShareTypeId;
    @JsonView(AccountJsonView.CostShareRequestList.class)
    private String costShareTypeTitle;
    private Long userId;
    @JsonView(AccountJsonView.CostShareRequestShortList.class)
    private String title;
    private Long  accountId;
    @JsonView(AccountJsonView.CostShareRequestList.class)
    private String accountName;
    @JsonView(AccountJsonView.CostShareRequestDetail.class)
    private String description;
    @JsonView(AccountJsonView.CostShareRequestList.class)
    private Boolean active;
    @JsonView(AccountJsonView.CostShareRequestList.class)
    private Date expireDate;
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;
    @JsonView(AccountJsonView.CostShareRequestDetail.class)
    private List<CostShareRequestDetailWrapper> costShareRequestDetailWrappers;

    public CostShareRequestWrapper(Long id,Long costShareTypeId, String costShareTypeTitle , Long userId, String title, Long accountId, String accountName, String description, Boolean active, Date expireDate, Date createDate, Long createBy, Date modifyDate, Long modifyBy) {
        this.id = id;
        this.costShareTypeId = costShareTypeId;
        this.costShareTypeTitle = costShareTypeTitle;
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



    @JsonView(AccountJsonView.CostShareRequestList.class)
    public String getActiveDesc() {
        if (this.getActive())
            return BaseUtils.getMessageResource("global.status.active");
        else
            return BaseUtils.getMessageResource("global.status.inActive");
    }


    @JsonView(AccountJsonView.CostShareRequestList.class)
    public Integer getStatus() {
        if (this.getExpireDate().getTime()<(new Date()).getTime())
            return 0;
        else
            return 1;
    }

    @JsonView(AccountJsonView.CostShareRequestList.class)
    public String getStatusDesc() {
        if (this.getExpireDate().getTime()<(new Date()).getTime())
            return BaseUtils.getMessageResource("global.status.done");
        else
            return "";
    }



    public List<CostShareRequestDetailWrapper> getCostShareRequestDetailWrappers() {
        if (this.costShareRequestDetailWrappers == null) {
            this.costShareRequestDetailWrappers=((CostShareRequestDetailRepository) AccountingRepositoryFactory.getRepository(ERepository.COST_SHARE_REQUEST_DETAIL)).findWrappersByCostShareRequestId(this.getId());
        }
        return this.costShareRequestDetailWrappers;
    }


}
