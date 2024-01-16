package ru.practicum.ewm;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewStats {

    private String app;

    private String uri;

    private int hits;

}
