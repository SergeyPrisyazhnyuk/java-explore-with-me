package ru.practicum.ewm.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;

@Component
@RequiredArgsConstructor
public class CheckUtil {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    public Category checkCatId(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Not found category with id = " + catId));
    }

    public Event checkEventId(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event with id = " + eventId));
    }

    public User checkUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id = " + userId));
    }

    public void userExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found user with id = " + userId);
        }
    }

    public void checkUniqueNameUser(String userName) {
        if (userRepository.findByName(userName) != null) {
            throw new ConflictException("User already exists, name: " + userName);
        }
    }

    public Event checkEventInitiator(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Not found event with id = " + eventId + " and user = " + userId));
    }


    public Compilation checkCompId(Long compId) {

        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Not found compilation with id = " + compId));
    }

    public void checkUniqNameCategory(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Category already exists: " + name);
        }
    }

    public Comment checkCommentId(Long comId) {
        return commentRepository.findById(comId).orElseThrow(() -> new NotFoundException("Not found commentary with id = " + comId));
    }

    public Request checkRequestEventAndRequester(Long eventId, Long userId) {
        return requestRepository.findByEventIdAndRequesterId(eventId, userId).orElseThrow(() -> new NotFoundException("Not found request with eventId = " + eventId + " and requestId = " + userId));
    }

}
