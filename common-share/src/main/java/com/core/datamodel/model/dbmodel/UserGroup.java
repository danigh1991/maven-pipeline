package com.core.datamodel.model.dbmodel;


import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = UserGroup.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserGroup.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "usg_id")),
        @AttributeOverride(name = "version", column = @Column(name = "usg_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "usg_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "usg_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "usg_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "usg_modify_by"))
})
public class UserGroup extends AbstractBaseMultiLingualEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_group";

    @Column(name = "usg_name",nullable = false)
    @JsonView({MyJsonView.UserGroupList.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.USER_GROUP)
    private String name;

    @Column(name = "usg_description")
    @JsonView({MyJsonView.UserGroupDetails.class,MyJsonView.MultiLingual.class})
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.USER_GROUP)
    private String description;

    @Column(name = "usg_active",nullable = false)
    @JsonView(MyJsonView.UserGroupList.class)
    private Boolean active=false;

    /*@OneToMany(targetEntity=User.class,mappedBy = "userGroup",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("userGroup")
    private List<User> Users  = new ArrayList<>();*/

    @JsonView(MyJsonView.UserGroupList.class)
    @Column(name = "usg_trt_id",nullable = false)
    private Long targetTypeId;

    @JsonView(MyJsonView.UserGroupList.class)
    //targetTypeId=2 ==> targetId is store branch, targetTypeId=31 ==> targetId is user
    @Column(name = "usg_target_id",nullable = false)
    private Long targetId;

}
