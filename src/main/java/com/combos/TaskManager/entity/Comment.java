package com.combos.TaskManager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 500)
    @NotNull
    private String content;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "member_id", nullable = false)
    private ProjectMember member;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
