package com.combos.TaskManager.repository;

import com.combos.TaskManager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
