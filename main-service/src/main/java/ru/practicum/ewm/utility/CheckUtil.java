package ru.practicum.ewm.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.exception.AlreadyExistsException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CheckUtil {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CompilationRepository compilationRepository;

    public Category checkCatId(Long catId) {

        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Not found category with id = " + catId));
    }

    public Event checkEventId(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event with id = " + eventId));
    }

    public User checkUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id = " + userId));
    }


    public Event checkEventInitiator(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Not found event with id = " + eventId + " and user = " + userId));
    }


    public Compilation checkCompId(Long compId) {

        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Not found compilation with id = " + compId));
    }

    public void checkUniqNameCategory(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new AlreadyExistsException(("Category already exists: " + name));
        }
    }

}
