package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.mapper.RequestMapper;
import ru.practicum.ewm.exception.CommonException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParameterException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService{

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private Event checkEventId(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event with id = " + eventId));
    }

    private User checkUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id = " + userId));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {

        checkUserId(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);

        return requestList.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {

        User user = checkUserId(userId);
        Event event = checkEventId(eventId);


        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new CommonException("Request already exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new CommonException("You can't request to participate in your own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new CommonException("Event with id=" + eventId + " was not found");
        }

        int participants = event.getParticipantLimit();
        int confirmedRequest = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (participants == confirmedRequest) {
            throw new CommonException("Participant limit is reached");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        requestRepository.save(request);

        return null;
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        checkUserId(userId);

        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("Request with id = " + requestId + "not found"));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new ParameterException("Request is already Cancelled or Rejected");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}