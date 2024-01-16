package ru.practicum.ewm.repository;

import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatRequest;
import ru.practicum.ewm.ViewStats;

import java.util.List;

public interface StatRepository {

    void saveHit(EndpointHit endpointHit);

    List<ViewStats> getViewStatsAll(StatRequest statRequest);

    List<ViewStats> getViewStatsUnique(StatRequest statRequest);
}
