package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    long countByChatRoomIdAndSenderTypeAndReadFalse(Long chatRoomId, Message.SenderType senderType);
    Message findFirstByChatRoomIdOrderByTimestampDesc(Long chatRoomId);
    void deleteByChatRoomId(Long chatRoomId);
    List<Message> findByChatRoomIdAndContentContainingIgnoreCaseOrderByTimestampDesc(Long chatRoomId, String searchText);
    List<Message> findBySenderIdAndSenderType(Long senderId, Message.SenderType senderType);

    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.senderType = :senderType AND m.read = false")
    List<Message> findByChatRoomIdAndSenderTypeAndReadFalse(
            @Param("chatRoomId") Long chatRoomId,
            @Param("senderType") Message.SenderType senderType
    );
}
