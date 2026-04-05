package com.combos.TaskManager.dto.UserDTO;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String nickname,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
