package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(Set<Long> reqIds);

    int countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus requestStatus);

    List<Request> findAllByRequesterId(Long requesterId);

}
