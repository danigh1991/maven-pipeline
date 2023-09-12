package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.UserAttempts;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserAttemptsRepository extends BaseRepository<UserAttempts, Long> {

    @Query("select b  from UserAttempts b where b.id =?1")
    UserAttempts findByEntityId(Long id);

}