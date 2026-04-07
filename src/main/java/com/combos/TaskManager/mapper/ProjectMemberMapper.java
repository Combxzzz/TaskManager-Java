package com.combos.TaskManager.mapper;

import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberRequestDTO;
import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberResponseDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.User;

public class ProjectMemberMapper {
    public static ProjectMember toEntity(
            ProjectMemberRequestDTO dto, Project project, User user) {
        if (dto == null) {
            return null;
        }

        ProjectMember projectMember = new ProjectMember();

        projectMember.setProject(project);
        projectMember.setRole(dto.role());
        projectMember.setUser(user);

        return projectMember;
    }

    public static ProjectMemberResponseDTO toDTO(ProjectMember projectMember) {
        if (projectMember == null) {
            return null;
        }

        return new ProjectMemberResponseDTO(
                projectMember.getId(),
                UserMapper.toDTO(projectMember.getUser()),
                ProjectMapper.toDTO(projectMember.getProject()),
                projectMember.getRole()
        );
    }
}
