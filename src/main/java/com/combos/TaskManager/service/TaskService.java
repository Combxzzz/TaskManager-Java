package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.TaskDTO.TaskRequestDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskResponseDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskUpdateDTO;
import com.combos.TaskManager.entity.Task;
import com.combos.TaskManager.entity.enums.TaskStatus;
import com.combos.TaskManager.mapper.TaskMapper;
import com.combos.TaskManager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Task not found")
        );
    }

    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = TaskMapper.toEntity(dto);
        task = taskRepository.save(task);

        return TaskMapper.toDTO(task);
    }

    public List<TaskResponseDTO> findAll() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    public TaskResponseDTO findById(Long id) {
        Task task = findTaskById(id);

        return TaskMapper.toDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO dto) {
        Task task = findTaskById(id);

        if (dto.name() != null) {
            task.setName(dto.name());
        }

        if (dto.description() != null) {
            task.setDescription(dto.description());
        }

        task = taskRepository.save(task);

        return TaskMapper.toDTO(task);
    }

    public TaskResponseDTO updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = findTaskById(id);
        task.setStatus(newStatus);

        task = taskRepository.save(task);

        return TaskMapper.toDTO(task);
    }

    public void deleteById(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }
}
