package com.combos.TaskManager.mapper;

import com.combos.TaskManager.dto.ProjectDTO.ProjectRequestDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectSummaryDTO;
import com.combos.TaskManager.entity.Project;

public class ProjectMapper {
    public static Project toEntity(ProjectRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Project project = new Project();

        project.setName(dto.name());
        project.setDescription(dto.description());

        return project;
    }

    public static ProjectResponseDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }

        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public static ProjectSummaryDTO toSummaryDTO(Project project) {
        if (project == null) {
            return null;
        }

        return new ProjectSummaryDTO(
                project.getId(),
                project.getName(),
                project.getDescription()
        );
    }
}
