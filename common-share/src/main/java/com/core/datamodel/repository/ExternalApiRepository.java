package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExternalApiRepository extends BaseRepository<ExternalApi, Long> {

    @Query("select e from ExternalApi e where e.id=:id")
    Optional<ExternalApi> findByEntityId(@Param("id") Long id);

    @Query("select e from ExternalApi e where e.name=:name")
    Optional<ExternalApi> findByEntityName(@Param("name") String name);

    @Query("select r  from ExternalApi r order by r.id")
    List<ExternalApi> findAllExternalApis();

}
