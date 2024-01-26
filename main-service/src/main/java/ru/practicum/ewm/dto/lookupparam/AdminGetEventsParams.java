package ru.practicum.ewm.dto.lookupparam;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminGetEventsParams {

    private List<Long> users;

    private List<String> states;

    private List<Long> categories;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String rangeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String rangeEnd;

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;
}
