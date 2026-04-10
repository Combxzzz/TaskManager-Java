package com.combos.TaskManager.dto.CommentDTO;

import jakarta.validation.constraints.Size;

public record CommentUpdateDTO(
        @Size(max = 500) String content
) {
}
