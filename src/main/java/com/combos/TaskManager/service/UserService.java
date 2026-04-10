package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.UserDTO.UserRequestDTO;
import com.combos.TaskManager.dto.UserDTO.UserResponseDTO;
import com.combos.TaskManager.dto.UserDTO.UserUpdateDTO;
import com.combos.TaskManager.entity.User;
import com.combos.TaskManager.mapper.UserMapper;
import com.combos.TaskManager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    private User findUserById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email already exists");
        }

        if (repository.existsByNickname(dto.nickname())) {
            throw new RuntimeException("Nickname already exists");
        }

        User user = UserMapper.toEntity(dto);
        user = repository.save(user);

        return UserMapper.toDTO(user);
    }

    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public UserResponseDTO findById(Long id) {
        User user = findUserById(id);

        return UserMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(Long requesterUserId, Long id, UserUpdateDTO dto) {
        User user = findUserById(id);

        if (!user.getId().equals(requesterUserId)) {
            throw new RuntimeException("You can only update your own account");
        }

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.nickname() != null && !dto.nickname().equals(user.getNickname())) {
            if (repository.existsByNickname(dto.nickname())) {
                throw new RuntimeException("Nickname already in use");
            }
            user.setNickname(dto.nickname());
        }

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (repository.existsByEmail(dto.email())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(dto.email());
        }

        if (dto.password() != null) {
            user.setPassword(dto.password()); // TODO: Password encoder here
        }

        user = repository.save(user);

        return UserMapper.toDTO(user);
    }

    public void deleteById(Long requesterUserId, Long id) {
        User user = findUserById(id);
        if (!user.getId().equals(requesterUserId)) {
            throw new RuntimeException("You can only delete your own account");
        }
        repository.delete(user);
    }
}
