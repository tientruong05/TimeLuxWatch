package com.TimeLuxWatchBE.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserEntity> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<UserEntity> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<UserEntity> searchUsersByName(String fullName) {
        return userRepository.findByFullNameContaining(fullName).stream()
                .filter(user -> user != null && user.getFullName() != null)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<UserEntity> getUsersByRole(int role) {
        return userRepository.findByRole(role).stream()
                .filter(user -> user != null)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Override
    public Optional<UserEntity> getUserByEmailAndPassword(String email, String password) {
        return Optional.ofNullable(userRepository.findByEmailAndPassword(email, password));
    }

    @Override
    public void createUser(UserEntity user) {
        Optional.ofNullable(user).ifPresent(userRepository::save);
    }

    @Override
    public void updateUser(UserEntity updatedUserData) {
        // Check if the user data itself or its ID is null (ID is primitive int, check against 0 or handle appropriately if ID can be 0 legitimately)
        if (updatedUserData == null || updatedUserData.getId() == 0) { // Assuming ID > 0 for valid users
            System.err.println("Attempted to update user with null data or invalid ID (0).");
            // Decide whether to return or throw an exception based on requirements
            // throw new IllegalArgumentException("User data or ID cannot be null/invalid for update.");
            return; // Hoặc throw exception
        }

        // 1. Fetch the existing user from the database
        Optional<UserEntity> existingUserOpt = userRepository.findById(updatedUserData.getId());

        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();

            // 2. Update only the allowed fields, preserving the password
            existingUser.setUsername(updatedUserData.getUsername()); // Username might change if allowed by controller logic
            existingUser.setEmail(updatedUserData.getEmail());
            existingUser.setFullName(updatedUserData.getFullName());
            existingUser.setPhone(updatedUserData.getPhone());
            existingUser.setAddress(updatedUserData.getAddress()); // Update address
            existingUser.setRole(updatedUserData.isRole());       // Update role
            existingUser.setStatus(updatedUserData.isStatus());   // Update status
            // DO NOT update the password here unless specifically handled
            // existingUser.setPassword(updatedUserData.getPassword()); // <- Avoid this line

            // 3. Save the updated existing entity
            userRepository.save(existingUser);
        } else {
            // Handle case where user to update is not found
            System.err.println("User with ID " + updatedUserData.getId() + " not found for update.");
            // Optionally throw an exception
             throw new RuntimeException("Không tìm thấy người dùng với ID: " + updatedUserData.getId());
        }
    }

    @Override
    public void deleteUser(int id) {
        if (id <= 0) return;
        
        // Check if user exists before attempting delete
        if (!userRepository.existsById(id)) {
             System.err.println("User with ID " + id + " not found for deletion.");
             throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        }

        try {
            userRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Catch specific exception for foreign key violations
            System.err.println("Error deleting user ID " + id + " due to data integrity violation: " + e.getMessage());
            // Throw a more specific/user-friendly exception or handle as needed
            throw new RuntimeException("Không thể xóa người dùng này vì có dữ liệu liên quan (ví dụ: đơn hàng).", e);
        } catch (Exception e) {
            // Catch other potential errors during delete
            System.err.println("Error deleting user ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Lỗi không mong muốn khi xóa người dùng.", e); // Re-throw general exception
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return Optional.ofNullable(email)
                .map(userRepository::existsByEmail)
                .orElse(false);
    }

    @Override
    public Optional<UserEntity> findByUsernameAndEmail(String username, String email) {
        return Optional.ofNullable(userRepository.findByUsernameAndEmail(username, email));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return Optional.ofNullable(phone)
                .map(userRepository::existsByPhone)
                .orElse(false);
    }

    @Override
    public boolean existsByUsername(String username) {
        return Optional.ofNullable(username)
                .map(userRepository::existsByUsername)
                .orElse(false);
    }
}
