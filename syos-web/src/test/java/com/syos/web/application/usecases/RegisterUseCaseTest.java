package com.syos.web.application.usecases;

import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.RegisterRequest;
import com.syos.web.infrastructure.persistence.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUseCaseTest {

    @Mock
    private UserDao userDao;

    private RegisterUseCase registerUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registerUseCase = new RegisterUseCase(userDao);
    }

    @Test
    public void testExecuteSuccess() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("Account created successfully", response.getMessage());
        verify(userDao).registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3);
    }

    @Test
    public void testExecuteNullRequest() {
        ApiResponse<Object> response = registerUseCase.execute(null);

        assertFalse(response.isSuccess());
        assertEquals("Request cannot be null", response.getMessage());
    }

    @Test
    public void testExecuteNullUserId() {
        RegisterRequest request = new RegisterRequest(null, "John Doe", "john@test.com", "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteEmptyUserId() {
        RegisterRequest request = new RegisterRequest("", "John Doe", "john@test.com", "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteWhitespaceUserId() {
        RegisterRequest request = new RegisterRequest("   ", "John Doe", "john@test.com", "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteNullFullName() {
        RegisterRequest request = new RegisterRequest("U001", null, "john@test.com", "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteEmptyFullName() {
        RegisterRequest request = new RegisterRequest("U001", "", "john@test.com", "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteNullEmail() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", null, "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteEmptyEmail() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "", "0771234567", "password123");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteNullPassword() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", null);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteEmptyPassword() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteUserIdExists() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("User ID already exists", response.getMessage());
        verify(userDao, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    public void testExecuteEmailExists() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Email already exists", response.getMessage());
        verify(userDao, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    public void testExecuteRegistrationFailed() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(false);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("Registration failed. Please try again", response.getMessage());
    }

    @Test
    public void testExecuteWithNullContactNumber() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", null, "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", null, "john@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteWithWhitespaceInFields() {
        RegisterRequest request = new RegisterRequest(" U001 ", " John Doe ", " john@test.com ", " 0771234567 ", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
        verify(userDao).registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3);
    }

    @Test
    public void testExecuteWithSpecialCharsInName() {
        RegisterRequest request = new RegisterRequest("U001", "O'Brien-Smith", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "O'Brien-Smith", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteWithUnicodeName() {
        RegisterRequest request = new RegisterRequest("U001", "José García", "jose@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("jose@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "José García", "0771234567", "jose@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteWithLongPassword() {
        String longPassword = "a".repeat(200);
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", longPassword);

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", longPassword, 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteWithSpecialEmailFormat() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john+test@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john+test@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john+test@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteDatabaseException() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> registerUseCase.execute(request));
    }

    @Test
    public void testExecuteVerifyInteractions() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        registerUseCase.execute(request);

        verify(userDao, times(1)).existsByUserId("U001");
        verify(userDao, times(1)).existsByEmail("john@test.com");
        verify(userDao, times(1)).registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3);
    }

    @Test
    public void testDefaultConstructor() {
        RegisterUseCase useCase = new RegisterUseCase();
        assertNotNull(useCase);
    }

    @Test
    public void testExecuteWithPhoneNumberFormat() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "077-123-4567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "077-123-4567", "john@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteCustomerRoleIsDefault() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        registerUseCase.execute(request);

        verify(userDao).registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), eq(3));
    }

    @Test
    public void testExecuteWithShortPassword() {
        RegisterRequest request = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "123");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteWithNumericUserId() {
        RegisterRequest request = new RegisterRequest("12345", "John Doe", "john@test.com", "0771234567", "password123");

        when(userDao.existsByUserId("12345")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("12345", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testExecuteAllFieldsEmpty() {
        RegisterRequest request = new RegisterRequest("", "", "", "", "");

        ApiResponse<Object> response = registerUseCase.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("All fields are required", response.getMessage());
    }

    @Test
    public void testExecuteMultipleRegistrations() {
        RegisterRequest request1 = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");
        RegisterRequest request2 = new RegisterRequest("U002", "Jane Doe", "jane@test.com", "0771234568", "password456");

        when(userDao.existsByUserId("U001")).thenReturn(false);
        when(userDao.existsByEmail("john@test.com")).thenReturn(false);
        when(userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password123", 3)).thenReturn(true);

        when(userDao.existsByUserId("U002")).thenReturn(false);
        when(userDao.existsByEmail("jane@test.com")).thenReturn(false);
        when(userDao.registerUser("U002", "Jane Doe", "0771234568", "jane@test.com", "password456", 3)).thenReturn(true);

        ApiResponse<Object> response1 = registerUseCase.execute(request1);
        ApiResponse<Object> response2 = registerUseCase.execute(request2);

        assertTrue(response1.isSuccess());
        assertTrue(response2.isSuccess());
        verify(userDao, times(2)).registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), eq(3));
    }
}

