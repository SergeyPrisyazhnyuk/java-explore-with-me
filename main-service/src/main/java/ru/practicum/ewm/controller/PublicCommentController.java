package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public List<CommentDtoText> getAllComments(@PathVariable Long eventId,
                                               @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Invoked PublicCommentController.getAllCommentsByUser method with eventId = {}", eventId);
        return commentService.getAllComments(eventId, from, size);
    }

}
