package ru.practicum.ewm.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

@UtilityClass
public class RequestMapper {

    public Request toRequest(ParticipationRequestDto participationRequestDto) {

        return Request.builder()
                .id(participationRequestDto.getId())
                .created(participationRequestDto.getCreated())
                .event(null)
                .requester(null)
                .status(participationRequestDto.getStatus())
                .build();
    }

    public ParticipationRequestDto toParticipationRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();

    }

}
