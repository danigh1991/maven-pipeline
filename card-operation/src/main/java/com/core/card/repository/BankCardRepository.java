package com.core.card.repository;

import com.core.card.model.dbmodel.BankCard;
import com.core.model.repository.BaseRepository;
import com.core.card.model.wrapper.BankCardWrapper;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BankCardRepository extends BaseRepository<BankCard, Long> {

    @Query("select e from BankCard e where e.id=:bankCardId")
    Optional<BankCard> findByEntityId(@Param("bankCardId") Long bankCardId);

    @Query("select e from BankCard e where e.id=:bankCardId and e.userId=:userId")
    Optional<BankCard> findByEntityId(@Param("bankCardId") Long bankCardId,@Param("userId") Long userId);

    @Query("select e from BankCard e where e.shpCardId=:shpCardId")
    Optional<BankCard> findByShpCardId(@Param("shpCardId") Long shpCardId);

    @Query("select e from BankCard e where e.shpCardId=:shpCardId and e.userId=:userId")
    Optional<BankCard> findByShpCardId(@Param("shpCardId") Long shpCardId,@Param("userId") Long userId);

    @Query("select e from BankCard e where e.cardNumber=:cardNumber and e.userId=:userId")
    Optional<BankCard> findByCardNumber(@Param("cardNumber") String cardNumber,@Param("userId") Long userId);


    @Query("select e from BankCard e where  e.userId=:userId  and e.expire is not null order by  e.id")
    List<BankCard> findMyByUserId(@Param("userId") Long userId);

    @Query("select e from BankCard e where  e.userId=:userId order by CASE WHEN (e.expire is null) THEN 0 ELSE 1 END  , e.id")
    List<BankCard> findAllByUserId(@Param("userId") Long userId);

    @Query("select e from BankCard e where  e.userId=:userId and e.expire is null order by  e.id")
    List<BankCard> findOtherByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true)
    BankCardWrapper findBankCardWrapperById(@Param("bankCardId") Long bankCardId);

    @Query(nativeQuery = true)
    BankCardWrapper findBankCardWrapperByIdAndUsrId(@Param("bankCardId") Long bankCardId,@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<BankCardWrapper> findMyBankCardWrappersByUsrId(@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<BankCardWrapper> findAllBankCardWrappersByUsrId(@Param("userId") Long userId);

    @Query(nativeQuery = true)
    List<BankCardWrapper> findOtherBankCardWrappersByUsrId(@Param("userId") Long userId);


    @Query(value = "select count(c) from BankCard c where c.userId=:userId and  FUNCTION('REPLACE', c.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countByName(@Param("name") String name,@Param("userId") Long userId);

    @Query(value = "select count(c) from BankCard c where c.id<>:bankCardId and c.userId=:userId and  FUNCTION('REPLACE', c.name,' ','')=FUNCTION('REPLACE', :name,' ','')" )
    Integer countByName(@Param("bankCardId") Long bankCardId,@Param("name") String name,@Param("userId") Long userId);

    @Query(value = "select count(c) from BankCard c where c.userId=:userId and  FUNCTION('REPLACE', c.cardNumber,' ','')=FUNCTION('REPLACE', :cardNumber,' ','')" )
    Integer countByCardNumber(@Param("cardNumber") String cardNumber,@Param("userId") Long userId);

    @Query(value = "select count(c) from BankCard c where c.id<>:bankCardId and c.userId=:userId and  FUNCTION('REPLACE', c.cardNumber,' ','')=FUNCTION('REPLACE', :cardNumber,' ','')" )
    Integer countByCardNumber(@Param("bankCardId") Long bankCardId,@Param("cardNumber") String cardNumber,@Param("cardNumber") Long userId);


}
