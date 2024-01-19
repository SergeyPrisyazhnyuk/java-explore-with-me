package ru.practicum.ewm.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

}
