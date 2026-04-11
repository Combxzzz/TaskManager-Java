package com.combos.TaskManager.dto.TaskDTO;

import com.combos.TaskManager.entity.enums.TaskStatus;

public record TaskSummaryDTO(
        Long id,
        String name,
        String description,
        TaskStatus status
) {
}
