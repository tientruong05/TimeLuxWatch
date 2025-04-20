package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserCRUDApiController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Map<String, Object> response = new HashMap<>();
        Page<UserEntity> userPage = userService.getAllUsers(page, size);
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalPages", userPage.getTotalPages());
        response.put("pageSize", userPage.getSize());
        response.put("totalItems", userPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserEntity user) {
        Map<String, Object> response = new HashMap<>();
        if (userService.existsByUsername(user.getUsername())) {
            response.put("error", "Tên đăng nhập '" + user.getUsername() + "' đã tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
        if (userService.existsByEmail(user.getEmail())) {
            response.put("error", "Email '" + user.getEmail() + "' đã tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
        if (userService.existsByPhone(user.getPhone())) {
            response.put("error", "Số điện thoại '" + user.getPhone() + "' đã tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
        userService.createUser(user);
        response.put("message", "Thêm người dùng thành công!");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable("id") int id) {
        Optional<UserEntity> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editUser(@PathVariable("id") int id, @RequestBody UserEntity user) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            if (!existingUser.get().getUsername().equals(user.getUsername()) && userService.existsByUsername(user.getUsername())) {
                response.put("error", "Tên đăng nhập '" + user.getUsername() + "' đã tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }
            if (!existingUser.get().getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
                response.put("error", "Email '" + user.getEmail() + "' đã tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }
            if (!existingUser.get().getPhone().equals(user.getPhone()) && userService.existsByPhone(user.getPhone())) {
                response.put("error", "Số điện thoại '" + user.getPhone() + "' đã tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }
            user.setId(id);
            userService.updateUser(user);
            response.put("message", "Cập nhật người dùng thành công!");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Không tìm thấy người dùng với ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") int id) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            response.put("message", "Xóa người dùng thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Không tìm thấy người dùng với ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam("keyword") String keyword) {
        Map<String, Object> response = new HashMap<>();
        response.put("users", userService.searchUsersByName(keyword));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Map<String, Object>> filterByRole(@PathVariable("role") int role) {
        Map<String, Object> response = new HashMap<>();
        response.put("users", userService.getUsersByRole(role));
        return ResponseEntity.ok(response);
    }
} 