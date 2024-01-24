package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Invoked PrivateEventController.getEventsByUserId method with userId={}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Invoked PrivateEventController.addEvent method with userId={} and newEventDto = {}", userId, newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        log.info("Invoked PrivateEventController.getEventByUserIdAndEventId method with userId={} and eventId={}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUserIdAndEventId(@PathVariable Long userId,
                                                      @PathVariable Long eventId,
                                                      @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {

        log.info("Invoked PrivateEventController.updateEventByUserIdAndEventId method with userId={} and eventId={}", userId, eventId);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipationRequests(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        log.info("Invoked PrivateEventController.getEventParticipationRequests method with userId={} and eventId={}", userId, eventId);
        return eventService.getEventParticipationRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable Long userId,
                                                                   @PathVariable Long eventId,
                                                                   @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Invoked PrivateEventController.updateEventRequestStatus method with userId={} and eventId={}", userId, eventId);
        return eventService.updateEventRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }





}