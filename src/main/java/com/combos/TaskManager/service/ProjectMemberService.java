package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberRequestDTO;
import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberResponseDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.User;
import com.combos.TaskManager.entity.enums.ProjectRole;
import com.combos.TaskManager.exception.DuplicateResourceException;
import com.combos.TaskManager.exception.ResourceNotFoundException;
import com.combos.TaskManager.exception.UnauthorizedException;
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
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Project", "id", id)
        );
    }

    private ProjectMember findProjectMemberById(Long id) {
        return projectMemberRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("ProjectMember", "id", id)
        );
    }

    private ProjectMember findMembershipByProjectAndUser(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId).orElseThrow(
                () -> new UnauthorizedException(
                        "access project members",
                        "You must be a member of this project"
                )
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
            throw new UnauthorizedException(
                    "add member",
                    "Only owners or admins can add members to this project"
            );
        }

        if (projectMemberRepository.existsByUserAndProject(user, project)) {
            throw new DuplicateResourceException("ProjectMember", "userId", dto.userId());
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
                        () -> new ResourceNotFoundException("ProjectMember", "userId", userId)
                );

        return ProjectMemberMapper.toDTO(member);
    }

    public void deleteMemberById(Long requesterUserId, Long memberId, Long projectId) {
        ProjectMember requester = findMembershipByProjectAndUser(projectId, requesterUserId);
        ProjectMember member = findProjectMemberById(memberId);

        if (!member.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("Member does not belong to this project");
        }

        if (!canManageMembers(requester.getRole())) {
            throw new UnauthorizedException("remove member", "Only owners or admins can remove members");
        }

        if (!canRemoveMember(requester, member)) {
            throw new UnauthorizedException("remove member", "You do not have permission to remove this member");
        }

        projectMemberRepository.delete(member);
    }
}
