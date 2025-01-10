package com.ertanAlabay.SiApp.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto {

    private Long id;
    private String name;
    private String content;
    private String createdBy;
    private LocalDateTime deadline;
    private Set<Long> assignedUserIds;

    // Getters and Setters
}
