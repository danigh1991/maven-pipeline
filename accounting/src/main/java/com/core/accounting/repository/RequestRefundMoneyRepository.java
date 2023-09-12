package com.core.accounting.repository;


import com.core.accounting.model.dbmodel.RequestRefundMoney;
import com.core.accounting.model.wrapper.RequestRefundMoneyWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RequestRefundMoneyRepository extends BaseRepository<RequestRefundMoney, Long> {

    @Query("select bp  from RequestRefundMoney bp where bp.id =?1")
    RequestRefundMoney findByEntityId(Long id);

    @Query("select bp  from RequestRefundMoney bp where bp.id =?1 and bp.reqUserId=?2")
    RequestRefundMoney findByEntityId(Long id,Long userId);

    /*@Query(nativeQuery = true)
    RequestRefundMoneyWrapper findWrapperById(@Param("id") Long id);

    @Query(nativeQuery = true)
    RequestRefundMoneyWrapper findWrapperByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);*/

    /*@Query(nativeQuery = true)
    List<RequestRefundMoneyWrapper> findAllByUserId(@Param("userId") Long userId, Pageable pageable);*/

    /*@Query(nativeQuery = true)
    List<RequestRefundMoneyWrapper> findAllRequest(Pageable pageable);*/
}
