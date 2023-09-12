package com.core.datamodel.model.dbmodel;

import com.core.model.dbmodel.AbstractEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = UserAttempts.TABLE_NAME)
public class UserAttempts  extends AbstractEntity{
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user_attempts";

    @Id
    @Column(name="usa_usr_id",nullable = false)
    private Long id;
    @Column(name="usa_attempts",nullable = false)
    private Long attempts;
    @Column(name="usa_lastmodified",nullable = false)
    private Date lastmodified;

}
