package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.ProjectDTO.ProjectRequestDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectUpdateDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.User;
import com.combos.TaskManager.entity.enums.ProjectRole;
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
                () -> new RuntimeException("Project not found")
        );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    private boolean isProjectAdmin(Long projectId, Long userId) {
        return projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .map(m -> m.getRole() == ProjectRole.ADMIN)
                .orElse(false);
    }

    public ProjectResponseDTO createProject(Long requesterUserId, ProjectRequestDTO dto) {
        User user = findUserById(requesterUserId);

        Project project = ProjectMapper.toEntity(dto);
        project = projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProject(project);
        member.setRole(ProjectRole.ADMIN);
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

        if (!isProjectAdmin(projectId, requesterUserId)) {
            throw new RuntimeException("Only project admin can update this project");
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

        if (!isProjectAdmin(projectId, requesterUserId)) {
            throw new RuntimeException("Only project admin can delete this project");
        }

        projectRepository.delete(project);
    }
}
