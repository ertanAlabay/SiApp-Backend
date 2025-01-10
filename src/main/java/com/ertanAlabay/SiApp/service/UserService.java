package com.ertanAlabay.SiApp.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ertanAlabay.SiApp.dto.RegisterRequest;
import com.ertanAlabay.SiApp.entity.Role;
import com.ertanAlabay.SiApp.entity.User;
import com.ertanAlabay.SiApp.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match!");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER)); // Varsayılan olarak "USER" rolü atanıyor
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public void updateUserRoles(Long userId, Set<Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));
        user.setRoles(roles);
        userRepository.save(user);
    }
    
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

}
