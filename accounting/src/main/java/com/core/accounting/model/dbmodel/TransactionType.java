package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.model.annotations.MultiLingual;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = TransactionType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = TransactionType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "tnt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "tnt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "tnt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "tnt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "tnt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "tnt_modify_by"))
})
public class TransactionType extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_transaction_type";

    @Column(name = "tnt_name",nullable = false)
    //@JsonView({MyJsonView.TransactionTypeList.class,MyJsonView.MultiLingual.class})
    private String name;

    @Column(name = "tnt_caption",nullable = false)
    //@JsonView({MyJsonView.TransactionTypeDetails.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.TRANSACTION_TYPE)
    private String caption;

    @Column(name = "tnt_active",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Boolean active;

    @Column(name = "tnt_system_only ",nullable = false)
    @JsonView(AccountJsonView.OperationTypeDetails.class)
    private Boolean systemOnly ;

    @OneToMany(targetEntity = Transaction.class ,mappedBy = "transactionType",fetch =FetchType.LAZY)
    @JsonIgnoreProperties("transactionType")
    private List<Transaction> transactions = new ArrayList<Transaction>();

    @OneToMany(targetEntity = OperationRequest.class,mappedBy = "transactionType",fetch =FetchType.LAZY )
    @JsonIgnoreProperties("transactionType")
    private List<OperationRequest> operations = new ArrayList<>();

}
