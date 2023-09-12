package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.view.AccountJsonView;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestTransactionWrapper implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonView(AccountJsonView.TransactionList.class)
        private Long  transactionId;
        @JsonView(AccountJsonView.TransactionList.class)
        private Integer operationTypeId;
        @JsonView(AccountJsonView.TransactionList.class)
        private String operationTypeName;
        @JsonView(AccountJsonView.TransactionList.class)
        private Integer operationTypeCode;
        private String description;
        @JsonView(AccountJsonView.TransactionDetails.class)
        private Integer transactionTypeId;
        private String transactionTypeCaption;
        @JsonView(AccountJsonView.TransactionList.class)
        private Double debit=0d;
        @JsonView(AccountJsonView.TransactionList.class)
        private Double credit=0d;
        @JsonView(AccountJsonView.TransactionList.class)
        private Integer status;
        @JsonView(AccountJsonView.TransactionList.class)
        private Long accountId;
        private String accountName;
        private Long accountUserId;
        private String accountUserName;
        private Long accountCategoryId;
        private String accountUserFirstName;
        private String accountUserLastName;
        private Long userCreditId;
        private String userCreditName;
        @JsonView(AccountJsonView.TransactionList.class)
        private Long crossUserId;
        @JsonView(AccountJsonView.TransactionList.class)
        private String crossUserInfo;
        private Date createDate;
        private Long createBy;
        private Date modifyDate;
        private Long modifyBy;


/*        public RequestTransactionWrapper(Long transactionId, Integer operationTypeId, String description,Integer transactionTypeId,String transactionTypeCaption,
                                          Double debit, Double credit, Integer status, Long accountId, String accountName, Long accountUserId, String accountUserName,Long accountCategoryId,
                                         String accountUserFirstName, String accountUserLastName,Long userCreditId, String userCreditName, Date createDate, Long createBy, Date modifyDate, Long modifyBy) {
                this.transactionId = transactionId;
                this.operationTypeId = operationTypeId;
                this.description = description;
                this.transactionTypeId = transactionTypeId;
                this.transactionTypeCaption = transactionTypeCaption;
                this.debit = debit;
                this.credit = credit;
                this.status = status;
                this.accountId = accountId;
                this.accountName = accountName;
                this.accountUserId = accountUserId;
                this.accountUserName = accountUserName;
                this.accountCategoryId = accountCategoryId;
                this.accountUserFirstName = accountUserFirstName;
                this.accountUserLastName = accountUserLastName;
                this.userCreditId = userCreditId;
                this.userCreditName = userCreditName;
                this.createDate = createDate;
                this.createBy = createBy;
                this.modifyDate = modifyDate;
                this.modifyBy = modifyBy;
        }*/


        @JsonView(AccountJsonView.TransactionList.class)
        @JsonSerialize(using = JsonDateTimeSerializer.class)
        public Date getCreateDate() {
            return createDate;
        }

        @JsonSerialize(using = JsonDateTimeSerializer.class)
        public Date getModifyDate() {
            return modifyDate;
        }

        @JsonView(AccountJsonView.TransactionList.class)
        public String getAmountDesc() {
                if (this.getCredit() > 0)
                        return BaseUtils.getMessageResource("global.deposit");
                else if (this.getDebit() > 0)
                        return BaseUtils.getMessageResource("global.withdraw");
                else
                        return "";
        }

        @JsonView(AccountJsonView.TransactionList.class)
        public String getOperationTypeCaption() {
                return EOperation.valueOfCode(operationTypeCode).getCaption();
        }

        @JsonView(AccountJsonView.TransactionDetails.class)
        public String getDescription() {
                return BaseUtils.getMessageResourceByKeyArgs(description);
        }

    }
