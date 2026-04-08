package com.combos.TaskManager.dto.ProjectDTO;

import jakarta.validation.constraints.Size;

public record ProjectUpdateDTO(
        @Size(max = 150) String name,
        @Size(max = 500) String description
) {
}
