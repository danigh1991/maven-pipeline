package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.view.AccountJsonView;
import com.core.common.util.Utils;
import com.core.datamodel.model.wrapper.AbstractMultiLingualWrapper;
import com.core.model.annotations.MultiLingual;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OperationTypeWrapper extends AbstractMultiLingualWrapper {

    @JsonView(AccountJsonView.OperationTypeList.class)
    private Long id;
    @JsonView(AccountJsonView.OperationTypeList.class)
    private String name;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.OPERATION_TYPE, targetIdFieldName = "id")
    private String description;
    @JsonView(AccountJsonView.OperationTypeList.class)
    private Boolean active;
    @JsonView(AccountJsonView.OperationTypeList.class)
    private Integer code;
    private char operationType;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double minAmount;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double maxAmount;
    //@JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer maxAmountDurationType;
    //@JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer maxAmountDuration;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer wageType;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double wageRate;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double wageAmount;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer sourceType; //1=wallet , 2=card
    private String defaultAmounts;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Double globalMaxDailyAmount;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Boolean starter;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Integer ord;
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private List<Double> defaultAmountList;


    public OperationTypeWrapper(Long id, String name, String description, Boolean active, Integer code, char operationType, Double minAmount, Double maxAmount, Integer maxAmountDurationType, Integer maxAmountDuration, Integer wageType, Double wageRate, Double wageAmount, Integer sourceType, String defaultAmounts,Double globalMaxDailyAmount,Boolean starter, Integer ord) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.code = code;
        this.operationType = operationType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.maxAmountDurationType = maxAmountDurationType;
        this.maxAmountDuration = maxAmountDuration;
        this.wageType = wageType;
        this.wageRate = wageRate;
        this.wageAmount = wageAmount;
        this.sourceType = sourceType;
        this.defaultAmounts = defaultAmounts;
        this.globalMaxDailyAmount = globalMaxDailyAmount;
        this.starter = starter;
        this.ord = ord;

        this.prepareMultiLingual();
    }
    @JsonView(AccountJsonView.OperationTypeList.class)
    public String getCaption(){
        return EOperation.valueOf(this.getId().intValue()).getCaption();
    }

    public List<Double> getDefaultAmountList() {
        if(defaultAmountList==null) {
            if (Utils.isStringSafeEmpty(this.defaultAmounts))
                defaultAmountList= new ArrayList<>();
            else
                defaultAmountList= Arrays.stream(defaultAmounts.split(",")).map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
        }
        return defaultAmountList;
    }
}
