package com.foodshop.repo;
import com.foodshop.model.FestivalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FestivalEventRepository extends JpaRepository<FestivalEvent, Long> {
    List<FestivalEvent> findAllByOrderByEventDateAsc();
}
