package ru.practicum.ewm.service;


import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatRequest;
import ru.practicum.ewm.ViewStats;

import java.util.List;

public interface StatService {

    void saveHit(EndpointHit endpointHit);

    List<ViewStats> getViewStats(StatRequest statRequest);

}
