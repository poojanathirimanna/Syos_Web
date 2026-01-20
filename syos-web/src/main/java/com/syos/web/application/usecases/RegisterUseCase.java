package com.syos.web.application.usecases;

import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.RegisterRequest;
import com.syos.web.infrastructure.persistence.dao.UserDao;

public class RegisterUseCase {

    private final UserDao userDao;
    private static final int DEFAULT_ROLE_ID = 3; // Customer role

    public RegisterUseCase() {
        this.userDao = new UserDao();
    }

    public RegisterUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Execute registration use case
     * @param request RegisterRequest containing user details
     * @return ApiResponse with result
     */
    public ApiResponse<Object> execute(RegisterRequest request) {
        // Validation
        if (request == null) {
            return ApiResponse.error("Request cannot be null");
        }

        String userId = trim(request.getUserId());
        String fullName = trim(request.getFullName());
        String email = trim(request.getEmail());
        String contactNumber = trim(request.getContactNumber());
        String password = request.getPassword();

        // Required fields validation
        if (isBlank(userId) || isBlank(fullName) || isBlank(email) || isBlank(password)) {
            return ApiResponse.error("All fields are required");
        }

        // Business rule: Check if user ID already exists
        if (userDao.existsByUserId(userId)) {
            return ApiResponse.error("User ID already exists");
        }

        // Business rule: Check if email already exists
        if (userDao.existsByEmail(email)) {
            return ApiResponse.error("Email already exists");
        }

        // Execute registration
        boolean success = userDao.registerUser(
                userId,
                fullName,
                contactNumber,
                email,
                password,
                DEFAULT_ROLE_ID
        );

        if (success) {
            return ApiResponse.success("Account created successfully");
        } else {
            return ApiResponse.error("Registration failed. Please try again");
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

