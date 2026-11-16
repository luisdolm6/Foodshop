package com.foodshop.repo;
import com.foodshop.model.EventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EventNotificationRepository extends JpaRepository<EventNotification, Long> {
    List<EventNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadFalse(Long userId);
}
