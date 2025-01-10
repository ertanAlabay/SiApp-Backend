package com.ertanAlabay.SiApp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String createdBy;

    private LocalDateTime deadline;

    @ManyToMany
    @JoinTable(
        name = "file_users",
        joinColumns = @JoinColumn(name = "file_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers = new HashSet<>();

    public File() {}

    public File(String name, String content, String createdBy, LocalDateTime deadline) {
        this.name = name;
        this.content = content;
        this.createdBy = createdBy;
        this.deadline = deadline;
    }

    // Getters and Setters
}
