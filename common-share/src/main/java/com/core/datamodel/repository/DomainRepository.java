package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Domain;
import com.core.datamodel.model.wrapper.DomainWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface DomainRepository extends BaseRepository<Domain, Long> {

    @Query("select l  from Domain l where l.id =:id")
    Domain findByEntityId(@Param("id") Long id);

    @Query("select l  from Domain l where l.id =:id and l.active=true")
    Domain findActiveById(@Param("id") Long id);

    @Query("select l  from Domain l order by l.order")
    List<Domain> findAll();

    @Query("select l  from Domain l where l.active =true order by l.order")
    List<Domain> findAllActive();

    @Query(nativeQuery = true)
    List<DomainWrapper> getActiveDomainWrappersByUrl(@Param("domainUrl") String domainUrl);
    @Query(nativeQuery = true)
    DomainWrapper getTop1ActiveDomainWrapperByUrl(@Param("domainUrl") String domainUrl);
    @Query(nativeQuery = true)
    DomainWrapper getTop1ActiveDomainWrapper();
    @Query(nativeQuery = true)
    DomainWrapper getActiveDomainWrapperById(@Param("domainId") Long domainId);
}
