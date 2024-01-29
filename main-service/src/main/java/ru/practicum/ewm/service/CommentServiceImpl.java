package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentDtoText;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService{
    @Override
    public List<CommentDtoText> getAllComments(Long eventId) {
        return null;
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId, Long eventId) {
        return null;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, CommentDtoText commentDtoText) {
        return null;
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long comId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long eventId, Long comId) {

    }

    @Override
    public List<CommentDto> getAllCommentsByAdmin(Long userId, Long eventId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long userId, Long eventId, Long comId) {

    }
}
