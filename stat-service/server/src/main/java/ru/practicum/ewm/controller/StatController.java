package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatRequest;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.service.StatService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody EndpointHit endpointHit) {
        log.info("Invoke saveHit method with app = {} and uri = {}", endpointHit.getApp(), endpointHit.getUri());
        statService.saveHit(endpointHit);
    }


    @GetMapping("/stats")
    public List<ViewStats> getViewStats(
                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime start,
                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime end,
                @RequestParam(required = false) List<String> uris,
                @RequestParam(defaultValue = "false") boolean unique) {
                log.info("Invoke getViewStats method");
                if (uris == null) {
                    uris = Collections.emptyList();
                }

                List<ViewStats> viewStats = statService.getViewStats(
                        StatRequest.builder()
                                .start(start)
                                .end(end)
                                .uris(uris)
                                .unique(unique)
                                .build()
                );

        return viewStats;
    }



}
