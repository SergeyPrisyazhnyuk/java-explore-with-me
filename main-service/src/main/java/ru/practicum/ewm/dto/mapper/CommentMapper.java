package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentDtoText;
import ru.practicum.ewm.model.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .userId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .build();
    }

    public static CommentDtoText toCommentDtoText(Comment comment) {
        return CommentDtoText.builder()
                .text(comment.getText())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }
}
