package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.CommentDTO.CommentRequestDTO;
import com.combos.TaskManager.dto.CommentDTO.CommentResponseDTO;
import com.combos.TaskManager.dto.CommentDTO.CommentUpdateDTO;
import com.combos.TaskManager.entity.*;
import com.combos.TaskManager.entity.enums.ProjectRole;
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
                () -> new RuntimeException("Comment not found")
        );
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Task not found")
        );
    }

    private ProjectMember findMemberById(Long id) {
        return projectMemberRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    private boolean isProjectAdmin(Long projectId, Long userId) {
        return projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .map(m -> m.getRole() == ProjectRole.ADMIN)
                .orElse(false);
    }

    public CommentResponseDTO createComment(Long requesterUserId, CommentRequestDTO dto) {
        Task task = findTaskById(dto.taskId());

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(
                        task.getProject().getId(),
                        requesterUserId
                )
                .orElseThrow(() -> new RuntimeException("User is not part of this project"));

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
            throw new RuntimeException("Don't have permission to update this comment");
        }

        if (dto.content() != null) {
            comment.setContent(dto.content());
        } else {
            throw new RuntimeException("Description cannot be null");
        }

        comment = commentRepository.save(comment);

        return CommentMapper.toDTO(comment);
    }

    public void deleteComment(Long requestUserId, Long commentId) {
        Comment comment = findCommentById(commentId);

        boolean isOwner = comment.getMember().getUser().getId().equals(requestUserId);
        boolean isAdmin = isProjectAdmin(comment.getTask().getProject().getId(), requestUserId);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
