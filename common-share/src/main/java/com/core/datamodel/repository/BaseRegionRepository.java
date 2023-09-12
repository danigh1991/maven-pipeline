package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Region;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//@NoRepositoryBean
public interface BaseRegionRepository extends BaseRepository<Region, Long> {

    @Query("select r from Region r where r.id=?1")
    Region findByEntityId(Long regionId);

    @Query("select r from Region r join r.city c where upper(c.englishName) = upper(?1) and upper(r.englishName) = upper(?2)")
    Region getRegionByCityAndRegionEnglishName(String cityEnglishName,String regionEnglishName);

    @Query("select r from Region r join r.city c where c.id = ?1 and upper(r.name) = upper(?2)")
    Region getRegionByCityAndRegionPersianName(Long cityId,String persianName);

    @Query("select r from Region r where r.city.id = ?1 order by r.name")
    List<Region> findAllByCityId(Long cityId);

    @Query("select r from Region r join r.city c where c.id = ?1 and r.id = ?2")
    Region getRegionByIdAndCityId(Long cityId,Long regionId);


}
