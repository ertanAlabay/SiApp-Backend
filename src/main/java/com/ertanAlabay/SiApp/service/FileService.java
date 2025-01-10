package com.ertanAlabay.SiApp.service;

import com.ertanAlabay.SiApp.dto.FileDto;
import com.ertanAlabay.SiApp.entity.File;
import com.ertanAlabay.SiApp.entity.User;
import com.ertanAlabay.SiApp.repository.FileRepository;
import com.ertanAlabay.SiApp.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public FileDto createFile(FileDto fileDto, String createdBy, Set<String> roles) {
        if (roles.contains("ROLE_USER")) {
            throw new AccessDeniedException("You do not have permission to create files.");
        }

        File file = new File();
        file.setName(fileDto.getName());
        file.setContent(fileDto.getContent());
        file.setCreatedBy(createdBy);
        file.setDeadline(fileDto.getDeadline());

        if (fileDto.getAssignedUserIds() != null) {
            Set<User> assignedUsers = new HashSet<>();
            for (Long userId : fileDto.getAssignedUserIds()) {
                assignedUsers.add(userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found")));
            }
            file.setAssignedUsers(assignedUsers);
        }

        file = fileRepository.save(file);
        return mapToDto(file);
    }

    public void deleteFile(Long fileId, Set<String> roles) {
        if (roles.contains("ROLE_USER")) {
            throw new AccessDeniedException("You do not have permission to delete files.");
        }
        fileRepository.deleteById(fileId);
    }

    public FileDto getFile(Long fileId, String currentUsername) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        boolean isAssigned = file.getAssignedUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUsername));

        if (!isAssigned) {
            throw new AccessDeniedException("You do not have permission to view this file.");
        }

        return mapToDto(file);
    }

    public FileDto updateFile(Long fileId, FileDto fileDto, Set<String> roles) {
        if (roles.contains("ROLE_USER")) {
            throw new AccessDeniedException("You do not have permission to update files.");
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        file.setName(fileDto.getName());
        file.setContent(fileDto.getContent());
        file.setDeadline(fileDto.getDeadline());

        if (fileDto.getAssignedUserIds() != null) {
            Set<User> assignedUsers = new HashSet<>();
            for (Long userId : fileDto.getAssignedUserIds()) {
                assignedUsers.add(userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found")));
            }
            file.setAssignedUsers(assignedUsers);
        }

        file = fileRepository.save(file);
        return mapToDto(file);
    }

    private FileDto mapToDto(File file) {
        FileDto dto = new FileDto();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setContent(file.getContent());
        dto.setCreatedBy(file.getCreatedBy());
        dto.setDeadline(file.getDeadline());
        dto.setAssignedUserIds(file.getAssignedUsers().stream().map(User::getId).collect(HashSet::new, HashSet::add, HashSet::addAll));
        return dto;
    }
    
    public List<FileDto> getUserFiles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        List<File> userFiles = fileRepository.findAll().stream()
                .filter(file -> file.getAssignedUsers().contains(user))
                .toList();

        return userFiles.stream()
                .map(this::mapToDto)
                .toList();
    }
}
