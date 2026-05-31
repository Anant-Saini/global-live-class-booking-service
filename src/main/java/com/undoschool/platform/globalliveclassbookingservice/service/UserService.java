package com.undoschool.platform.globalliveclassbookingservice.service;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.UserRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.entity.User;
import com.undoschool.platform.globalliveclassbookingservice.entity.UserRole;
import com.undoschool.platform.globalliveclassbookingservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.undoschool.platform.globalliveclassbookingservice.repository.UserRepository;

import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(UserRequestDTO dto) {
        // Validate Timezone early
        try { 
            ZoneId.of(dto.timezoneId()); 
        } catch (Exception e) { 
            throw new IllegalArgumentException("Invalid Timezone ID"); 
        }

        User user = User.builder()
                .name(dto.name())
                .role(UserRole.valueOf(dto.role()))
                .timezoneId(dto.timezoneId())
                .build();
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserRequestDTO dto) {
        // Validate Timezone early
        try { 
            ZoneId.of(dto.timezoneId()); 
        } catch (Exception e) { 
            throw new IllegalArgumentException("Invalid Timezone ID"); 
        }

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        existing.setName(dto.name());
        existing.setRole(UserRole.valueOf(dto.role()));
        existing.setTimezoneId(dto.timezoneId());
        
        return userRepository.save(existing);
    }

    public List<User> getAllUsers() { 
        return userRepository.findAll(); 
    }
}
