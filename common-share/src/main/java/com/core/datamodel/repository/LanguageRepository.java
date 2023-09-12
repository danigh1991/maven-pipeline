package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Language;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LanguageRepository extends BaseRepository<Language, Long> {

    @Query("select l  from Language l where l.id =:id")
    Language findByEntityId(@Param("id") Long id);

    @Query("select l  from Language l where l.id =:id and l.active=true")
    Language findActiveById(@Param("id") Long id);

    @Query("select l  from Language l where l.name =:name")
    Language findByName(@Param("name") String name);

    @Query("select l  from Language l where l.shortName =:shortName")
    Language findByShortName(@Param("shortName") String shortName);

    @Query("select l  from Language l where l.shortName =:shortName and l.active=true")
    Language findActiveByShortName(@Param("shortName") String shortName);


    @Query("select l  from Language l order by l.shortName")
    List<Language> findAll();

    @Query("select l  from Language l where l.active =true order by l.shortName")
    List<Language> findAllActive();

    @Query(value = "select count(l) from Language l where l.siteTheme.id=:siteThemeId and l.active=true")
    Integer countActiveByThemeId(@Param("siteThemeId") Long siteThemeId);


}
