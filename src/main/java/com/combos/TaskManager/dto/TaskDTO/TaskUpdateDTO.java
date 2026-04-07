package com.combos.TaskManager.dto.TaskDTO;

import jakarta.validation.constraints.Size;

public record TaskUpdateDTO(
        @Size(max = 150) String name,
        @Size(max = 1000) String description,
        Long projectId
) {
}
