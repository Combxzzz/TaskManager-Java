package com.combos.TaskManager.dto.CommentDTO;

import com.combos.TaskManager.dto.TaskDTO.TaskResponseDTO;
import com.combos.TaskManager.dto.UserDTO.UserResponseDTO;

public record CommentResponseDTO(
        Long id,
        String content,
        TaskResponseDTO task,
        UserResponseDTO user
) {
}
