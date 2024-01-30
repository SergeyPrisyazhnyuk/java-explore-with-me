package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllCommentsByAdmin(@PathVariable Long eventId) {
        log.info("Invoked AdminCommentController.getAllCommentsByAdmin method with eventId = {}", eventId);
        return commentService.getAllCommentsByAdmin(eventId);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long comId) {
        log.info("Invoked AdminCommentController.deleteCommentByAdmin method with  comId = {}", comId);
        commentService.deleteCommentByAdmin(comId);
    }

}
