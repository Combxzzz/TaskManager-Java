package com.combos.TaskManager.entity;

import com.combos.TaskManager.entity.enums.ProjectRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "tb_project_members",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ProjectRole role;
}
