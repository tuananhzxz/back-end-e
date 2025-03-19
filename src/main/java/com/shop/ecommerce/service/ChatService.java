package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.ChatRoom;
import com.shop.ecommerce.modal.Message;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.ChatRoomRepository;
import com.shop.ecommerce.repository.MessageRepository;
import com.shop.ecommerce.repository.SellerRepository;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.request.ChatMessageDTO;
import com.shop.ecommerce.request.ChatRoomDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageDTO sendMessage(ChatMessageDTO messageDTO) {
        log.info("Sending message: {}", messageDTO);

        if (messageDTO.getSenderId() == null || messageDTO.getSenderType() == null) {
            throw new IllegalArgumentException("Sender ID and type are required");
        }

        // Find or create chat room
        ChatRoom chatRoom;
        if (messageDTO.getChatRoomId() != null) {
            chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("Chat room not found"));
        } else {
            // Create new chat room
            chatRoom = ChatRoom.builder()
                    .userId(messageDTO.getSenderType() == Message.SenderType.USER ?
                            messageDTO.getSenderId() : findUserIdFromMessage(messageDTO))
                    .sellerId(messageDTO.getSenderType() == Message.SenderType.SELLER ?
                            messageDTO.getSenderId() : findSellerIdFromMessage(messageDTO))
                    .lastMessageTime(LocalDateTime.now())
                    .build();
            chatRoom = chatRoomRepository.save(chatRoom);
        }

        // Create and save message
        Message message = Message.builder()
                .content(messageDTO.getContent())
                .messageType(messageDTO.getMessageType() != null ?
                        messageDTO.getMessageType() : Message.MessageType.TEXT)
                .senderId(messageDTO.getSenderId())
                .senderType(messageDTO.getSenderType())
                .chatRoom(chatRoom)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();

        log.info("Created message entity: {}", message);
        message = messageRepository.save(message);
        log.info("Saved message: {}", message);

        // Update chat room with last message info
        chatRoom.setLastMessageTime(message.getTimestamp());
        chatRoom.setLastMessagePreview(message.getContent().length() > 50 
                ? message.getContent().substring(0, 47) + "..." 
                : message.getContent());
                
        // Set unread flags
        if (message.getSenderType() == Message.SenderType.USER) {
            chatRoom.setHasUnreadMessagesForSeller(true);
        } else {
            chatRoom.setHasUnreadMessagesForUser(true);
        }
        
        chatRoomRepository.save(chatRoom);
        
        // Convert to DTO for response
        ChatMessageDTO responseDTO = convertToMessageDTO(message);
        
        // Send message to appropriate WebSocket destinations
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoom.getId(), responseDTO);
        
        // Also send notifications to user and seller private queues
        messagingTemplate.convertAndSend("/queue/user/" + chatRoom.getUserId(), responseDTO);
        messagingTemplate.convertAndSend("/queue/seller/" + chatRoom.getSellerId(), responseDTO);
        
        return responseDTO;
    }
    
    private Long findSellerIdFromMessage(ChatMessageDTO messageDTO) {
        return messageDTO.getChatRoomId() != null
                ? chatRoomRepository.findById(messageDTO.getChatRoomId())
                    .map(ChatRoom::getSellerId)
                    .orElseThrow(() -> new RuntimeException("Cannot determine seller"))
                : null;
    }
    
    private Long findUserIdFromMessage(ChatMessageDTO messageDTO) {
        // Similar to above, but for finding the user ID
        return messageDTO.getChatRoomId() != null 
                ? chatRoomRepository.findById(messageDTO.getChatRoomId())
                    .map(ChatRoom::getUserId)
                    .orElseThrow(() -> new RuntimeException("Cannot determine user"))
                : null;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserId(userId);
        return chatRooms.stream()
                .map(this::convertToChatRoomDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getSellerChatRooms(Long sellerId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySellerId(sellerId);
        return chatRooms.stream()
                .map(this::convertToChatRoomDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomDTO getChatRoom(Long chatRoomId, boolean isUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        ChatRoomDTO dto = convertToChatRoomDTO(chatRoom);
        
        // Load messages
        List<Message> messages = messageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
        dto.setMessages(messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList()));
                
        return dto;
    }
    
    @Transactional
    public void markMessagesAsRead(Long chatRoomId, Message.SenderType readerType) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
                
        // Find unread messages sent by the other party
        Message.SenderType otherType = readerType == Message.SenderType.USER 
                ? Message.SenderType.SELLER 
                : Message.SenderType.USER;
                
        List<Message> unreadMessages = messageRepository.findByChatRoomIdAndSenderTypeAndReadFalse(
                chatRoomId, otherType);
                
        // Mark messages as read
        unreadMessages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unreadMessages);
        
        // Update chat room unread flags
        if (readerType == Message.SenderType.USER) {
            chatRoom.setHasUnreadMessagesForUser(false);
        } else {
            chatRoom.setHasUnreadMessagesForSeller(false);
        }
        
        chatRoomRepository.save(chatRoom);
        
        // Notify about read status
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId + "/read", 
                Map.of("reader", readerType, "timestamp", LocalDateTime.now()));
    }

    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        User user = userRepository.findById(chatRoom.getUserId()).orElse(null);
        Seller seller = sellerRepository.findById(chatRoom.getSellerId()).orElse(null);
        
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .userId(chatRoom.getUserId())
                .sellerId(chatRoom.getSellerId())
                .userFullName(user != null ? user.getFullName() : "Unknown User")
                .sellerName(seller != null ? seller.getSellerName() : "Unknown Seller")
                .sellerBusinessName(seller != null && seller.getBusinessDetails() != null ? 
                        seller.getBusinessDetails().getBusinessName() : null)
                .lastMessageTime(chatRoom.getLastMessageTime())
                .lastMessagePreview(chatRoom.getLastMessagePreview())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(Long chatRoomId) {
        // Validate chat room exists
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new RuntimeException("Chat room not found");
        }

        // Get messages ordered by timestamp
        List<Message> messages = messageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);

        // Convert to DTOs
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        messageRepository.deleteByChatRoomId(chatRoomId);

        chatRoomRepository.delete(chatRoom);

        Map<String, Object> deleteNotification = new HashMap<>();
        deleteNotification.put("chatRoomId", chatRoomId);
        deleteNotification.put("action", "deleted");
        deleteNotification.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/queue/user/" + chatRoom.getUserId(), deleteNotification);
        messagingTemplate.convertAndSend("/queue/seller/" + chatRoom.getSellerId(), deleteNotification);
    }

    @Transactional
    public ChatRoomDTO getOrCreateChatRoom(Long userId, Long sellerId) {
        ChatRoom existingRoom = chatRoomRepository.findByUserIdAndSellerId(userId, sellerId).orElse(null);

        if (existingRoom != null) {
            return convertToChatRoomDTO(existingRoom);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .userId(userId)
                .sellerId(sellerId)
                .lastMessageTime(LocalDateTime.now())
                .hasUnreadMessagesForSeller(false)
                .hasUnreadMessagesForUser(false)
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);
        return convertToChatRoomDTO(chatRoom);
    }

    private ChatMessageDTO convertToMessageDTO(Message message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .chatRoomId(message.getChatRoom().getId())
                .timestamp(message.getTimestamp())
                .read(message.isRead())
                .build();
    }
}