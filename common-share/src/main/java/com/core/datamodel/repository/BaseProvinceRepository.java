package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Province;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BaseProvinceRepository extends BaseRepository<Province, Long> {

    @Query("select p from Province p where p.active=true and upper(p.englishName) = upper(?1)")
    Province getProvinceByEnglishName(String englishName);


    @Query("select p from Province p where p.id=?1")
    Province findByEntityId(Long provinceId);

    @Query("select p from Province p where p.id=?1 and p.country.id=?2")
    Province findByIdAndCountryId(Long provinceId,Long countryId);

    @Query("select p from Province p where p.active =true and p.id=?1")
    Province findActiveById(Long provinceId);

    @Query("select p from Province p where p.active =true and p.id=?1 and p.country.id=?2")
    Province findActiveByIdAndCountryId(Long provinceId,Long countryId);

    @Override
    @Query(value = "SELECT p.* FROM sc_province p  ORDER BY p.prv_order,p.prv_name",nativeQuery = true)
    List<Province> findAll();

    @Query(value = "SELECT p.* FROM sc_province p where p.prv_active=true ORDER BY p.prv_order,p.prv_name",nativeQuery = true)
    List<Province> findAllActive();

    @Query("select p from Province p where p.active=true and p.country.id=?1")
    List<Province> findAllActiveByCountryId(Long countryId);
}
