package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = Currency.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Currency.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cur_id")),
        @AttributeOverride(name = "version", column = @Column(name = "cur_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "cur_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "cur_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "cur_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "cur_modify_by"))
})
public class Currency extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_currency";


    @Column(name = "cur_name",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CURRENCY)
    private String name;

    @Column(name = "cur_short_name",nullable = false)
    private String shortName;

    @Column(name = "cur_symbol")
    private String symbol;

    @Column(name = "cur_rnd_num",nullable = false)
    private Integer rndNumCount;

    @Column(name = "cur_active",nullable = false)
    private Boolean active;

   @JsonSerialize(using = JsonDateTimeSerializer.class)
   public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }
}
