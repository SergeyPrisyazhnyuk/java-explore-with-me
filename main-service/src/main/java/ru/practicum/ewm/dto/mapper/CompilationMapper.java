package ru.practicum.ewm.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.model.Compilation;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(CompilationDto compilationDto) {

        return Compilation.builder()
                .pinned(compilationDto.isPinned())
                .title(compilationDto.getTitle())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toSet()))
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

}
