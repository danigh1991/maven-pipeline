package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.MessageBox;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.model.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MessageBoxRepository extends BaseRepository<MessageBox, Long> {

    @Query("select c from MessageBox c where c.id=:messageBoxId")
    Optional<MessageBox> findByEntityId(@Param("messageBoxId") Long messageBoxId);

    @Query("select c from MessageBox c where c.senderId=:senderId")
    List<MessageBox> findAllSendMessageBox(@Param("senderId") Long senderId);

    @Query("select c from MessageBox c where c.receiverId =:receiverId")
    List<MessageBox> findAllReceiveMessageBox(@Param("receiverId") Long receiverId);


    @Query(nativeQuery = true)
    List<MessageBoxWrapper> findWrapperByReceiverId(@Param("receiverId") Long receiverId, Pageable pageable);

    @Query(nativeQuery = true)
    List<MessageBoxWrapper> findNoSeenWrapperByReceiverId(@Param("receiverId") Long receiverId, Pageable pageable);


    @Query(nativeQuery = true)
    Integer findNoSeenCountByReceiverId(@Param("receiverId") Long receiverId);
}
