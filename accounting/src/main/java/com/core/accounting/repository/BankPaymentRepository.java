package com.core.accounting.repository;

import com.core.accounting.model.dbmodel.BankPayment;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BankPaymentRepository extends BaseRepository<BankPayment, Long> {

    @Query("select bp  from BankPayment bp where bp.id =?1 ")
    BankPayment findByEntityId(Long id);

    @Query("select bp  from BankPayment bp where bp.myReferenceNumber=:myReferenceNumber")
    BankPayment findByMyReferenceNumber(@Param("myReferenceNumber") String myReferenceNumber);


    @Query("select count(bp)  from BankPayment bp where bp.status>0 and bp.bankReferenceNumber =?1")
    Integer countSuccessPaymentByBankReferenceNumber(String bankReferenceNumber);

    @Query("select count(bp)  from BankPayment bp where bp.status>0 and bp.myReferenceNumber =?1")
    Integer countSuccessPaymentByMyReferenceNumber(String myReferenceNumber);

    @Query("select bp  from BankPayment bp where (:status=-1 or bp.status=:status) order by bp.id desc")
    List<BankPayment> findBankPayments(@Param("status") Integer status , Pageable pageable);

}
