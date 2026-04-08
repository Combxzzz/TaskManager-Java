package com.combos.TaskManager.service;

import com.combos.TaskManager.dto.ProjectDTO.ProjectRequestDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectResponseDTO;
import com.combos.TaskManager.dto.ProjectDTO.ProjectUpdateDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.mapper.ProjectMapper;
import com.combos.TaskManager.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Project not found")
        );
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Project project = ProjectMapper.toEntity(dto);
        project = projectRepository.save(project);

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

    public ProjectResponseDTO updateProject(Long id, ProjectUpdateDTO dto) {
        Project project = findProjectById(id);

        if (dto.name() != null) {
            project.setName(dto.name());
        }

        if (dto.description() != null) {
            project.setDescription(dto.description());
        }

        project = projectRepository.save(project);

        return ProjectMapper.toDTO(project);
    }

    public void deleteById(Long id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }
}
