package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Currency;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BaseCurrencyRepository extends BaseRepository<Currency, Long> {

    @Query("select l  from Currency l where l.id =:id")
    Currency findByEntityId(@Param("id") Long id);

    @Query("select l  from Currency l where l.id =:id and l.active=true")
    Currency findActiveById(@Param("id") Long id);

    @Query("select l  from Currency l where l.name =:name")
    Currency findByName(@Param("name") String name);

    @Query("select l  from Currency l where l.shortName =:shortName")
    Currency findByShortName(@Param("shortName") String shortName);

    @Query("select l  from Currency l order by l.shortName")
    List<Currency> findAll();

    @Query("select l  from Currency l where l.active =true order by l.shortName")
    List<Currency> findAllActive();

}
