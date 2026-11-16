package com.foodshop.repo;
import com.foodshop.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    List<EventRegistration> findByEventId(Long eventId);
}
