package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Country;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BaseCountryRepository extends BaseRepository<Country, Long> {



    @Query("select c from Country c where c.id=?1")
    Country findByEntityId(Long countryId);

    @Query("select c from Country c where c.active =true and c.id=?1")
    Country findActiveById(Long countryId);


    @Query("select c from Country c where c.active =true")
    List<Country> findAllCountry();
}
