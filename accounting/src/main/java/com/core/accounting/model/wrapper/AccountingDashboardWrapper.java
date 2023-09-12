package com.core.accounting.model.wrapper;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.repository.AccountRepository;
import com.core.accounting.repository.OperationTypeRepository;
import com.core.accounting.repository.factory.AccountingRepositoryFactory;
import com.core.common.util.Utils;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.enums.ERepository;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class AccountingDashboardWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    private Integer registerUserCount;
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    private Integer activeMerchantCount;
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    private Double sumAllBalance;
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    private Double sumCreditBalance;
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    private Double sumAllBlock;

    private List<AccountingDashboardDetailWrapper> dashboardDetailWrappers;
    private List<OperationTypeSummeryWrapper> operationTypeSummeryWrappers;

    public AccountingDashboardWrapper(Integer registerUserCount, Integer activeMerchantCount, Double sumAllBalance, Double sumCreditBalance, Double sumAllBlock) {
        this.registerUserCount = registerUserCount;
        this.activeMerchantCount = activeMerchantCount;
        this.sumAllBalance = sumAllBalance;
        this.sumCreditBalance = sumCreditBalance;
        this.sumAllBlock = sumAllBlock;
    }

    public List<AccountingDashboardDetailWrapper> getDashboardDetailWrappers() {
        if (dashboardDetailWrappers==null)
            dashboardDetailWrappers=((AccountRepository) AccountingRepositoryFactory.getRepository(ERepository.ACCOUNT)).findDashboardDetailWrapperSummery();
        return dashboardDetailWrappers;
    }

    @JsonView({AccountJsonView.AccountingDashboardView.class})
    public List<String> getAccountDashboardLabels(){
        return this.getDashboardDetailWrappers().stream().map(r -> Utils.getShamsiDate(r.getSummeryDate())).collect(Collectors.toList());
    }

    @JsonView({AccountJsonView.AccountingDashboardView.class})
    public List<List> getRegisterUserCount(){
        List<List> array = new ArrayList<>();
        array.add(this.getDashboardDetailWrappers().stream().map(r -> r.getRegisterUserCount()).collect(Collectors.toList()));
        return array;

    }
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    public List<List> getRegisterMerchantCount(){
        List<List> array = new ArrayList<>();
        array.add(this.getDashboardDetailWrappers().stream().map(r -> r.getRegisterMerchantCount()).collect(Collectors.toList()));
        return array;

    }
    @JsonView({AccountJsonView.AccountingDashboardView.class})
    public List<List> getMoneyInOut(){
        List<List> array = new ArrayList<>();
        array.add(this.getDashboardDetailWrappers().stream().map(r -> r.getSumChargeAmount()).collect(Collectors.toList()));
        array.add(this.getDashboardDetailWrappers().stream().map(r -> r.getSumGetMoneyAmount()).collect(Collectors.toList()));
        return array;
    }

    public List<OperationTypeSummeryWrapper> getOperationTypeSummeryWrappers() {
        if (operationTypeSummeryWrappers==null)
            operationTypeSummeryWrappers=((OperationTypeRepository) AccountingRepositoryFactory.getRepository(ERepository.OPERATION_TYPE)).findOperationTypeSummery();
        return operationTypeSummeryWrappers;
    }

    @JsonView({AccountJsonView.AccountingDashboardView.class})
    public List<String> getOperationTypeSummeryLabels(){
        return this.getOperationTypeSummeryWrappers().stream().map(r -> r.getDescription()).collect(Collectors.toList());
    }

    @JsonView({AccountJsonView.AccountingDashboardView.class})
    public List<List> getOperationTypeSummery(){
        List<List> array = new ArrayList<>();
        array.add(this.getOperationTypeSummeryWrappers().stream().map(r -> r.getSumAmount()).collect(Collectors.toList()));
        return array;

    }

}
