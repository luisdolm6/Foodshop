package com.foodshop.web;

import com.foodshop.model.*;
import com.foodshop.repo.*;
import com.foodshop.service.EventNotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
public class EventController {
    private final FestivalEventRepository events;
    private final EventRegistrationRepository registrations;
    private final EventNotificationRepository notifications;
    private final EventNotificationService notificationService;

    public EventController(FestivalEventRepository events,
                           EventRegistrationRepository registrations,
                           EventNotificationRepository notifications,
                           EventNotificationService notificationService) {
        this.events = events;
        this.registrations = registrations;
        this.notifications = notifications;
        this.notificationService = notificationService;
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", events.findAllByOrderByEventDateAsc());
        return "events";
    }

    @PostMapping("/events/{id}/register")
    public String register(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        FestivalEvent event = events.findById(id).orElse(null);
        if (event == null || event.isCancelled()) return "redirect:/events";
        if (!registrations.existsByEventIdAndUserId(id, user.getId())) {
            EventRegistration registration = new EventRegistration();
            registration.setEvent(event);
            registration.setUser(user);
            registration.setRegisteredAt(LocalDateTime.now());
            registrations.save(registration);
            notificationService.send(user, event, "CONFIRMATION",
                    "Your registration for '" + event.getTitle() + "' is confirmed.");
        }
        return "redirect:/notifications";
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("notifications",
                notifications.findByUserIdOrderByCreatedAtDesc(user.getId()));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        notifications.findById(id).filter(n -> n.getUser().getId().equals(user.getId()))
                .ifPresent(n -> { n.setRead(true); notifications.save(n); });
        return "redirect:/notifications";
    }

    // Organizer endpoints for creating, updating, and cancelling events.
    // Add role-based authorization before exposing these endpoints publicly.
    @PostMapping("/organizer/events")
    @ResponseBody
    public FestivalEvent createEvent(@RequestBody FestivalEvent event) {
        event.setCancelled(false);
        return events.save(event);
    }

    @PutMapping("/organizer/events/{id}")
    @ResponseBody
    public FestivalEvent updateEvent(@PathVariable Long id, @RequestBody FestivalEvent changes) {
        FestivalEvent event = events.findById(id).orElseThrow();
        event.setTitle(changes.getTitle());
        event.setDescription(changes.getDescription());
        event.setEventDate(changes.getEventDate());
        FestivalEvent saved = events.save(event);
        notificationService.notifyRegisteredStudents(saved, "UPDATE",
                "Event update: '" + saved.getTitle() + "' has new details. Please check the event page.");
        return saved;
    }

    @DeleteMapping("/organizer/events/{id}")
    @ResponseBody
    public void cancelEvent(@PathVariable Long id) {
        FestivalEvent event = events.findById(id).orElseThrow();
        event.setCancelled(true);
        events.save(event);
        notificationService.notifyRegisteredStudents(event, "CANCELLATION",
                "Event cancellation: '" + event.getTitle() + "' has been cancelled.");
    }
}
