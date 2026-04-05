package com.combos.TaskManager.dto.TaskDTO;

import com.combos.TaskManager.dto.CommentDTO.CommentResponseDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.entity.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponseDTO(
        Long id,
        String name,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ProjectResponseDTO project,
        List<CommentResponseDTO> comments
) {
}
