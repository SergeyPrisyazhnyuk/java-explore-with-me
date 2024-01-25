package ru.practicum.ewm.dto;

import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {

    @NotNull
    private float lat;

    @NotNull
    private float lon;

}
