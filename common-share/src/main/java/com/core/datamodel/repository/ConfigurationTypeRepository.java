package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.ConfigurationType;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConfigurationTypeRepository extends BaseRepository<ConfigurationType, Long> {

    @Query("select ct  from ConfigurationType ct where ct.id =?1")
    ConfigurationType findByEntityId(Long id);

    @Query("select ct  from ConfigurationType ct where ct.sysName=:sysName")
    List<ConfigurationType> findBySysName(@Param("sysName") String sysName);

    @Query("select ct  from ConfigurationType ct order by ct.id")
    List<ConfigurationType> findAllOrOrderById();




}
