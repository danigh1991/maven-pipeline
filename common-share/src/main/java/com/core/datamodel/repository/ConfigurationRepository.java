package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Configuration;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConfigurationRepository extends BaseRepository<Configuration, Long> {

    @Query("select c  from Configuration c where c.id =?1 and c.show=true")
    Configuration findByEntityId(Long id);

    @Query(value = "select c  from Configuration c where c.name=:name and c.show=true")
    Configuration findByName(@Param("name") String name);

}
