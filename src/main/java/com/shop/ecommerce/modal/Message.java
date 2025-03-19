package com.shop.ecommerce.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    private LocalDateTime timestamp;

    @Column(name = "is_read")
    private boolean read;

    public enum MessageType {
        TEXT, IMAGE, FILE
    }

    public enum SenderType {
        USER, SELLER
    }
}