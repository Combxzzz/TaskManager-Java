package com.combos.TaskManager.mapper;

import com.combos.TaskManager.dto.CommentDTO.CommentRequestDTO;
import com.combos.TaskManager.dto.CommentDTO.CommentResponseDTO;
import com.combos.TaskManager.entity.Comment;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.Task;

public class CommentMapper {
    public static Comment toEntity(CommentRequestDTO dto, ProjectMember member, Task task) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();

        comment.setContent(dto.content());
        comment.setTask(task);
        comment.setMember(member);

        return comment;
    }

    public static CommentResponseDTO toDTO(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                TaskMapper.toSummaryDTO(comment.getTask()),
                UserMapper.toDTO(comment.getMember() != null ? comment.getMember().getUser() : null)
        );
    }
}
