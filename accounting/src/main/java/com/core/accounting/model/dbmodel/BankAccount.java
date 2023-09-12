package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.model.dbmodel.Bank;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.exception.InvalidDataException;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name= BankAccount.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = BankAccount.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bac_id")),
        @AttributeOverride(name = "version", column = @Column(name = "bac_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "bac_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "bac_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "bac_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "bac_modify_by"))
})
public class BankAccount extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_bank_account";

    @JsonView(AccountJsonView.BankAccountList.class)
    @Column(name="bac_title",nullable = false)
    private String title;

    @Column(name="bac_usr_id",nullable = false)
    private Long userId;

    @JsonView(AccountJsonView.BankAccountList.class)
    @Column(name="bac_active",nullable = false)
    private Boolean active = true;

    @Column(name="bac_description")
    private String description;

    @JsonView(AccountJsonView.BankAccountList.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_bnk_id",referencedColumnName = "bnk_id",nullable = false, foreignKey = @ForeignKey(name = "FK_bac_bnk_id"))
    private Bank bank;

    @JsonProperty("account_number")
    @JsonView(AccountJsonView.BankAccountDetails.class)
    @Column(name="bac_account_number")
    private String accountNumber;

    @JsonProperty("iban_number")
    @JsonView(AccountJsonView.BankAccountDetails.class)
    @Column(name="bac_iban_number")
    private String ibanNumber;

    @JsonProperty("card_number")
    @JsonView(AccountJsonView.BankAccountDetails.class)
    @Column(name="bac_card_number")
    private String cardNumber;

    @JsonProperty("swift_number")
    @JsonView(AccountJsonView.BankAccountDetails.class)
    @Column(name="bac_swift_number")
    private String swiftNumber;

    @JsonProperty("paypal_account")
    @JsonView(AccountJsonView.BankAccountDetails.class)
    @Column(name="bac_paypal_account")
    private String paypalAccount;

    @JsonView(AccountJsonView.BankAccountDetails.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.BankAccountDetails.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @JsonView(AccountJsonView.BankAccountDetails.class)
    public String getStatusDesc() {
        if (this.active)
            return BaseUtils.getMessageResource("global.status.active");
        else
            return BaseUtils.getMessageResource("global.status.reject");
    }

    public String getValueByFinanceDestName(String financeDestName){
        switch (financeDestName.toLowerCase()) {
            case "account_number":
                return this.getAccountNumber();
            case "cart_number":
                return this.getCardNumber();
            case "iban_number":
                return this.getIbanNumber();
            case "swift_number":
                return this.getSwiftNumber();
            case "paypal_account":
                return this.getPaypalAccount();
            default:
                return "";
        }
    }
    public void setValueByFinanceDestName(String financeDestName,String financeDestValue){
        switch (financeDestName.toLowerCase()) {
            case "account_number":
                this.setAccountNumber(financeDestValue);
                break;
            case "cart_number":
                this.setCardNumber(financeDestValue);
                break;
            case "iban_number":
                this.setIbanNumber(financeDestValue);
                break;
            case "swift_number":
                this.setSwiftNumber(financeDestValue);
                break;
            case "paypal_account":
                this.setPaypalAccount(financeDestValue);
                break;
            default:
                break;
        }
    }

}
