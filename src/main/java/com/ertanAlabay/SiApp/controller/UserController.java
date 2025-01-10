package com.ertanAlabay.SiApp.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ertanAlabay.SiApp.dto.LoginRequest;
import com.ertanAlabay.SiApp.dto.RegisterRequest;
import com.ertanAlabay.SiApp.entity.Role;
import com.ertanAlabay.SiApp.entity.User;
import com.ertanAlabay.SiApp.repository.UserRepository;
import com.ertanAlabay.SiApp.service.UserService;
import com.ertanAlabay.SiApp.util.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Kullanıcı Kaydı
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Kullanıcı Giriş
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // Kullanıcıyı kimlik doğrulama mekanizması ile doğrula
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Kimlik doğrulaması başarılı olursa, kullanıcı detaylarını alın
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // JWT oluştur
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok("Login successful! \nToken: " + jwt);
    }

    // Kullanıcı Güncelleme
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @RequestBody RegisterRequest updatedRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        user.setName(updatedRequest.getName());
        user.setEmail(updatedRequest.getEmail());

        // Şifre güncelleme
        if (updatedRequest.getPassword() != null && !updatedRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedRequest.getPassword()));
        }

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully!");
    }

    // Rolleri Güncelleme (Sadece ADMIN)
    @PutMapping("/update-roles/{id}")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @RequestBody Set<Role> roles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("User roles updated successfully!");
    }
}