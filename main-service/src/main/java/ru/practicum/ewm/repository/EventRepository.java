package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByIdIn(List<Long> eventIds);

    List<Event> findByCategory(Category category);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

}
