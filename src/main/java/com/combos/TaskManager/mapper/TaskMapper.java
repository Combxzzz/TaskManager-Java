package com.combos.TaskManager.mapper;

import com.combos.TaskManager.dto.CommentDTO.CommentResponseDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskRequestDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskResponseDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskSummaryDTO;
import com.combos.TaskManager.entity.Task;

import java.util.List;

public class TaskMapper {
    public static Task toEntity(TaskRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Task task = new Task();

        task.setName(dto.name());
        task.setDescription(dto.description());

        return task;
    }

    public static TaskResponseDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        List<CommentResponseDTO> comments = task.getComments() == null
                ? List.of()
                : task.getComments().stream()
                  .map(CommentMapper::toDTO)
                  .toList();

        return new TaskResponseDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getMember() != null
                        ? UserMapper.toDTO(task.getMember().getUser())
                        : null,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                ProjectMapper.toSummaryDTO(task.getProject()),
                comments
        );
    }

    public static TaskSummaryDTO toSummaryDTO(Task task) {
        if (task == null) {
            return null;
        }

        return new TaskSummaryDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus()
        );
    }
}
