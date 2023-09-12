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
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = City.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = City.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cty_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cty_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cty_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cty_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cty_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cty_modify_by"))
})
public class City extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_city";

    @Column(name = "cty_name",nullable = false)
    @Field(index= Index.YES, analyze= Analyze.YES)
    @JsonView({MyJsonView.CityList.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CITY)
    private String name;

    @Column(name = "cty_english_name")
    @JsonView(MyJsonView.CityList.class)
    @Field(index= Index.YES, analyze= Analyze.YES)
    private String englishName;

    @Column(name = "cty_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CITY)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cty_prv_id",nullable = false,referencedColumnName = "prv_id", foreignKey = @ForeignKey(name = "FK_cty_prv_id"))
    @JsonIgnoreProperties("cities")
    private Province province;

/*    @OneToMany(targetEntity = StoreBranchs.class, mappedBy = "city",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("city")
    private List<StoreBranchs> storeBranchs;*/

    @OneToMany(targetEntity = Region.class, mappedBy = "city",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("city")
    private List<Region> regions;

    @Column(name = "cty_lat",nullable = false)
    @JsonView({MyJsonView.CityView.class, MyJsonView.UserInfoView.class})
    private Double lat;
    @Column(name = "cty_lan",nullable = false)
    @JsonView({MyJsonView.CityView.class, MyJsonView.UserInfoView.class})
    private Double lan;

    @Column(name = "cty_map_zoom")
    @JsonView({MyJsonView.CityView.class, MyJsonView.UserInfoView.class})
    private Integer mapZoom;

    /*@OneToMany(targetEntity = User.class, mappedBy = "city",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("city")
    private List<User> users;*/

    @Column(name = "cty_isactive",nullable = false)
    private Boolean active;


    @Column(name = "cty_geo_regional_code")
    private String geoReonalCode;

    @Column(name = "cty_show_for_register",nullable = false)
    private Boolean showForRegister;

    @Column(name = "cty_show_for_store",nullable = false)
    private Boolean showForStore;

    @Column(name = "cty_order",nullable = false)
    private Long order;

    @JsonView({MyJsonView.CityView.class, MyJsonView.UserInfoView.class})
    @Column(name = "cty_postal_code_pattern",nullable = false)
    private String postalCodePattern;


    public String getSlugUrl() {
        String citySlugUrl = this.getEnglishName().replace(" ", "-").toLowerCase();
        return "/"+ citySlugUrl;
    }

}
