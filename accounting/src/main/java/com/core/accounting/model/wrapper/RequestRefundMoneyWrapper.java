package com.core.accounting.model.wrapper;


import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.model.dbmodel.Bank;
import com.core.datamodel.model.enums.*;
import com.core.datamodel.model.staticstatus.StatusMaps;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.repository.BaseBankRepository;
import com.core.datamodel.repository.factory.ShareRepositoryFactory;
import com.core.datamodel.util.ShareUtils;
import com.core.datamodel.util.json.JsonDateShortSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestRefundMoneyWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private Long id;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private Long accountId;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String accountName;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private Integer status;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Long reqUserId;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String reqUserName;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String reqDesc;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private Double reqAmount;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private Date createDate;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Date modifyDate;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Long payUserId;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private String payUserName;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private Date payDate;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private String payBankRef;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private String payDesc;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String financeDestName;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String financeDestCaption;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String financeDestValue;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Integer refundTypeId;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Long refundForId;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Long payBankId;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String payBankName;
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    private Long toBankId;
    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    private String toBankName;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @JsonSerialize(using = JsonDateShortSerializer.class)
    public Date getPayDate() {
        return payDate;
    }


    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    public String getStatusDesc(){
        return ERefundStatus.captionOf(this.getStatus());
    }

    @JsonView({AccountJsonView.RequestRefundMoneyList.class})
    public String getRefundTypeDesc(){
        return ERefundType.captionOf(this.getRefundTypeId());
    }

    //@JsonView({AccountJsonView.RequestRefundMoneyDetailsForAdmin.class})
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    public Object[] getStatusTo(){
        Map<Integer, TypeWrapper> result = new HashMap<>(StatusMaps.REFUND_VALID_CHANGE_STATUS_MAP.get(this.getStatus()).entrySet().stream().collect(Collectors.toMap(
                                                                                entry -> entry.getKey(),
                                                                                entry -> ERefundStatus.asObjectWrapper(entry.getValue()))));
        if(ShareUtils.getCurrentUserId() !=  this.getReqUserId())
            result.remove(4);
        return result.values().toArray();
//        return AccountingServiceImpl.getStaticRequestRefundValidStatusesTo(this.getStatus(), this.getReqUserId());
    }

//    @JsonView({AccountJsonView.RequestRefundMoneyDetailsForAdmin.class})
    @JsonView({AccountJsonView.RequestRefundMoneyDetails.class})
    public List<Bank> getBankAllowAccountIntro(){
        return ((BaseBankRepository) ShareRepositoryFactory.getRepository(ERepository.BANK)).findAllAllowAccountIntro();
    }

}
