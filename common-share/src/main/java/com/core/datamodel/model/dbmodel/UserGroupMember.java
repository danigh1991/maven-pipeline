package com.core.datamodel.model.dbmodel;


import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = UserGroupMember.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserGroupMember.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "ugm_id")),
        @AttributeOverride(name = "version", column = @Column(name = "ugm_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "ugm_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "ugm_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "ugm_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "ugm_modify_by"))
})
public class UserGroupMember extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_group_member";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ugm_usg_id",nullable = false,referencedColumnName = "usg_id", foreignKey = @ForeignKey(name = "ugm_usg_id"))
    @JsonIgnoreProperties("userGroupMembers")
    private UserGroup userGroup;

    @Column(name = "ugm_usr_id",nullable = false)
    private Long userId;

}
