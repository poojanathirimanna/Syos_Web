package com.syos.web.application.usecases;

import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.LoginRequest;
import com.syos.web.application.dto.UserDTO;
import com.syos.web.dao.UserDao;

public class LoginUseCase {

    private final UserDao userDao;

    public LoginUseCase() {
        this.userDao = new UserDao();
    }

    public LoginUseCase(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Execute login use case
     * @param request LoginRequest containing username and password
     * @return ApiResponse with result
     */
    public ApiResponse execute(LoginRequest request) {
        // Validation
        if (request == null) {
            return ApiResponse.error("Request cannot be null");
        }

        String username = request.getUsername();
        String password = request.getPassword();

        if (isBlank(username) || isBlank(password)) {
            return ApiResponse.error("Username and password are required");
        }

        // Business logic: validate credentials
        boolean isValid = userDao.isValidUser(username, password);

        if (isValid) {
            // Create user DTO to return
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(username);

            return ApiResponse.success("Login successful", userDTO);
        } else {
            return ApiResponse.error("Invalid credentials");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}


