package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatRequest;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.repository.StatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    public void saveHit(EndpointHit endpointHit) {
        statRepository.saveHit(endpointHit);
    }

    @Override
    public List<ViewStats> getViewStats(StatRequest statRequest) {

        if (statRequest.isUnique()) {
            return statRepository.getViewStatsUnique(statRequest);
        } else {
            return statRepository.getViewStatsAll(statRequest);
        }
    }
}
