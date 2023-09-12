package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.RedirectUrl;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RedirectUrlRepository extends BaseRepository<RedirectUrl, Long> {

    @Query("select c from RedirectUrl c where c.id=?1")
    RedirectUrl findByEntityId(Long id);

    @Query("select c from RedirectUrl c where c.active =true and c.id=?1")
    RedirectUrl findActiveById(Long id);


    @Query("select c from RedirectUrl c where c.active =true")
    List<RedirectUrl> findAllActiveRedirectUrl();
}
