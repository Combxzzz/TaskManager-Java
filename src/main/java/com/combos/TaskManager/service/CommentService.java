package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.CommentDTO.CommentRequestDTO;
import com.combos.TaskManager.dto.CommentDTO.CommentResponseDTO;
import com.combos.TaskManager.dto.CommentDTO.CommentUpdateDTO;
import com.combos.TaskManager.entity.*;
import com.combos.TaskManager.entity.enums.ProjectRole;
import com.combos.TaskManager.exception.InvalidOperationException;
import com.combos.TaskManager.exception.ResourceNotFoundException;
import com.combos.TaskManager.exception.UnauthorizedException;
import com.combos.TaskManager.mapper.CommentMapper;
import com.combos.TaskManager.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            ProjectMemberRepository projectMemberRepository) {

        this.commentRepository = commentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
    }

    private Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", id)
        );
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Task", "id", id)
        );
    }

    private boolean canManageProjectComments(Long projectId, Long userId) {
        return projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .map(m -> m.getRole() == ProjectRole.ADMIN || m.getRole() == ProjectRole.OWNER)
                .orElse(false);
    }

    public CommentResponseDTO createComment(Long requesterUserId, CommentRequestDTO dto) {
        Task task = findTaskById(dto.taskId());

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(
                        task.getProject().getId(),
                        requesterUserId
                )
                .orElseThrow(() -> new UnauthorizedException(
                        "create comment",
                        "You must be a member of this project"
                ));

        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setTask(task);
        comment.setMember(member);

        comment = commentRepository.save(comment);

        return CommentMapper.toDTO(comment);
    }

    public List<CommentResponseDTO> findAllByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(CommentMapper::toDTO)
                .toList();
    }

    public CommentResponseDTO updateComment(Long commentId, Long requesterUserId, CommentUpdateDTO dto) {
        Comment comment = findCommentById(commentId);

        if (!comment.getMember().getUser().getId().equals(requesterUserId)) {
            throw new UnauthorizedException("update comment", "Only the comment author can update this comment");
        }

        if (dto.content() != null) {
            comment.setContent(dto.content());
        } else {
            throw new InvalidOperationException("update comment", "Comment content cannot be null");
        }

        comment = commentRepository.save(comment);

        return CommentMapper.toDTO(comment);
    }

    public void deleteComment(Long requestUserId, Long commentId) {
        Comment comment = findCommentById(commentId);

        boolean isOwner = comment.getMember().getUser().getId().equals(requestUserId);
        boolean canManageComments = canManageProjectComments(comment.getTask().getProject().getId(), requestUserId);

        if (!isOwner && !canManageComments) {
            throw new UnauthorizedException(
                    "delete comment",
                    "Only the comment author, project owner, or project admins can delete this comment"
            );
        }

        commentRepository.delete(comment);
    }
}
