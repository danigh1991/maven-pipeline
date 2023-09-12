package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = MessageResource.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = MessageResource.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "msg_id")),
        @AttributeOverride(name = "version", column = @Column(name = "msg_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "msg_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "msg_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "msg_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "msg_modify_by"))
})
public class MessageResource extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_message_resource";

    @Column(name = "msg_type",nullable = false)
    //app=1, panelJson=2, uiJs=3
    private Integer type;


    @JsonView(MyJsonView.MessageResourceView.class)
    @Column(name = "msg_key",nullable = false)
    private String key;

    @JsonView(MyJsonView.MessageResourceView.class)
    @Column(name = "msg_content",nullable = false)
    @MultiLingual(destinationTable = ERepository.MultiLingualMessageResource,targetType = ERepository.MESSAGE)
    private String content;


}
