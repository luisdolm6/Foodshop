package com.foodshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_notifications")
public class EventNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne @JoinColumn(name = "event_id")
    private FestivalEvent event;
    @Column(nullable = false, length = 40) private String type;
    @Column(nullable = false, length = 1000) private String message;
    private LocalDateTime createdAt;
    @Column(nullable = false) private boolean read = false;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public FestivalEvent getEvent() { return event; }
    public void setEvent(FestivalEvent event) { this.event = event; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
