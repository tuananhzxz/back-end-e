package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUserId(Long userId);
    List<ChatRoom> findBySellerId(Long sellerId);

    Optional<ChatRoom> findByUserIdAndSellerId(Long userId, Long sellerId);
}