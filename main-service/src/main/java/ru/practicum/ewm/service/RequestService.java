package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;
import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

}
