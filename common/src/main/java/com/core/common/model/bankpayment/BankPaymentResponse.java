package com.core.common.model.bankpayment;

import com.core.datamodel.model.enums.EBank;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankPaymentResponse implements Serializable {
    private Long accountId;
    private EBank bank;
    private Double amount;
    private  String  state;
    private  String orderReferenceNumber;
    private  String  bankReferenceNumber;
    private  String  bankTransactionRef;
    private  Boolean verified=false;
    private  Boolean reverse=false;
    private  Boolean refund=false;
    private  Boolean settlement=false;
    private  Boolean rollback=false;

    private String mid;
    private String stateCode;
    private  String bankRequestNumber;

    private Long transactionId;
    private Long orderId;
    private String bankTraceNo;
    private String payerID;
    private Long userId;
    private Map<String , Object> others=new HashMap<>();
    private  Boolean status=false;
    private  String statusDesc;
    private List<Long> shipmentIdList=null;
    private Long operationRequestId;


    public BankPaymentResponse() {
    }


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public EBank getBank() {
        return bank;
    }

    public void setBank(EBank bank) {
        this.bank = bank;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getOrderReferenceNumber() {
        return orderReferenceNumber;
    }

    public void setOrderReferenceNumber(String orderReferenceNumber) {
        this.orderReferenceNumber = orderReferenceNumber;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBankReferenceNumber() {
        return bankReferenceNumber;
    }

    public void setBankReferenceNumber(String bankReferenceNumber) {
        this.bankReferenceNumber = bankReferenceNumber;
    }

    public String getBankTransactionRef() {
        return bankTransactionRef;
    }

    public void setBankTransactionRef(String bankTransactionRef) {
        this.bankTransactionRef = bankTransactionRef;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getBankRequestNumber() {
        return bankRequestNumber;
    }

    public void setBankRequestNumber(String bankRequestNumber) {
        this.bankRequestNumber = bankRequestNumber;
    }

    public Boolean getReverse() {        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public Boolean getRefund() {
        return refund;
    }

    public void setRefund(Boolean refund) {
        this.refund = refund;
    }

    public Boolean getSettlement() {
        return settlement;
    }

    public void setSettlement(Boolean settlement) {
        this.settlement = settlement;
    }

    public Boolean getRollback() {
        return rollback;
    }

    public void setRollback(Boolean rollback) {
        this.rollback = rollback;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBankTraceNo() {
        return bankTraceNo;
    }

    public void setBankTraceNo(String bankTraceNo) {
        this.bankTraceNo = bankTraceNo;
    }

    public String getPayerID() {
        return payerID;
    }

    public void setPayerID(String payerID) {
        this.payerID = payerID;
    }

    public Map<String, Object> getOthers() {
        return others;
    }

    public void setOthers(Map<String, Object> others) {
        this.others = others;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public List<Long> getShipmentIdList() {
        return shipmentIdList;
    }

    public void setShipmentIdList(List<Long> shipmentIdList) {
        this.shipmentIdList = shipmentIdList;
    }

    public Long getOperationRequestId() {
        return operationRequestId;
    }

    public void setOperationRequestId(Long operationRequestId) {
        this.operationRequestId = operationRequestId;
    }
}
