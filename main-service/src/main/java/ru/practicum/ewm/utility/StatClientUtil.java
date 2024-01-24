package ru.practicum.ewm.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StatClientUtil {


    private final StatClient statClient;
    private final ObjectMapper objectMapper;

    @Value("${server.application.name:ewm-service}")
    private String appName;

    public void saveStatHit(HttpServletRequest httpServletRequest) {
        statClient.saveHit(EndpointHit.builder()
                .app(appName)
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    public Map<Long, Integer> getStatViewAll(List<Event> eventList) {
        Map<Long, Integer> view = new HashMap<>();

        LocalDateTime startDate = eventList.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (startDate == null) {
            return Map.of();
        }

        List<String> uris = eventList.stream()
                .map(s -> "/events/" + s.getId())
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statClient.getStats(startDate, LocalDateTime.now(), uris, true);

        List<ViewStats> viewStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<List<ViewStats>>() {});

        view = viewStatsList.stream()
                .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                .collect(Collectors.toMap(
                        statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                        ViewStats::getHits));

        return view;
    }

}
