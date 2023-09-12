package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.SmsBodyCode;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SmsBodyCodeRepository extends BaseRepository<SmsBodyCode, Long> {

    @Query("select b from SmsBodyCode b where b.id=:id ")
    SmsBodyCode findByEntityId(@Param("id") Long id);

    @Query("select b from SmsBodyCode b ")
    List<SmsBodyCode> findAll();


}
