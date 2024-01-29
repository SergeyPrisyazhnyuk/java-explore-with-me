package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentDtoText;
import ru.practicum.ewm.dto.mapper.CommentMapper;
import ru.practicum.ewm.exception.CommonException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.utility.CheckUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CheckUtil checkUtil;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;


    @Override
    public List<CommentDtoText> getAllComments(Long eventId) {

        checkUtil.checkEventId(eventId);

        List<Comment> commentList = commentRepository.findAllByEventId(eventId);

        return commentList.stream().map(CommentMapper::toCommentDtoText).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId, Long eventId) {

        checkUtil.userExists(userId);
        checkUtil.checkEventId(eventId);

        List<Comment> commentList = commentRepository.findAllByEventId(eventId);

        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, CommentDtoText commentDtoText) {

        User user = checkUtil.checkUserId(userId);
        Event event = checkUtil.checkEventInitiator(userId, eventId);

        Comment comment = Comment.builder()
                .text(commentDtoText.getText())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long comId, CommentDtoText commentDtoText) {

        checkUtil.checkUserId(userId);
        checkUtil.checkEventInitiator(userId, eventId);
        Comment comment = checkUtil.checkCommentId(comId);

        if (comment.getText().equals(commentDtoText.getText())) {
            throw new CommonException("Nothing to update. All is up do date.");
        }

        comment.setText(commentDtoText.getText());
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long eventId, Long comId) {

        checkUtil.checkUserId(userId);
        checkUtil.checkEventInitiator(userId, eventId);

        commentRepository.deleteById(comId);

    }

    @Override
    public List<CommentDto> getAllCommentsByAdmin(Long eventId) {

        checkUtil.checkEventId(eventId);

        List<Comment> commentList = commentRepository.findAllByEventId(eventId);

        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long eventId, Long comId) {

        checkUtil.checkEventId(eventId);
        checkUtil.checkCommentId(comId);

        commentRepository.deleteById(comId);
    }
}
