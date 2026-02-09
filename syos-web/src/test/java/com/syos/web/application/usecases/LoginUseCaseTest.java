package com.syos.web.application.usecases;

import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.LoginRequest;
import com.syos.web.application.dto.UserDTO;
import com.syos.web.infrastructure.persistence.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginUseCaseTest {

    @Mock
    private UserDao userDao;

    private LoginUseCase loginUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginUseCase = new LoginUseCase(userDao);
    }

    @Test
    public void testExecuteSuccess() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "John Doe", "user@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("user@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("user@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("U001", response.getData().getUserId());
        verify(userDao).isValidUser("user@test.com", "password123");
        verify(userDao).getUserDetails("user@test.com");
    }

    @Test
    public void testExecuteInvalidCredentials() {
        LoginRequest request = new LoginRequest("user@test.com", "wrongpassword");

        when(userDao.isValidUser("user@test.com", "wrongpassword")).thenReturn(false);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void testExecuteNullRequest() {
        ApiResponse<UserDTO> response = loginUseCase.execute(null);

        assertFalse(response.isSuccess());
        assertEquals("Request cannot be null", response.getMessage());
    }

    @Test
    public void testExecuteNullUsername() {
        LoginRequest request = new LoginRequest(null, "password123");

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteEmptyUsername() {
        LoginRequest request = new LoginRequest("", "password123");

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteWhitespaceUsername() {
        LoginRequest request = new LoginRequest("   ", "password123");

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteNullPassword() {
        LoginRequest request = new LoginRequest("user@test.com", null);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteEmptyPassword() {
        LoginRequest request = new LoginRequest("user@test.com", "");

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteWhitespacePassword() {
        LoginRequest request = new LoginRequest("user@test.com", "   ");

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteUserDetailsNull() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");

        when(userDao.isValidUser("user@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("user@test.com")).thenReturn(null);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Unable to fetch user details", response.getMessage());
    }

    @Test
    public void testExecuteWithSpecialCharsInUsername() {
        LoginRequest request = new LoginRequest("user+test@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "John Doe", "user+test@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("user+test@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("user+test@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    public void testExecuteWithLongPassword() {
        String longPassword = "a".repeat(200);
        LoginRequest request = new LoginRequest("user@test.com", longPassword);

        when(userDao.isValidUser("user@test.com", longPassword)).thenReturn(false);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
    }

    @Test
    public void testExecuteCustomerRole() {
        LoginRequest request = new LoginRequest("customer@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "C001", "Customer Name", "customer@test.com", "0771234567", 3
        );

        when(userDao.isValidUser("customer@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("customer@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().getRoleId());
    }

    @Test
    public void testExecuteCashierRole() {
        LoginRequest request = new LoginRequest("cashier@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "CA001", "Cashier Name", "cashier@test.com", "0771234567", 2
        );

        when(userDao.isValidUser("cashier@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("cashier@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().getRoleId());
    }

    @Test
    public void testExecuteAdminRole() {
        LoginRequest request = new LoginRequest("admin@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "A001", "Admin Name", "admin@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("admin@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("admin@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().getRoleId());
    }

    @Test
    public void testExecuteDatabaseException() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");

        when(userDao.isValidUser("user@test.com", "password123")).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> loginUseCase.execute(request));
    }

    @Test
    public void testExecuteMultipleFailedAttempts() {
        LoginRequest request = new LoginRequest("user@test.com", "wrongpassword");

        when(userDao.isValidUser("user@test.com", "wrongpassword")).thenReturn(false);

        for (int i = 0; i < 5; i++) {
            ApiResponse<UserDTO> response = loginUseCase.execute(request);
            assertFalse(response.isSuccess());
        }

        verify(userDao, times(5)).isValidUser("user@test.com", "wrongpassword");
    }

    @Test
    public void testExecuteCaseSensitivePassword() {
        LoginRequest request1 = new LoginRequest("user@test.com", "Password123");
        LoginRequest request2 = new LoginRequest("user@test.com", "password123");

        when(userDao.isValidUser("user@test.com", "Password123")).thenReturn(false);
        when(userDao.isValidUser("user@test.com", "password123")).thenReturn(true);

        ApiResponse<UserDTO> response1 = loginUseCase.execute(request1);
        ApiResponse<UserDTO> response2 = loginUseCase.execute(request2);

        assertFalse(response1.isSuccess());
    }

    @Test
    public void testExecuteWithUnicodeUsername() {
        LoginRequest request = new LoginRequest("пользователь@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "Name", "пользователь@test.com", "0771234567", 3
        );

        when(userDao.isValidUser("пользователь@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("пользователь@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testDefaultConstructor() {
        LoginUseCase useCase = new LoginUseCase();
        assertNotNull(useCase);
    }

    @Test
    public void testExecuteVerifyInteractions() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "John Doe", "user@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("user@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("user@test.com")).thenReturn(userDetails);

        loginUseCase.execute(request);

        verify(userDao, times(1)).isValidUser("user@test.com", "password123");
        verify(userDao, times(1)).getUserDetails("user@test.com");
        verifyNoMoreInteractions(userDao);
    }

    @Test
    public void testExecuteWithNullBothFields() {
        LoginRequest request = new LoginRequest(null, null);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteWithEmptyBothFields() {
        LoginRequest request = new LoginRequest("", "");

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Username and password are required", response.getMessage());
    }

    @Test
    public void testExecuteSuccessMessage() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "John Doe", "user@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("user@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("user@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertEquals("Login successful", response.getMessage());
    }

    @Test
    public void testExecuteUserDataComplete() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "John Doe", "user@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("user@test.com", "password123")).thenReturn(true);
        when(userDao.getUserDetails("user@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        UserDTO data = response.getData();
        assertEquals("U001", data.getUserId());
        assertEquals("John Doe", data.getFullName());
        assertEquals("user@test.com", data.getEmail());
        assertEquals("0771234567", data.getContactNumber());
        assertEquals(1, data.getRoleId());
    }

    @Test
    public void testExecuteWithNumbersInPassword() {
        LoginRequest request = new LoginRequest("user@test.com", "12345678");

        when(userDao.isValidUser("user@test.com", "12345678")).thenReturn(false);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertFalse(response.isSuccess());
    }

    @Test
    public void testExecuteWithSpecialCharsInPassword() {
        LoginRequest request = new LoginRequest("user@test.com", "P@ssw0rd!#$");

        UserDao.UserDetails userDetails = new UserDao.UserDetails(
            "U001", "John Doe", "user@test.com", "0771234567", 1
        );

        when(userDao.isValidUser("user@test.com", "P@ssw0rd!#$")).thenReturn(true);
        when(userDao.getUserDetails("user@test.com")).thenReturn(userDetails);

        ApiResponse<UserDTO> response = loginUseCase.execute(request);

        assertTrue(response.isSuccess());
    }
}

