package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.ContactUs;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface ContactUsRepository extends BaseRepository<ContactUs, Long> {

    @Query("select cu from ContactUs cu where cu.id=?1")
    ContactUs findByEntityId(Long contactId);



    @Query("select cu from ContactUs cu order by cu.createDate desc,cu.id")
    List<ContactUs> findAllContact(Pageable pageable);


    @Query("select cu from ContactUs cu where cu.reply is null order by cu.createDate desc,cu.id")
    List<ContactUs> findAllNoReplyContact(Pageable pageable);
}
