package com.combos.TaskManager.dto.TaskDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull Long projectId
) {
}
