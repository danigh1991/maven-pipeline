package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.SiteTheme;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteThemeRepository extends BaseRepository<SiteTheme, Long> {

    @Query("select a  from SiteTheme a where a.id =?1")
    SiteTheme findByEntityId(Long siteThemeId);

    @Query("select a  from SiteTheme a where a.active=true and a.id =?1")
    SiteTheme findActiveById(Long siteThemeId);

    @Query("select a  from SiteTheme a where a.active=true")
    List<SiteTheme> findAllActives();

    @Query("select a  from SiteTheme a")
    List<SiteTheme> findAll();

}
