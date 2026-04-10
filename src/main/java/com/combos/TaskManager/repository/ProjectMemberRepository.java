package com.combos.TaskManager.repository;

import com.combos.TaskManager.dto.ProjectMemberDTO.ProjectMemberResponseDTO;
import com.combos.TaskManager.entity.Project;
import com.combos.TaskManager.entity.ProjectMember;
import com.combos.TaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByUserAndProject(User user, Project project);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByProjectId(Long projectId);
}
