package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.lookupparam.PublicGetEventsParams;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParameterException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.repository.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final StatClient statClient;


    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    private Event checkEventId(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event with id = " + eventId));
    }

    private User checkUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id = " + userId));
    }

    
    @Override
    public List<EventShortDto> getEvents(PublicGetEventsParams publicGetEventsParams, HttpServletRequest httpServletRequest) {

        Specification<Event> specification = Specification.where(null);

        // Text
        if (publicGetEventsParams.getText() != null) {
            String text = publicGetEventsParams.getText().toLowerCase();
            specification = specification.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("annotation")), "%" + text + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + text + "%")
                    ));
        }

        // Categories
        if (publicGetEventsParams.getCategories() != null && !publicGetEventsParams.getCategories().isEmpty()) {
            specification = specification.and((root, query, cb) ->
                    root.get("category").get("id").in(publicGetEventsParams.getCategories()));
        }

        // Paid
        if (publicGetEventsParams.getPaid() != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("paid"), publicGetEventsParams.getPaid()));
        }

        // RangeStart RangeEnd
        if (publicGetEventsParams.getRangeEnd() != null && publicGetEventsParams.getRangeStart() != null) {
            if (publicGetEventsParams.getRangeEnd().isBefore(publicGetEventsParams.getRangeStart())) {
                throw new ParameterException("StartDate is after EndDate");
            }
        }

        specification = specification.and((root, query, cb) ->
                cb.greaterThan(root.get("eventDate"), Objects.requireNonNullElse(publicGetEventsParams.getRangeStart(), LocalDateTime.now())));

        if (publicGetEventsParams.getRangeEnd() != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThan(root.get("eventDate"), publicGetEventsParams.getRangeEnd()));
        }

        // event status
        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("eventStatus"), EventState.PUBLISHED));


        String sort = (publicGetEventsParams.getSort() != null && publicGetEventsParams.getSort().equalsIgnoreCase("EVENT_DATE"))
                ? "eventDate" : "views";

        Pageable pageable = PageRequest.of(publicGetEventsParams.getFrom() / publicGetEventsParams.getSize(), publicGetEventsParams.getSize(), Sort.by(sort).descending());

        List<Event> eventList = eventRepository.findAll(specification, pageable).getContent();

        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        if (publicGetEventsParams.getOnlyAvailable() == null || publicGetEventsParams.getOnlyAvailable().equals(false)) {
            eventShortDtoList = eventList
                    .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        } else if (publicGetEventsParams.getOnlyAvailable().equals(true)) {
            for (Event event : eventList) {
                if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() < event.getParticipantLimit()) {
                    eventShortDtoList.add(EventMapper.toEventShortDto(event));
                }
            }
        }

        for (EventShortDto event : eventShortDtoList) {
            Long views = event.getViews();
            if (views >= 0) {
                event.setViews(views + 1L);
            } else {
                event.setViews(1L);
            }
        }

        statClient.saveHit(httpServletRequest);

        return eventShortDtoList;
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {

        Event event = checkEventId(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Not found event with id = " + eventId);
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(event.getViews() + 1L);

        statClient.saveHit(httpServletRequest);

        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsByUserId(Long userId, Integer from, Integer size) {

        checkUserId(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        Specification<Event> specification = Specification.where(null);

        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("initiator").get("id"), userId));

        List<Event> eventList = eventRepository.findAll(specification, pageRequest).getContent();

        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipationRequests(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return null;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin() {
        return null;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        return null;
    }
}
