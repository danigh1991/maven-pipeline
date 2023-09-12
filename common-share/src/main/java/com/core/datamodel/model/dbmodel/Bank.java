package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
//import com.core.model.annotations.MultiLingual;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name=Bank.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Bank.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bnk_id")),
        @AttributeOverride(name = "version", column = @Column(name = "bnk_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "bnk_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "bnk_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "bnk_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "bnk_modify_by"))
})
public class Bank  extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_bank";

    @Column(name = "bnk_name",nullable = false)
    @JsonView({MyJsonView.BankList.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.BANK)
    private String name;

    @Column(name = "bnk_ename")
    @JsonView(MyJsonView.BankList.class)
    private String eName;

    @Column(name = "bnk_status",nullable = false)
    @JsonView(MyJsonView.BankDetails.class)
    private Integer status;

    @Column(name = "bnk_allow_account_intro",nullable = false)
    @JsonView(MyJsonView.BankDetails.class)
    private Boolean allowAccountIntro;

    /*@OneToMany(targetEntity=BankPayment.class,mappedBy = "bank",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("bank")
    private List<BankPayment> bankPayments = new ArrayList<>();


    @OneToMany(targetEntity=Account.class,mappedBy = "bank",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("bank")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(targetEntity=RequestRefundMoney.class,mappedBy = "payBank",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("payBank")
    private List<RequestRefundMoney> payRequestRefundMonies = new ArrayList<>();

    @OneToMany(targetEntity=RequestRefundMoney.class,mappedBy = "toBank",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("toBank")
    private List<RequestRefundMoney> toRequestRefundMonies = new ArrayList<>();*/


}
