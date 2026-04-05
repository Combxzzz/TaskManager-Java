package com.combos.TaskManager.dto.ProjectDTO;

import java.time.LocalDateTime;

public record ProjectResponseDTO(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
