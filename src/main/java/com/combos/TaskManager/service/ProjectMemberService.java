package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberRequestDTO;
import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberResponseDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.User;
import com.combos.TaskManager.entity.enums.ProjectRole;
import com.combos.TaskManager.mapper.ProjectMemberMapper;
import com.combos.TaskManager.repository.ProjectMemberRepository;
import com.combos.TaskManager.repository.ProjectRepository;
import com.combos.TaskManager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// TODO: Add JWT for verification in add and delete member
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;


    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                UserRepository userRepository,
                                ProjectRepository projectRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Project not found")
        );
    }

    private ProjectMember findProjectMemberById(Long id) {
        return projectMemberRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Project member not found")
        );
    }

    public ProjectMemberResponseDTO addMember(ProjectMemberRequestDTO dto) {
        User user = findUserById(dto.userId());
        Project project = findProjectById(dto.projectId());

        if (projectMemberRepository.existsByUserAndProject(user, project)) {
            throw new RuntimeException("User already in this project");
        }

        ProjectMember member = ProjectMemberMapper.toEntity(dto, project, user);

        member = projectMemberRepository.save(member);

        return ProjectMemberMapper.toDTO(member);
    }

    public List<ProjectMemberResponseDTO> findAllByProjectId(Long id) {
        return projectMemberRepository.findByProjectId(id).stream()
                .map(ProjectMemberMapper::toDTO)
                .toList();
    }

    public ProjectMemberResponseDTO findByProjectIdAndUserId(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(
                        () -> new RuntimeException("Member not found")
                );

        return ProjectMemberMapper.toDTO(member);
    }

    public void deleteMemberById(Long id) {
        ProjectMember member = findProjectMemberById(id);
        if (member.getRole() == ProjectRole.ADMIN) {
            throw new RuntimeException("Cannot remove admin");
        }
        projectMemberRepository.delete(member);
    }
}
