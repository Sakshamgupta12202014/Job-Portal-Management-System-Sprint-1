package com.capg.jobportal.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capg.jobportal.auth.dao.UserRepository;
import com.capg.jobportal.auth.entity.User;
import com.capg.jobportal.auth.enums.UserStatus;
import com.capg.jobportal.auth.exception.ResourceNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ DELETE USER
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    // ✅ BAN USER
    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    // ✅ UNBAN USER
    public void unbanUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }
}