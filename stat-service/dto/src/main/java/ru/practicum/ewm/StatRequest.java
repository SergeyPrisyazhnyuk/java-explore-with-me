package ru.practicum.ewm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class StatRequest {

    @Builder.Default
    private LocalDateTime start = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime end = LocalDateTime.now();

    private List<String> uris;

    private boolean unique;

    private String app;
}
