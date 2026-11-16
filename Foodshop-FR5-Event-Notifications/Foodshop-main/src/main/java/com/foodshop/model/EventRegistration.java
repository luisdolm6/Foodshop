package com.foodshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
public class EventRegistration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "event_id")
    private FestivalEvent event;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime registeredAt;

    public Long getId() { return id; }
    public FestivalEvent getEvent() { return event; }
    public void setEvent(FestivalEvent event) { this.event = event; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
