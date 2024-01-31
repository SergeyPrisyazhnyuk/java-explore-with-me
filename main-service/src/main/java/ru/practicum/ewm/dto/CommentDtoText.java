package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoText {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    private String text;

}
