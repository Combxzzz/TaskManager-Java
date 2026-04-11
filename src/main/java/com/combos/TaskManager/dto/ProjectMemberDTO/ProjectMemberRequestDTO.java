package com.combos.TaskManager.dto.ProjectMemberDTO;

import jakarta.validation.constraints.NotNull;

public record ProjectMemberRequestDTO(
        @NotNull Long userId,
        @NotNull Long projectId
) {
}
