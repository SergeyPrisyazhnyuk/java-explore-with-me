package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.lookupparam.PublicGetEventsParams;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(PublicGetEventsParams publicGetEventsParams, HttpServletRequest httpServletRequest) {
        log.info("Invoked PublicEventController.getEvents method");
        return eventService.getEvents(publicGetEventsParams, httpServletRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        log.info("Invoked PublicEventController.getEventById method with eventId={}", eventId);
        return eventService.getEventById(eventId, httpServletRequest);
    }

}
