package com.combos.TaskManager.repository;

import com.combos.TaskManager.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
