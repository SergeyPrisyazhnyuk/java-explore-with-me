package ru.practicum.ewm.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;

    private Set<EventShortDto> events;

    private boolean pinned;

    private String title;

}
