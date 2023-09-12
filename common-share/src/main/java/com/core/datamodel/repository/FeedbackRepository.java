package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.ContactUs;
import com.core.datamodel.model.dbmodel.Feedback;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface FeedbackRepository extends BaseRepository<Feedback, Long> {

    @Query("select f from Feedback f where f.id=:feedbackId")
    Optional<Feedback> findByEntityId(@Param("feedbackId") Long feedbackId);

    @Query("select f from Feedback f where f.id=:feedbackId and f.userId=:userId")
    Optional<Feedback> findByEntityId(@Param("feedbackId") Long feedbackId,@Param("userId") Long userId);

    @Query("select f from Feedback f order by f.id desc")
    List<Feedback> findAllFeedback(Pageable pageable);

    @Query("select f from Feedback f where f.userId=:userId order by f.id desc")
    List<Feedback> findAllFeedback(@Param("userId") Long userId, Pageable pageable);

}
