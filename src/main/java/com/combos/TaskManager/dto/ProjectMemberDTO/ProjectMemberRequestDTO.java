package com.combos.TaskManager.dto.ProjectMemberDTO;

import com.combos.TaskManager.entity.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record ProjectMemberRequestDTO(
        @NotNull Long userId,
        @NotNull Long projectId,
        @NotNull ProjectRole role
) {
}
