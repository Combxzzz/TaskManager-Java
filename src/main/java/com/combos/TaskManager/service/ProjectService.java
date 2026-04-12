package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.ProjectDTO.ProjectRequestDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectUpdateDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.User;
import com.combos.TaskManager.entity.enums.ProjectRole;
import com.combos.TaskManager.exception.ResourceNotFoundException;
import com.combos.TaskManager.exception.UnauthorizedException;
import com.combos.TaskManager.mapper.ProjectMapper;
import com.combos.TaskManager.repository.ProjectMemberRepository;
import com.combos.TaskManager.repository.ProjectRepository;
import com.combos.TaskManager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
                          ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Project", "id", id)
        );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    private ProjectMember findMembershipByProjectAndUser(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId).orElseThrow(
                () -> new UnauthorizedException(
                        "access project",
                        "You must be a member of this project"
                )
        );
    }

    private boolean canManageProject(ProjectRole role) {
        return role == ProjectRole.OWNER || role == ProjectRole.ADMIN;
    }

    private boolean isProjectOwner(ProjectRole role) {
        return role == ProjectRole.OWNER;
    }

    public ProjectResponseDTO createProject(Long requesterUserId, ProjectRequestDTO dto) {
        User user = findUserById(requesterUserId);

        Project project = ProjectMapper.toEntity(dto);
        project = projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProject(project);
        member.setRole(ProjectRole.OWNER);
        projectMemberRepository.save(member);

        return ProjectMapper.toDTO(project);
    }

    public List<ProjectResponseDTO> findAll() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toDTO)
                .toList();
    }

    public ProjectResponseDTO findById(Long id) {
        Project project = findProjectById(id);

        return ProjectMapper.toDTO(project);
    }

    public ProjectResponseDTO updateProject(Long requesterUserId, Long projectId, ProjectUpdateDTO dto) {
        Project project = findProjectById(projectId);
        ProjectMember requester = findMembershipByProjectAndUser(projectId, requesterUserId);

        if (!canManageProject(requester.getRole())) {
            throw new UnauthorizedException("update project", "Only owners or admins can update this project");
        }

        if (dto.name() != null) {
            project.setName(dto.name());
        }

        if (dto.description() != null) {
            project.setDescription(dto.description());
        }

        project = projectRepository.save(project);

        return ProjectMapper.toDTO(project);
    }

    public void deleteById(Long requesterUserId, Long projectId) {
        Project project = findProjectById(projectId);
        ProjectMember requester = findMembershipByProjectAndUser(projectId, requesterUserId);

        if (!isProjectOwner(requester.getRole())) {
            throw new UnauthorizedException("delete project", "Only owners can delete this project");
        }

        projectRepository.delete(project);
    }
}
