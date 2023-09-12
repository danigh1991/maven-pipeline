package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.City;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BaseCityRepository  extends BaseRepository<City, Long> {

    @Query("select c from City c where c.active=true and upper(c.englishName) = upper(?1)")
    City getCityByEnglishName(String englishName);


    @Query("select c from City c where c.id=?1")
    City findByEntityId(Long cityId);

    @Query("select c from City c where c.active =true and c.id=?1")
    City findActiveById(Long cityId);

    @Override
    @Query(value = "SELECT c.* FROM sc_city c where c.cty_isactive=1  ORDER BY c.cty_order,c.cty_name",nativeQuery = true)
    List<City> findAll();

    @Query(value = "SELECT c.* FROM sc_city c where c.cty_isactive=1 and c.cty_show_for_register=1 ORDER BY c.cty_order,c.cty_name",nativeQuery = true)
    List<City> findAllActiveCityForRegister();

    @Query(value = "SELECT c.* FROM sc_city c where c.cty_isactive=1 and c.cty_prv_id=?1 ORDER BY c.cty_order,c.cty_name",nativeQuery = true)
    List<City> findAllActiveCity(Long provinceId);

    @Query(value = "SELECT c.* FROM sc_city c where c.cty_isactive=1 and c.cty_show_for_store=1 ORDER BY c.cty_order,c.cty_name",nativeQuery = true)
    List<City> findAllActiveCityForStore();

}
