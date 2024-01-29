package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentDtoText;

import java.util.List;

public interface CommentService {

    List<CommentDtoText> getAllComments(Long eventId);

    List<CommentDto> getAllCommentsByUser(Long userId, Long eventId);

    CommentDto addComment(Long userId, Long eventId, CommentDtoText commentDtoText);

    CommentDto updateComment(Long userId, Long eventId, Long comId);

    void deleteCommentByUser(Long userId, Long eventId, Long comId);

    List<CommentDto> getAllCommentsByAdmin(Long userId, Long eventId);

    void deleteCommentByAdmin(Long userId, Long eventId, Long comId);

}
