package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.lookupparam.AdminGetEventsParams;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@Valid AdminGetEventsParams adminGetEventsParams) {
        log.info("Invoked AdminEventController.getEventsByAdmin method");
        return eventService.getEventsByAdmin(adminGetEventsParams);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Invoked AdminEventController.updateEventByAdmin method");
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}
