package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {

    @NotNull
    @Max(90)
    @Min(-90)
    private float lat;

    @NotNull
    @Max(90)
    @Min(-90)
    private float lon;

}
