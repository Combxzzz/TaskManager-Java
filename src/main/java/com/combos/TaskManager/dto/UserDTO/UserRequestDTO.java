package com.combos.TaskManager.dto.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 50) String nickname,
        @Email @NotBlank @Size(max = 150) String email,
        @NotBlank @Size(min = 6, max = 100) String password
) {
}
