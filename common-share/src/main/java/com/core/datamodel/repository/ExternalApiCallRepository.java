package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.ExternalApi;
import com.core.datamodel.model.dbmodel.ExternalApiCall;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ExternalApiCallRepository extends BaseRepository<ExternalApiCall, Long> {

    @Query("select e from ExternalApiCall e where e.id=:id")
    Optional<ExternalApiCall> findByEntityId(@Param("id") Long id);

    @Query("select e from ExternalApiCall e where e.trackingId=:trackingId")
    Optional<ExternalApiCall> findByTrackingId(@Param("trackingId") String trackingId);

    @Query("select c from ExternalApiCall c join c.externalApi e where e.id=:id and c.requestKey=:requestKey and c.status=1 and c.responseReferenceId is null and c.responseDate>:validDate  order by c.responseDate desc")
    List<ExternalApiCall> findByNewestByExternalApiId(@Param("id") Long id,@Param("requestKey") Integer requestKey, @Param("validDate") Date validDate, Pageable pageable);

    @Query("select c from ExternalApiCall c join c.externalApi e where e.name=:externalApiName and c.requestKey=:requestKey and c.status=1 and c.responseReferenceId is null and c.responseDate>:validDate  order by c.responseDate desc")
    List<ExternalApiCall> findByNewestByExternalApiName(@Param("externalApiName") String externalApiName,@Param("requestKey") Integer requestKey, @Param("validDate") Date validDate, Pageable pageable);

}
