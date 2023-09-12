package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = Country.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Country.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cnt_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cnt_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cnt_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cnt_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cnt_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cnt_modify_by"))
})
public class Country extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_country";


    @Column(name = "cnt_name",nullable = false)
    @JsonView({MyJsonView.CountryList.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.COUNTRY)
    private String name;

    @Column(name = "cnt_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.COUNTRY)
    private String description;

    /*@OneToMany(targetEntity=Brand.class,mappedBy = "country",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("country")
    private List<Brand> brands ;*/

    @OneToMany(targetEntity = Province.class, mappedBy = "country",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("country")
    private List<Province> provinces;

    @Column(name = "cnt_isactive",nullable = false)
    private Boolean active;


    /*public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }*/

}
