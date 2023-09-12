package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.MerchantCustomerWrapper;
import com.core.accounting.model.wrapper.MerchantWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="merchantCustomerWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= MerchantCustomerWrapper.class,
                                columns={
                                        @ColumnResult(name="merchantCustomerId",type = Long.class),
                                        @ColumnResult(name="merchantId",type = Long.class),
                                        @ColumnResult(name="merchantUserId",type = Long.class),
                                        @ColumnResult(name="customerUserId",type = Long.class),
                                        @ColumnResult(name="customerUserName",type = String.class),
                                        @ColumnResult(name="firstName",type = String.class),
                                        @ColumnResult(name="lastName",type = String.class),
                                        @ColumnResult(name="score",type = Long.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class)
                                }
                        )
                }
        )
})
@Table(name= MerchantCustomer.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = MerchantCustomer.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "mcs_id")),
        @AttributeOverride(name = "version", column = @Column(name = "mcs_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "mcs_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "mcs_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "mcs_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "mcs_modify_by"))
})
public class MerchantCustomer extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_merchant_customer";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mcs_mrc_id",referencedColumnName = "mrc_id", foreignKey = @ForeignKey(name = "fk_mcs_mrc_id"),nullable = false)
    @JsonIgnoreProperties("merchants")
    private Merchant merchant;

    @Column(name="mcs_usr_id",nullable = false)
    private Long userId;

    @Column(name="mcs_score")
    private Long score=0l;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.MerchantDetail.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
