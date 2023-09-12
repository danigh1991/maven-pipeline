package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.MessageResource;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageResourceRepository extends BaseRepository<MessageResource, Long> {



    @Query("select m from MessageResource m where m.id=?1")
    MessageResource findByEntityId(Long countryId);

    @Query("select m from MessageResource m where m.key=?1")
    MessageResource findByKey(String key);

    @Query("select m from MessageResource m where (m.type=:typeId or :typeId=0)")
    List<MessageResource> findAllMessages(@Param("typeId") Integer typeId);
}
