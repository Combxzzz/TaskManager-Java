package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.TaskDTO.TaskRequestDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskResponseDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskUpdateDTO;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.Task;
import com.combos.TaskManager.entity.enums.ProjectRole;
import com.combos.TaskManager.entity.enums.TaskStatus;
import com.combos.TaskManager.mapper.TaskMapper;
import com.combos.TaskManager.repository.ProjectMemberRepository;
import com.combos.TaskManager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public TaskService(TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository) {
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Task not found")
        );
    }

    private boolean canModifyTask(Task task, Long requesterUserId) {
        boolean isOwner = task.getMember().getUser().getId().equals(requesterUserId);
        boolean isAdmin = projectMemberRepository
                .findByProjectIdAndUserId(task.getProject().getId(), requesterUserId)
                .map(m -> m.getRole() == ProjectRole.ADMIN)
                .orElse(false);

        return isOwner || isAdmin;
    }

    public TaskResponseDTO createTask(Long requesterUserId, TaskRequestDTO dto) {
        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(dto.projectId(), requesterUserId)
                .orElseThrow(() -> new RuntimeException("User not in project"));

        Task task = TaskMapper.toEntity(dto);

        task.setMember(member);
        task.setProject(member.getProject());

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

    public TaskResponseDTO updateTask(Long requesterUserId, Long id, TaskUpdateDTO dto) {
        Task task = findTaskById(id);

        if (!canModifyTask(task, requesterUserId)) {
            throw new RuntimeException("Not allowed to update this task");
        }

        if (dto.name() != null) {
            task.setName(dto.name());
        }

        if (dto.description() != null) {
            task.setDescription(dto.description());
        }

        task = taskRepository.save(task);

        return TaskMapper.toDTO(task);
    }

    public TaskResponseDTO updateTaskStatus(Long requesterUserId, Long id, TaskStatus newStatus) {
        Task task = findTaskById(id);

        if (!canModifyTask(task, requesterUserId)) {
            throw new RuntimeException("Not allowed to update this task");
        }

        task.setStatus(newStatus);

        task = taskRepository.save(task);

        return TaskMapper.toDTO(task);
    }

    public void deleteById(Long requesterUserId, Long id) {
        Task task = findTaskById(id);

        if (!canModifyTask(task, requesterUserId)) {
            throw new RuntimeException("Not allowed to delete this task");
        }

        taskRepository.delete(task);
    }
}
