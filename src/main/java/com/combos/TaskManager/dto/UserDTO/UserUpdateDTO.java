package com.combos.TaskManager.dto.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(max = 100) String name,
        @Size(max = 50) String nickname,
        @Size(max = 150) @Email String email,
        @Size(min = 6, max = 100) String password
) {
}
