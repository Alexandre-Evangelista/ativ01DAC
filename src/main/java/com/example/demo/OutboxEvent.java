
package com.example.demo;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String userName;

    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    private LocalDateTime createdAt;

    public OutboxEvent() {
    }

    public OutboxEvent(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.status = OutboxEventStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public OutboxEventStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxEventStatus status) {
        this.status = status;
    }
}
