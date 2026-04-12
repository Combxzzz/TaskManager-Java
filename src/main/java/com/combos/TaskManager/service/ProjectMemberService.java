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

    private ProjectMember findMembershipByProjectAndUser(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId).orElseThrow(
                () -> new RuntimeException("User is not a member of this project")
        );
    }

    private boolean canManageMembers(ProjectRole role) {
        return role == ProjectRole.OWNER || role == ProjectRole.ADMIN;
    }

    private boolean canRemoveMember(ProjectMember requester, ProjectMember target) {
        if (!requester.getProject().getId().equals(target.getProject().getId())) {
            return false;
        }

        if (requester.getId().equals(target.getId())) {
            return false;
        }

        return switch (requester.getRole()) {
            case OWNER -> target.getRole() != ProjectRole.OWNER;
            case ADMIN -> target.getRole() == ProjectRole.MEMBER;
            case MEMBER -> false;
        };
    }

    public ProjectMemberResponseDTO addMember(Long requesterUserId, ProjectMemberRequestDTO dto) {
        User user = findUserById(dto.userId());
        Project project = findProjectById(dto.projectId());
        ProjectMember requester = findMembershipByProjectAndUser(dto.projectId(), requesterUserId);

        if (!canManageMembers(requester.getRole())) {
            throw new RuntimeException("Only owners or admins can add members to project");
        }

        if (projectMemberRepository.existsByUserAndProject(user, project)) {
            throw new RuntimeException("User already in this project");
        }

        ProjectMember member = ProjectMemberMapper.toEntity(dto, project, user);

        member = projectMemberRepository.save(member);

        return ProjectMemberMapper.toDTO(member);
    }

    public List<ProjectMemberResponseDTO> findAllByProjectId(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
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

    public void deleteMemberById(Long requesterUserId, Long memberId, Long projectId) {
        ProjectMember requester = findMembershipByProjectAndUser(projectId, requesterUserId);
        ProjectMember member = findProjectMemberById(memberId);

        if (!member.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Member does not belong to this project");
        }

        if (!canManageMembers(requester.getRole())) {
            throw new RuntimeException("Only owners or admins can remove members");
        }

        if (!canRemoveMember(requester, member)) {
            throw new RuntimeException("You do not have permission to remove this member");
        }

        projectMemberRepository.delete(member);
    }
}
