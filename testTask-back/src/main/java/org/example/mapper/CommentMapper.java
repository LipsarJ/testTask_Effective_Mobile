package org.example.mapper;

import org.example.dto.response.ResponseCommentDTO;
import org.example.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = BaseLocalDateTimeOffsetDateTimeMapper.class)
public interface CommentMapper {
    ResponseCommentDTO toResponseCommentDTO(Comment comment);
}
