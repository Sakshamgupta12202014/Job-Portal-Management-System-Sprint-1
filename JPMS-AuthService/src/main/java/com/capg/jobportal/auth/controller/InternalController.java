package com.capg.jobportal.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capg.jobportal.auth.entity.User;
import com.capg.jobportal.auth.service.UserService;

@RestController
@RequestMapping("/api/internal")
public class InternalController {

    @Autowired
    private UserService userService;

    // ✅ GET ALL USERS
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ DELETE USER
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // ✅ BAN USER
    @PutMapping("/users/{id}/ban")
    public void banUser(@PathVariable Long id) {
        userService.banUser(id);
    }

    // ✅ UNBAN USER
    @PutMapping("/users/{id}/unban")
    public void unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
    }
}