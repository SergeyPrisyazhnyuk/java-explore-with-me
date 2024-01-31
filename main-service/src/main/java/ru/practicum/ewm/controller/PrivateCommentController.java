package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentDtoText;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllCommentsByUser(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Invoked PrivateCommentController.getAllCommentsByUser method with userId = {} and eventId = {}", userId, eventId);
        return commentService.getAllCommentsByUser(userId, eventId, from, size);
    }

    @PostMapping
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody CommentDtoText commentDtoText) {
        log.info("Invoked AdminCommentController.addComment method with eventId = {} and userId = {}", eventId, userId);
        return commentService.addComment(userId, eventId, commentDtoText);
    }

    @PatchMapping("/{comId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long comId,
                                    @RequestBody CommentDtoText commentDtoText) {
        log.info("Invoked AdminCommentController.updateComment method with eventId = {} and userId = {} and comId = {}", eventId, userId, comId);
        return commentService.updateComment(userId, eventId, comId, commentDtoText);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long comId) {
        log.info("Invoked AdminCommentController.deleteCommentByUser method with eventId = {} and userId = {} and comId = {}", eventId, userId, comId);
        commentService.deleteCommentByUser(userId, eventId, comId);
    }

}
