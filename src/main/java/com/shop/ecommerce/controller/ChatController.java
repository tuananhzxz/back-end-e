package com.shop.ecommerce.controller;

import com.shop.ecommerce.modal.Message;
import com.shop.ecommerce.request.ChatMessageDTO;
import com.shop.ecommerce.request.ChatRoomDTO;
import com.shop.ecommerce.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket endpoint to handle sending messages
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO messageDTO) {
        ChatMessageDTO savedMessage = chatService.sendMessage(messageDTO);
    }

    @GetMapping("/room")
    public ResponseEntity<ChatRoomDTO> getChatRoom(
            @RequestParam Long userId,
            @RequestParam Long sellerId) {
        return ResponseEntity.ok(chatService.getOrCreateChatRoom(userId, sellerId));
    }


    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<ChatRoomDTO>> getUserChatRooms(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserChatRooms(userId));
    }

    @GetMapping("/rooms/seller/{sellerId}")
    public ResponseEntity<List<ChatRoomDTO>> getSellerChatRooms(@PathVariable Long sellerId) {
        return ResponseEntity.ok(chatService.getSellerChatRooms(sellerId));
    }


    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatService.getChatMessages(chatRoomId));
    }

    @PostMapping("/messages/read")
    public ResponseEntity<?> markMessagesAsRead(
            @RequestParam Long chatRoomId,
            @RequestParam String readerType) {
        Message.SenderType senderType = Message.SenderType.valueOf(readerType.toUpperCase());
        chatService.markMessagesAsRead(chatRoomId, senderType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/room/{chatRoomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.ok().build();
    }
}