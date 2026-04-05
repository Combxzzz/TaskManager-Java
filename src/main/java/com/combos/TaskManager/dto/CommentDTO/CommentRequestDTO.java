package com.combos.TaskManager.dto.CommentDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(
        @NotBlank @Size(max = 500) String content,
        @NotNull Long taskId,
        @NotNull Long memberId
) {
}
