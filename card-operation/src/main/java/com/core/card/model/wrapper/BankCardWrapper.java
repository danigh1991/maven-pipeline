package com.core.card.model.wrapper;

import com.core.accounting.model.enums.ETransactionSourceType;
import com.core.accounting.model.wrapper.OperationTypeWrapper;
import com.core.accounting.repository.OperationTypeRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.card.model.view.CardJsonView;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class BankCardWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(CardJsonView.BankCardList.class)
    private Long id;
    @JsonView(CardJsonView.BankCardList.class)
    private String name;
    @JsonView(CardJsonView.BankCardList.class)
    private String cardNumber;
    private Long userId;
    @JsonView(CardJsonView.BankCardList.class)
    private Long accountTypeId=0l;
    @JsonView(CardJsonView.BankCardList.class)
    private String accountTypeName="card";
    @JsonView(CardJsonView.BankCardList.class)
    private String accountTypeCaption="کارت بانکی";
    @JsonProperty("status")
    @JsonView(CardJsonView.BankCardList.class)
    private Boolean active;
    @JsonView(CardJsonView.BankCardList.class)
    private String cardExpireDate;
    @JsonView(CardJsonView.BankCardDetails.class)
    private String description;
    @JsonView(CardJsonView.BankCardDetails.class)
    private Long createBy;
    @JsonView(CardJsonView.BankCardDetails.class)
    private Date createDate;
    @JsonView(CardJsonView.BankCardDetails.class)
    private Long modifyBy;
    @JsonView(CardJsonView.BankCardDetails.class)
    private Date modifyDate;
    //@JsonView(CardJsonView.BankCardList.class)
    private List<OperationTypeWrapper> operations=null;


    public BankCardWrapper(Long id, String name, String cardNumber, Long userId, Boolean active, String cardExpireDate, String description, Long createBy, Date createDate, Long modifyBy, Date modifyDate) {
        this.id = id;
        this.name = name;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.active = active;
        this.cardExpireDate = cardExpireDate;
        this.description = description;
        this.createBy = createBy;
        this.createDate = createDate;
        this.modifyBy = modifyBy;
        this.modifyDate = modifyDate;
    }

    public List<OperationTypeWrapper> getOperations() {
        if (operations == null) {
            operations=((OperationTypeRepository) AccountingRepositoryFactory.getRepository(ERepository.OPERATION_TYPE)).findOperationTypeWrappersBySourceTypeId(ETransactionSourceType.CARD.getId());
        }
        return operations;
    }

}
