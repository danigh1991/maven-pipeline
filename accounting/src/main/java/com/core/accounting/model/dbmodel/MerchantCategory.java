package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.MerchantWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name= MerchantCategory.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = MerchantCategory.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "mrg_id")),
        @AttributeOverride(name = "version", column = @Column(name = "mrg_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "mrg_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "mrg_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "mrg_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "mrg_modify_by"))
})
public class MerchantCategory extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_merchant_category";

    @JsonView(AccountJsonView.MerchantCategoryList.class)
    @Column(name="mrg_name",nullable = false)
    private String name;

    @JsonView(AccountJsonView.MerchantCategoryDetail.class)
    @Column(name="mrg_description")
    private String description;

    @JsonView(AccountJsonView.MerchantCategoryDetail.class)
    @Column(name="mrg_active" ,nullable = false)
    private Boolean active;

    @JsonView(AccountJsonView.MerchantCategoryDetail.class)
    @Column(name="mrg_code",nullable = false)
    private Integer code;

    @JsonView(AccountJsonView.MerchantCategoryDetail.class)
    @Column(name="mrg_transfer_operation_code",nullable = false)
    private Integer transferOperationCode;

    @OneToMany(targetEntity= Merchant.class,mappedBy = "merchantCategory",fetch =FetchType.LAZY)
    @JsonIgnoreProperties("merchantCategory")
    private List<Merchant> merchants = new ArrayList<>();

    @JsonView(AccountJsonView.MerchantCategoryDetail.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.MerchantCategoryDetail.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }
}
