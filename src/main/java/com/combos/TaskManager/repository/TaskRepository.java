package com.combos.TaskManager.repository;

import com.combos.TaskManager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
