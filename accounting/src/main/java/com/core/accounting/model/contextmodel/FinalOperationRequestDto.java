package com.core.accounting.model.contextmodel;

import com.core.datamodel.util.ShareUtils;
import com.core.util.BaseUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalOperationRequestDto extends OperationRequestDto {

    private Double sumFromAccountsAmount;
    private Double sumToAccountsAmount;
    private Double onlineAmount;
    private Boolean onlinePayment;
    private Long targetOnlineAccountId ;
    private Boolean self=false ;
    private Double wage=0d ;
    private Double onlineWage=0d ;
    private Double onlineAmountAndWage;

    public FinalOperationRequestDto(OperationRequestDto operationRequestDto) {
        super(operationRequestDto.getOperationTypeCode(), operationRequestDto.getFromAccounts(), operationRequestDto.getToAccounts(), operationRequestDto.getDescription(), operationRequestDto.getPlatform(),operationRequestDto.getTransactionTypeId(),operationRequestDto.getReferenceOperationTypeCode(), operationRequestDto.getReferenceId());
    }

    public Double getSumFromAccountsAmount(){
        if(sumFromAccountsAmount==null)
           this.calcSumFromAccountsAmount();
        return  this.sumFromAccountsAmount;
    }
    public Double calcSumFromAccountsAmount(){
        if (this.getFromAccounts() == null || this.getFromAccounts().size() <= 0)
            this.sumFromAccountsAmount= 0d;
        else
            this.sumFromAccountsAmount= BaseUtils.round(((Double) this.getFromAccounts().stream().mapToDouble(TargetAccountDto::getAmount).sum()).doubleValue(), ShareUtils.getPanelCurrencyRndNumCount());
        return this.sumFromAccountsAmount;
    }

    public Double getSumToAccountsAmount(){
        if(sumToAccountsAmount==null)
            this.calcSumToAccountsAmount();
        return this.sumToAccountsAmount;
    }

    public Double calcSumToAccountsAmount(){
        if (this.getToAccounts()==null || this.getToAccounts().size()<=0)
            this.sumToAccountsAmount= 0d;
        else
            this.sumToAccountsAmount=BaseUtils.round(((Double)this.getToAccounts().stream().mapToDouble(TargetAccountDto::getAmount).sum()).doubleValue(), ShareUtils.getPanelCurrencyRndNumCount());
        return this.sumToAccountsAmount;
    }

    public Double getOnlineAmount(){
       if(this.onlineAmount==null)
           this.onlineAmount= BaseUtils.round(this.getSumToAccountsAmount()  - this.getSumFromAccountsAmount(), ShareUtils.getPanelCurrencyRndNumCount());
       return this.onlineAmount;
    }

    public Double getOnlineAmountAndWage(){
        if(this.onlineAmountAndWage==null)
            onlineAmountAndWage=this.getOnlineAmount()+ this.getOnlineWage();
        return this.onlineAmountAndWage;
    }

    public Boolean hasOnlinePayment(){
        //this.onlinePayment=this.getOnlineAmount()>0;
       this.onlinePayment=this.getOnlineAmountAndWage()>0;
       return this.onlinePayment;
    }

}
