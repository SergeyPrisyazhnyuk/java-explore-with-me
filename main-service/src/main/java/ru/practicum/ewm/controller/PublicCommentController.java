package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.CommentDtoText;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/comments/{eventId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDtoText> getAllComments(@PathVariable Long eventId) {
        log.info("Invoked PublicCommentController.getAllCommentsByUser method with eventId = {}", eventId);
        return commentService.getAllComments(eventId);
    }

}
