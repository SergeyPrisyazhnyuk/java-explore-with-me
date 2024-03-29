package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.mapper.RequestMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParameterException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.utility.CheckUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final CheckUtil checkUtil;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {

        checkUtil.checkUserId(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);

        return requestList.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {

        User user = checkUtil.checkUserId(userId);
        Event event = checkUtil.checkEventId(eventId);


        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("You can't request to participate in your own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event with id=" + eventId + " was not found");
        }

        int participants = event.getParticipantLimit();
        int confirmedRequest = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (participants == confirmedRequest && participants != 0) {
            throw new ConflictException("Participant limit is reached");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (event.isRequestModeration() && participants != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        requestRepository.save(request);

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        checkUtil.checkUserId(userId);

        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("Request with id = " + requestId + "not found"));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new ParameterException("Request is already Cancelled or Rejected");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}