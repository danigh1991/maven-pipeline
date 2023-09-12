package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = Region.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Region.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "crg_id")),
        @AttributeOverride(name = "version", column = @Column(name = "crg_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "crg_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "crg_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "crg_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "crg_modify_by"))
})
public class Region extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_city_region";

    @Column(name = "crg_name",nullable = false)
    @JsonView({MyJsonView.RegionList.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.REGION)
    private String name;

    @Column(name = "crg_english_name",nullable = false)
    @JsonView(MyJsonView.RegionList.class)
    private String englishName;

    @Column(name = "crg_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.REGION)
    private String description;


    @Column(name = "crg_lat",nullable = false)
    @JsonView({MyJsonView.RegionView.class})
    private Double lat;
    @Column(name = "crg_lan",nullable = false)
    @JsonView({MyJsonView.RegionView.class})
    private Double lan;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crg_cty_id",nullable = false,referencedColumnName = "cty_id", foreignKey = @ForeignKey(name = "FK_crg_cty_id"))
    @JsonIgnoreProperties("regions")
    private City city;

    public String getUrl(String cityName) {
        String slugUrl = this.getName();
        return BaseUtils.prepareUrl("/discount-region/"+cityName +"/"+ slugUrl);
    }
    public String getUrl(String cityName,String regionName) {
        String slugUrl = BaseUtils.toSlug(regionName);
        return BaseUtils.prepareUrl("/discount-region/"+cityName +"/"+ slugUrl);
    }
}
