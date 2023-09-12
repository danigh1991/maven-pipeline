package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = SmsBodyCode.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = SmsBodyCode.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "sbc_id")),
        @AttributeOverride(name = "version", column = @Column(name = "sbc_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "sbc_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "sbc_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "sbc_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "sbc_modify_by"))
})
public class SmsBodyCode extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_sms_body_code";

    @Column(name = "sbc_title",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.SMS_BODY_CODE)
    private String title;

    @Column(name = "sbc_code",nullable = false)
    private Integer code;

    @Column(name = "sbc_desc",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.SMS_BODY_CODE)
    private String description;

    public String getDescFormatReplaceArg() {
        return this.getDescription().replaceAll("\\{([0-9]*)\\}","%s");
    }

}
