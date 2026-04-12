package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.TaskDTO.TaskRequestDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskResponseDTO;
import com.combos.TaskManager.dto.TaskDTO.TaskUpdateDTO;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.Task;
import com.combos.TaskManager.entity.enums.ProjectRole;
import com.combos.TaskManager.entity.enums.TaskStatus;
import com.combos.TaskManager.exception.InvalidOperationException;
import com.combos.TaskManager.exception.ResourceNotFoundException;
import com.combos.TaskManager.exception.UnauthorizedException;
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
                () -> new ResourceNotFoundException("Task", "id", id)
        );
    }

    private boolean canModifyTask(Task task, Long requesterUserId) {
        boolean isOwner = task.getMember().getUser().getId().equals(requesterUserId);
        boolean isProjectManager = projectMemberRepository
                .findByProjectIdAndUserId(task.getProject().getId(), requesterUserId)
                .map(m -> m.getRole() == ProjectRole.ADMIN || m.getRole() == ProjectRole.OWNER)
                .orElse(false);

        return isOwner || isProjectManager;
    }

    public TaskResponseDTO createTask(Long requesterUserId, TaskRequestDTO dto) {
        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(dto.projectId(), requesterUserId)
                .orElseThrow(() -> new UnauthorizedException(
                        "create task",
                        "You must be a member of this project"
                ));

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
            throw new UnauthorizedException(
                    "update task",
                    "Only the task creator, project owner, or project admins can modify tasks"
            );
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
            throw new UnauthorizedException(
                    "update task status",
                    "Only the task creator, project owner, or project admins can modify this task"
            );
        }

        if (newStatus == null) {
            throw new InvalidOperationException("update task status", "Task status cannot be null");
        }

        task.setStatus(newStatus);

        task = taskRepository.save(task);

        return TaskMapper.toDTO(task);
    }

    public void deleteById(Long requesterUserId, Long id) {
        Task task = findTaskById(id);

        if (!canModifyTask(task, requesterUserId)) {
            throw new UnauthorizedException(
                    "delete task",
                    "Only the task creator, project owner, or project admins can delete this task"
            );
        }

        taskRepository.delete(task);
    }
}
