package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.PathLimitation;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PathLimitationRepository extends BaseRepository<PathLimitation, Long> {

    @Query("select b  from PathLimitation b where b.id =?1")
    PathLimitation findByEntityId(Long id);
    PathLimitation findByName(String name);

    List<PathLimitation> findAllByActiveTrueOrderByPriorityDesc();

    @Query("select p.path from PathLimitation p where p.active=true order by p.priority asc")
    List<String> findAllActivePaths();

}
