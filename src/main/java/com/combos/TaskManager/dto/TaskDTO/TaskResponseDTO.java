package com.combos.TaskManager.dto.TaskDTO;

import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.entity.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String name,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ProjectResponseDTO project
        // TODO: List<CommentResponseDTO> comments
) {
}
