package com.shop.ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {
    private Long id;
    private Long userId;
    private Long sellerId;
    private String userFullName;
    private String sellerName;
    private String sellerBusinessName;
    private LocalDateTime lastMessageTime;
    private String lastMessagePreview;
    private boolean hasUnreadMessages;
    private List<ChatMessageDTO> messages;
}