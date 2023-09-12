package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.CostShareRequestWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
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
@Table(name= CostShareType.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = CostShareType.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cst_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cst_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cst_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cst_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cst_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cst_modify_by"))
})
public class CostShareType extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_cost_share_type";

    @JsonView(AccountJsonView.CostShareTypeList.class)
    @Column(name="cst_title")
    private String title;

    @JsonView(AccountJsonView.CostShareTypeDetail.class)
    @Column(name="cst_description")
    private String description;

    @JsonView(AccountJsonView.CostShareTypeList.class)
    @Column(name="cst_active")
    private Boolean active;

    @JsonView(AccountJsonView.CostShareTypeList.class)
    @Column(name="cst_svg_icon")
    private String svgIcon;

    @JsonView(AccountJsonView.CostShareTypeDetail.class)
    @Column(name="cst_order")
    private Integer order;

    @OneToMany(targetEntity=CostShareRequest.class,mappedBy = "costShareType",fetch =FetchType.LAZY)
    @JsonIgnoreProperties("costShareType")
    private List<CostShareRequest> costShareRequests = new ArrayList<>();

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


}
