package com.combos.TaskManager.dto.ProjectMemberDTO;

import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.dto.UserDTO.UserResponseDTO;
import com.combos.TaskManager.entity.enums.ProjectRole;

public record ProjectMemberResponseDTO(
        Long id,
        UserResponseDTO user,
        ProjectResponseDTO project,
        ProjectRole role
) {
}
