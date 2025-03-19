package com.shop.ecommerce.request;

import com.shop.ecommerce.modal.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private String content;
    private Message.MessageType messageType;
    private Long senderId;
    private Message.SenderType senderType;
    private Long chatRoomId;
    private LocalDateTime timestamp;
    private boolean read;
}