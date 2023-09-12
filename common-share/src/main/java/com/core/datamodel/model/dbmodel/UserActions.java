package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import com.core.model.dbmodel.AbstractMultiLingualEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = UserActions.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = UserActions.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
/*@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "ual_id")),
        @AttributeOverride(name = "version", column = @Column(name = "ual_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "ual_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "ual_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "ual_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "ual_modify_by"))
})*/
public class UserActions extends AbstractMultiLingualEntity /*AbstractBaseMultiLingualEntity */{
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_actions";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = AbstractBaseEntity.DEFAULT_SEQ_GEN)
    @Column(name="ual_id",unique = true, nullable = false)
    private Long id;

    @Column(name="ual_name",nullable = false)
    private String name;

    @Column(name = "ual_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.USER_ACTION)
    private String description;

    @OneToMany(targetEntity = UserLog.class, mappedBy = "userAction", fetch = FetchType.LAZY,cascade = CascadeType.DETACH)
    @JsonIgnoreProperties("userAction")
    private List<UserLog> userLogs;

}
