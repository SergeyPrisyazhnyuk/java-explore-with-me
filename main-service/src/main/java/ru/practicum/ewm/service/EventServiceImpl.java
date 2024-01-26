package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.lookupparam.AdminGetEventsParams;
import ru.practicum.ewm.dto.lookupparam.PublicGetEventsParams;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.dto.mapper.LocationMapper;
import ru.practicum.ewm.dto.mapper.RequestMapper;
import ru.practicum.ewm.exception.CommonException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParameterException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.AdminEventState;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.model.enums.UserEventState;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.utility.CheckUtil;
import ru.practicum.ewm.utility.StatClientUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    @Autowired
    private final CheckUtil checkUtil;
    private final StatClientUtil statClientUtil;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getEvents(PublicGetEventsParams publicGetEventsParams, HttpServletRequest httpServletRequest) {

        statClientUtil.saveStatHit(httpServletRequest);

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
        LocalDateTime rangeEnd = LocalDateTime.parse(publicGetEventsParams.getRangeEnd(), formatter);
        LocalDateTime rangeStart = LocalDateTime.parse(publicGetEventsParams.getRangeStart(), formatter);

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ParameterException("StartDate is after EndDate");
            }
        }

        specification = specification.and((root, query, cb) ->
                cb.greaterThan(root.get("eventDate"), Objects.requireNonNullElse(rangeStart, LocalDateTime.now())));

        if (publicGetEventsParams.getRangeEnd() != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThan(root.get("eventDate"), publicGetEventsParams.getRangeEnd()));
        }

        // event status
        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("state"), EventState.PUBLISHED));


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

        Map<Long, Integer> statViews = statClientUtil.getStatViewAll(eventList);

        for (EventShortDto event : eventShortDtoList) {
//            Long views = event.getViews();
              Long views = Long.valueOf(statViews.getOrDefault(event.getId(),0));
              event.setViews(views);
        }

        return eventShortDtoList;
    }


    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {

        statClientUtil.saveStatHit(httpServletRequest);

        Event event = checkUtil.checkEventId(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Not found event with id = " + eventId);
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        Map<Long, Integer> statViews = statClientUtil.getStatViewAll(List.of(event));

        Long views = Long.valueOf(statViews.getOrDefault(event.getId(),0));
        event.setViews(views);

        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsByUserId(Long userId, Integer from, Integer size) {

        checkUtil.checkUserId(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        Specification<Event> specification = Specification.where(null);

        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("initiator").get("id"), userId));

        List<Event> eventList = eventRepository.findAll(specification, pageRequest).getContent();

        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {

        User user = checkUtil.checkUserId(userId);
        Category category = checkUtil.checkCatId(newEventDto.getCategory());

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ParameterException("EventDate should be after current date + 2 hours");
        }

        Event event = EventMapper.toEvent(newEventDto);

        event.setCategory(category);
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);

        if (newEventDto.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
            event.setLocation(location);
        }

        event.setState(EventState.PENDING);
        event.setViews(0L);

        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);

    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {

        checkUtil.checkUserId(userId);
        Event event = checkUtil.checkEventInitiator(eventId, userId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        checkUtil.checkUserId(userId);
        Event event = checkUtil.checkEventInitiator(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new CommonException("Event is already PUBLISHED");
        }

        boolean changeable = false;

        String annotationNew = updateEventUserRequest.getAnnotation();
        if (annotationNew != null && !annotationNew.isBlank()) {
            event.setAnnotation(annotationNew);
            changeable = true;
        }

        Long categoryNew = updateEventUserRequest.getCategory();
        if (categoryNew != null) {
            Category category = checkUtil.checkCatId(categoryNew);
            event.setCategory(category);
            changeable = true;
        }

        String descriptionNew = updateEventUserRequest.getDescription();
        if (descriptionNew != null && !descriptionNew.isBlank()) {

            event.setDescription(descriptionNew);
            changeable = true;
        }

        LocalDateTime eventDateNew = updateEventUserRequest.getEventDate();
        if (eventDateNew != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ParameterException("EventDate should be after current date + 2 hours");
            }
            event.setEventDate(eventDateNew);
            changeable = true;
        }

        if (updateEventUserRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEventUserRequest.getLocation());
            event.setLocation(location);
            changeable = true;
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
            changeable = true;
        }

        Integer participantLimitNew = updateEventUserRequest.getParticipantLimit();
        if (participantLimitNew != null) {
            event.setParticipantLimit(participantLimitNew);
            changeable = true;
        }

        Boolean requestModerationNew = updateEventUserRequest.getRequestModeration();
        if (requestModerationNew != null) {
            event.setRequestModeration(requestModerationNew);
            changeable = true;
        }

        UserEventState userEventStateNew = updateEventUserRequest.getStateAction();
        if (userEventStateNew != null) {

            if (userEventStateNew.equals(UserEventState.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
            changeable = true;
        }

        String titleNew = updateEventUserRequest.getTitle();
        if (titleNew != null && !titleNew.isBlank()) {
            event.setTitle(titleNew);
            changeable = true;
        }

        if (changeable) {
            eventRepository.save(event);
        }

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipationRequests(Long userId, Long eventId) {

        checkUtil.checkUserId(userId);
        checkUtil.checkEventInitiator(eventId, userId);

        List<Request> requestList = requestRepository.findAllByEventId(eventId);

        return requestList.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        User user = checkUtil.checkUserId(userId);
        Event event = checkUtil.checkEventInitiator(eventId, userId);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new CommonException("This event doesn't need approve");
        }

        Integer confirmedRequestsCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Integer participantLimit = event.getParticipantLimit();

        if (participantLimit.equals(confirmedRequestsCount)) {
            throw new CommonException("Limit of participants is reached");
        }

        List<Request> requestList = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());


        for (Request request : requestList) {
            if (participantLimit == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                if (event.getConfirmedRequests() != 0) {
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    event.setConfirmedRequests(1);
                }
            } else {
                if (request.getEvent().getId().equals(event.getId())) {
                    if (event.getInitiator().equals(user)) {
                        if (request.getStatus().equals(RequestStatus.PENDING)) {
                            request.setStatus(eventRequestStatusUpdateRequest.getStatus());
                            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                                if (event.getConfirmedRequests() != 0) {
                                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                                } else {
                                    event.setConfirmedRequests(1);
                                }
                            }
                        } else {
                            throw new CommonException("Can't update request if it is not pending");
                        }
                    }
                }
            }
            requestRepository.save(request);
        }

        List<ParticipationRequestDto> requestDtoList = requestList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (ParticipationRequestDto dto : requestDtoList) {
            if (dto.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(dto);
            }
            if (dto.getStatus().equals(RequestStatus.REJECTED)) {
                rejectedRequests.add(dto);
            }
        }

        EventRequestStatusUpdateResult statusUpdateResult = new EventRequestStatusUpdateResult();
        statusUpdateResult.setConfirmedRequests(confirmedRequests);
        statusUpdateResult.setRejectedRequests(rejectedRequests);
        eventRepository.save(event);
        return statusUpdateResult;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(AdminGetEventsParams adminGetEventsParams) {

        log.info("Begin reading AdminGetEventsParams - " + adminGetEventsParams.toString());

        PageRequest pageable = PageRequest.of(adminGetEventsParams.getFrom() / adminGetEventsParams.getSize(),
                adminGetEventsParams.getSize());
        Specification<Event> specification = Specification.where(null);



        List<Long> userList = adminGetEventsParams.getUsers();
        List<String> stateList = adminGetEventsParams.getStates();
        List<Long> categoryList = adminGetEventsParams.getCategories();



        LocalDateTime rangeEnd = LocalDateTime.parse(adminGetEventsParams.getRangeEnd(), formatter);
        LocalDateTime rangeStart = LocalDateTime.parse(adminGetEventsParams.getRangeStart(), formatter);

        if (userList != null && !userList.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(userList));
        }
        if (stateList != null && !stateList.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(stateList));
        }
        if (categoryList != null && !categoryList.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categoryList));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        Page<Event> events = eventRepository.findAll(specification, pageable);

        List<EventFullDto> eventFullDtoList = events.getContent()
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        List<Request> requestList = requestRepository.findAllByEventIdInAndStatus(events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList()), RequestStatus.CONFIRMED);

        Map<Long, List<Request>> confirmedRequestsCountMap = requestList.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId()));


        for (EventFullDto event : eventFullDtoList) {
            List<Request> requests = confirmedRequestsCountMap.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequests(requests.size());
        }
        return eventFullDtoList;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        Event event = checkUtil.checkEventId(eventId);

        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime eventDate = updateEventAdminRequest.getEventDate();
            if (event.getPublishedOn() != null) {
                if (event.getPublishedOn().isAfter(eventDate.plusHours(1))) {
                    event.setEventDate(eventDate);
                } else {
                    throw new CommonException("EventDate should be not earlier of 1 hours from publishedOn");
                }
            } else {
                event.setEventDate(eventDate);
            }

        }

        boolean changeable = false;

        String annotationNew = updateEventAdminRequest.getAnnotation();
        if (annotationNew != null && !annotationNew.isBlank()) {
            event.setAnnotation(annotationNew);
            changeable = true;
        }

        Long categoryNew = updateEventAdminRequest.getCategory();
        if (categoryNew != null) {
            Category category = checkUtil.checkCatId(categoryNew);
            event.setCategory(category);
            changeable = true;
        }

        String descriptionNew = updateEventAdminRequest.getDescription();
        if (descriptionNew != null && !descriptionNew.isBlank()) {

            event.setDescription(descriptionNew);
            changeable = true;
        }

        LocalDateTime eventDateNew = updateEventAdminRequest.getEventDate();
        if (eventDateNew != null) {
            event.setEventDate(eventDateNew);
            changeable = true;
        }

        if (updateEventAdminRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEventAdminRequest.getLocation());
            event.setLocation(location);
            changeable = true;
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
            changeable = true;
        }

        Integer participantLimitNew = updateEventAdminRequest.getParticipantLimit();
        if (participantLimitNew != null) {
            event.setParticipantLimit(participantLimitNew);
            changeable = true;
        }

        Boolean requestModerationNew = updateEventAdminRequest.getRequestModeration();
        if (requestModerationNew != null) {
            event.setRequestModeration(requestModerationNew);
            changeable = true;
        }


        AdminEventState stateAction = AdminEventState.valueOf(updateEventAdminRequest.getStateAction());

        if (stateAction != null) {

            if (stateAction.equals(AdminEventState.PUBLISH_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new CommonException("Event is already published");
                } else if (event.getState().equals(EventState.CANCELED)) {
                    throw new CommonException("Event is already canceled");
                } else if (event.getState().equals(EventState.PENDING)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    changeable = true;
                } else {
                    throw new CommonException("Got Unrnown status");
                }
            }

            if (stateAction.equals(AdminEventState.REJECT_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new CommonException("Event is already published");
                } else if (event.getState().equals(EventState.CANCELED)) {
                    throw new CommonException("Event is already canceled");
                } else if (event.getState().equals(EventState.PENDING)) {
                    event.setState(EventState.CANCELED);
                    changeable = true;
                } else {
                    throw new CommonException("Got Unrnown status");
                }
            }


        }

        String titleNew = updateEventAdminRequest.getTitle();
        if (titleNew != null && !titleNew.isBlank()) {
            event.setTitle(titleNew);
            changeable = true;
        }

        if (changeable) {
            eventRepository.save(event);
        }

        EventFullDto eventFullDtoResult = EventMapper.toEventFullDto(event);

        log.info("Returning EventFullDtoResult = " + eventFullDtoResult);

        return eventFullDtoResult;
    }
}
