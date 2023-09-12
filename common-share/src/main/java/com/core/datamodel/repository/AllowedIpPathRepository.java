package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.AllowedIpPath;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AllowedIpPathRepository extends BaseRepository<AllowedIpPath, Long> {

    @Query("select b  from AllowedIpPath b where b.id =?1")
    AllowedIpPath findByEntityId(Long id);

    @Query("select b from AllowedIpPath b where b.active=true")
    List<AllowedIpPath> findActiveAllowedIpPaths();

    @Query("select b from AllowedIpPath b ")
    List<AllowedIpPath> findAllAllowedIpPaths();

}
