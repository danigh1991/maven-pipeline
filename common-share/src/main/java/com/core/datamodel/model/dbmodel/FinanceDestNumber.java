package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = FinanceDestNumber.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = FinanceDestNumber.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "fdn_id")),
        @AttributeOverride(name = "version", column = @Column(name = "fdn_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "fdn_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "fdn_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "fdn_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "fdn_modify_by"))
})
public class FinanceDestNumber extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_finance_dest_number";

    @JsonView(MyJsonView.FinanceDestNumberList.class)
    @Column(name="fdn_name",nullable = false)
    private String name;

    @JsonView(MyJsonView.FinanceDestNumberList.class)
    @Column(name="fdn_caption",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.FINANCE_DEST_NUMBER)
    private String caption;

    @JsonView(MyJsonView.FinanceDestNumberDetails.class)
    @Column(name="fdn_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.FINANCE_DEST_NUMBER)
    private String description;

    @JsonView(MyJsonView.FinanceDestNumberList.class)
    @Column(name="fdn_mask")
    private String mask;

    @JsonView(MyJsonView.FinanceDestNumberList.class)
    @Column(name="fdn_active",nullable = false)
    private Boolean active;

    @JsonView(MyJsonView.FinanceDestNumberList.class)
    @Column(name="fdn_mandatory",nullable = false)
    private String mandatory;


}
