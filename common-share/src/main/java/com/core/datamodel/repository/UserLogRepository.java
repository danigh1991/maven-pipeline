package com.core.datamodel.repository;


import com.core.datamodel.model.dbmodel.UserLog;
import com.core.datamodel.model.wrapper.UserLogWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserLogRepository extends BaseRepository<UserLog, Long> {

    @Query("select ul  from UserLog ul where ul.id =?1")
    UserLog findByEntityId(Long id);

    @Query("select ul  from UserLog ul where ul.id =?1 and ul.userId=?2")
    UserLog findByEntityId(Long id, Long userId);

    @Query("select ul  from UserLog ul where ul.userId=?1")
    List<UserLog> findByUserId(Long userId);


    @Query(nativeQuery = true)
    List<UserLogWrapper> findWrapperByUserId(@Param("userId") Long userId);

}
