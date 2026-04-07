package com.combos.TaskManager.mapper;

import com.combos.TaskManager.dto.UserDTO.UserRequestDTO;
import com.combos.TaskManager.dto.UserDTO.UserResponseDTO;
import com.combos.TaskManager.entity.User;

public class UserMapper {
    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();

        user.setName(dto.name());
        user.setNickname(dto.nickname());
        user.setPassword(dto.password());
        user.setEmail(dto.email());

        return user;
    }

    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getNickname(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
