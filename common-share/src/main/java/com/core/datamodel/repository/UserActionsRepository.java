package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.UserActions;
import com.core.model.repository.BaseRepository;


public interface UserActionsRepository extends BaseRepository<UserActions, Long> {
    UserActions findByName(String name);
}
