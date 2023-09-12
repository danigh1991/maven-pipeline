package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = Province.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Province.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "prv_id")),
        @AttributeOverride(name = "version", column = @Column(name = "prv_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "prv_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "prv_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "prv_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "prv_modify_by"))
})
public class Province extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_province";

    @Column(name = "prv_name",nullable = false)
    @JsonView({MyJsonView.ProvinceList.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PROVINCE)
    private String name;

    @Column(name = "prv_english_name",nullable = false)
    @JsonView({MyJsonView.ProvinceList.class})
    private String englishName;

    @JsonView({MyJsonView.ProvinceDetails.class,MyJsonView.MultiLingual.class})
    @Column(name = "prv_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PROVINCE)
    private String description;

    @JsonView({MyJsonView.ProvinceDetails.class})
    @OneToMany(targetEntity = City.class, mappedBy = "province",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("province")
    private List<City> cities;

    @JsonView({MyJsonView.ProvinceList.class})
    @Column(name = "prv_active",nullable = false)
    private Boolean active;

    @Column(name = "prv_order",nullable = false)
    private Long order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prv_cnt_id",nullable = false,referencedColumnName = "cnt_id", foreignKey = @ForeignKey(name = "FK_prv_cnt_id"))
    @JsonIgnoreProperties("provinces")
    private Country country;


}
