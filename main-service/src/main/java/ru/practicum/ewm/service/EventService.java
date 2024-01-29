package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.lookupparam.AdminGetEventsParams;
import ru.practicum.ewm.dto.lookupparam.PublicGetEventsParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEvents(PublicGetEventsParams publicGetEventsParams, HttpServletRequest httpServletRequest);

    EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest);

    List<EventFullDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getEventParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEventsByAdmin(AdminGetEventsParams adminGetEventsParams);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);


}
