package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.dto.mapper.CompilationMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParameterException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.utility.CheckUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService{

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CheckUtil checkUtil;

    private Compilation checkCompId(Long compId) {

        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Not found compilation with id = " + compId));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from, size);

        List<Compilation> compilations = new ArrayList<>();

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {

        return CompilationMapper.toCompilationDto(checkCompId(compId));
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setPinned(Optional.of(compilation.isPinned()).orElse(false));

        Set<Long> compEvIds = (newCompilationDto.getEvents() != null) ? newCompilationDto.getEvents() : Collections.emptySet();
        List<Long> eventIds = new ArrayList<>(compEvIds);
        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        Set<Event> eventSet = new HashSet<>(events);
        compilation.setEvents(eventSet);

        Compilation compilationToAdd = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilationToAdd);    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {

        checkCompId(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {

        Compilation compilation = checkCompId(compId);

        Set<Long> eventIds = updateCompilationRequest.getEvents();

        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
            Set<Event> eventSet = new HashSet<>(events);
            compilation.setEvents(eventSet);
        }

        compilation.setPinned(Optional.of(updateCompilationRequest.isPinned()).orElse(compilation.isPinned()));
        if (compilation.getTitle().isBlank()) {
            throw new ParameterException("Title is blank");
        }

        compilation.setTitle(Optional.ofNullable(updateCompilationRequest.getTitle()).orElse(compilation.getTitle()));

        return CompilationMapper.toCompilationDto(compilation);

    }
}
