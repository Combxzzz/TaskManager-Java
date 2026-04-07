package com.combos.TaskManager.dto.CommentDTO;

import com.combos.TaskManager.dto.TaskDTO.TaskSummaryDTO;
import com.combos.TaskManager.dto.UserDTO.UserResponseDTO;

public record CommentResponseDTO(
        Long id,
        String content,
        TaskSummaryDTO task,
        UserResponseDTO user
) {
}
