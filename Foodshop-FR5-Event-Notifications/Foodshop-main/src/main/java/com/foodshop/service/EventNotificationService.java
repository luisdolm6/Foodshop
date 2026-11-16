package com.foodshop.service;

import com.foodshop.model.*;
import com.foodshop.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class EventNotificationService {
    private final EventNotificationRepository notifications;
    private final EventRegistrationRepository registrations;

    public EventNotificationService(EventNotificationRepository notifications,
                                    EventRegistrationRepository registrations) {
        this.notifications = notifications;
        this.registrations = registrations;
    }

    public void send(User user, FestivalEvent event, String type, String message) {
        EventNotification notification = new EventNotification();
        notification.setUser(user);
        notification.setEvent(event);
        notification.setType(type);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notifications.save(notification);
    }

    @Transactional
    public void notifyRegisteredStudents(FestivalEvent event, String type, String message) {
        for (EventRegistration registration : registrations.findByEventId(event.getId())) {
            send(registration.getUser(), event, type, message);
        }
    }
}
