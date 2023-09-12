package com.core.card.services.impl;

import com.core.accounting.model.enums.EOperation;
import com.core.card.model.dbmodel.BankCard;
import com.core.card.model.dbmodel.BankCardOperation;
import com.core.card.repository.BankCardOperationRepository;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Component("cardOperationUtil")
public class CardOperationUtil {

    private BankCardOperationRepository bankCardOperationRepository;

    @Autowired
    public CardOperationUtil(BankCardOperationRepository bankCardOperationRepository) {
        this.bankCardOperationRepository = bankCardOperationRepository;
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BankCardOperation createBankCardOperation(BankCard sourceBankCard, String targetCard, String description, EOperation eOperationType, String billId,String billPay, Double amount){
        BankCardOperation bankCardOperation=new BankCardOperation();
        bankCardOperation.setBankCard(sourceBankCard);
        bankCardOperation.setDescription(description);
        bankCardOperation.setOperationTypeId(eOperationType.getId());
        bankCardOperation.setTrackingId(Utils.createUniqueRandom());
        bankCardOperation.setTargetCard(targetCard);
        bankCardOperation.setUserId(Utils.getCurrentUserId());
        bankCardOperation.setBillId(billId);
        bankCardOperation.setBillPay(billPay);
        bankCardOperation.setAmount(amount);
        bankCardOperation.setLastStatusDate(new Date());
        bankCardOperation=bankCardOperationRepository.save(bankCardOperation);
        return  bankCardOperation;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BankCardOperation editBankCardOperation(BankCardOperation bankCardOperation, Integer status , String shpCardHolderName, String shpTransactionId, Date shpTransactionDate,
                                                   String shpApprovalCode, Date shpRegistrationDate,String shpRrn ,Integer shpStan,String errorDescription){
        bankCardOperation.setStatus(status);
        bankCardOperation.setLastStatusDate(new Date());
        bankCardOperation.setShpCardHolderName(shpCardHolderName);
        bankCardOperation.setShpTransactionId(shpTransactionId);
        bankCardOperation.setShpTransactionDate(shpTransactionDate);
        bankCardOperation.setShpApprovalCode(shpApprovalCode);
        bankCardOperation.setShpRegistrationDate(shpRegistrationDate);
        bankCardOperation.setShpRrn(shpRrn);
        bankCardOperation.setShpStan(shpStan);
        bankCardOperation.setErrorDescription(errorDescription);
        bankCardOperation=bankCardOperationRepository.save(bankCardOperation);
        return  bankCardOperation;
    }

    public BankCardOperation getBankCardOperation(Long bankCardOperationId){
        if (bankCardOperationId==null)
            throw new ResourceNotFoundException("Invalid Data", "common.bankCardOperation.id_required");

        Optional<BankCardOperation> result= bankCardOperationRepository.findByEntityId(bankCardOperationId,Utils.getCurrentUserId());
        if (!result.isPresent())
            throw new ResourceNotFoundException(bankCardOperationId.toString(), "common.bankCardOperation.id_notFound");

        return result.get();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BankCardOperation editBankCardOperationStatus(Long bankCardOperationId, Integer status){
        return this.editBankCardOperationStatus(bankCardOperationId,status, null);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BankCardOperation editBankCardOperationStatus(Long bankCardOperationId, Integer status, String errorDesc){
        return this.editBankCardOperationStatus(this.getBankCardOperation(bankCardOperationId),status, errorDesc);
    }

    public BankCardOperation editBankCardOperationStatus(BankCardOperation bankCardOperation, Integer status){
        return this.editBankCardOperationStatus(bankCardOperation,status, null);
    }

    public BankCardOperation editBankCardOperationStatus(BankCardOperation bankCardOperation, Integer status, String errorDesc){
        bankCardOperation.setStatus(status);
        bankCardOperation.setLastStatusDate(new Date());
        if(!Utils.isStringSafeEmpty(errorDesc))
            bankCardOperation.setErrorDescription(errorDesc);

        bankCardOperation=bankCardOperationRepository.save(bankCardOperation);
        return  bankCardOperation;
    }


}
