package com.ertanAlabay.SiApp.controller;

import com.ertanAlabay.SiApp.dto.FileDto;
import com.ertanAlabay.SiApp.service.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public FileDto createFile(@RequestBody FileDto fileDto, Authentication authentication) {
        String createdBy = authentication.getName();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());
        return fileService.createFile(fileDto, createdBy, roles);
    }

    @DeleteMapping("/{fileId}")
    public void deleteFile(@PathVariable Long fileId, Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());
        fileService.deleteFile(fileId, roles);
    }

    @GetMapping("/{fileId}")
    public FileDto getFile(@PathVariable Long fileId, Authentication authentication) {
        return fileService.getFile(fileId, authentication.getName());
    }
    
    @GetMapping("/user-files/{userId}")
    public List<FileDto> getUserFiles(@PathVariable Long userId) {
        return fileService.getUserFiles(userId);
    }

    @PutMapping("/{fileId}")
    public FileDto updateFile(@PathVariable Long fileId, @RequestBody FileDto fileDto, Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());
        return fileService.updateFile(fileId, fileDto, roles);
    }
}
