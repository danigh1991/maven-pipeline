package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.BlockedIp;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlockedIpRepository extends BaseRepository<BlockedIp, Long> {

    @Query("select b  from BlockedIp b where b.id =?1")
    BlockedIp findByEntityId(Long id);

    @Query("select b.ip from BlockedIp b where b.active=true")
    List<String> findAllActiveBlockedIps();

}
