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
            // Fetch complete user details including role_id
            UserDao.UserDetails userDetails = userDao.getUserDetails(username);

            if (userDetails != null) {
                // Create user DTO to return with all details
                UserDTO userDTO = new UserDTO(
                    userDetails.getUserId(),
                    userDetails.getFullName(),
                    userDetails.getEmail(),
                    userDetails.getContactNumber(),
                    userDetails.getRoleId()
                );

                return ApiResponse.success("Login successful", userDTO);
            } else {
                return ApiResponse.error("Unable to fetch user details");
            }
        } else {
            return ApiResponse.error("Invalid credentials");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}


