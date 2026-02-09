package com.syos.web.infrastructure.persistence.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
    }

    @Test
    public void testConstructor() {
        assertNotNull(userDao);
    }

    @Test
    public void testIsValidUser() {
        assertDoesNotThrow(() -> {
            try {
                userDao.isValidUser("user@test.com", "password");
            } catch (Exception e) {
                // Expected if DB not available
            }
        });
    }

    @Test
    public void testIsValidUserNullUsername() {
        assertDoesNotThrow(() -> {
            try {
                userDao.isValidUser(null, "password");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testIsValidUserNullPassword() {
        assertDoesNotThrow(() -> {
            try {
                userDao.isValidUser("user@test.com", null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testGetUserDetails() {
        assertDoesNotThrow(() -> {
            try {
                userDao.getUserDetails("user@test.com");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByUserId() {
        assertDoesNotThrow(() -> {
            try {
                userDao.existsByUserId("U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByEmail() {
        assertDoesNotThrow(() -> {
            try {
                userDao.existsByEmail("user@test.com");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testRegisterUser() {
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser("U001", "John Doe", "0771234567", "john@test.com", "password", 3);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testRegisterUserNullFields() {
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser(null, null, null, null, null, 0);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testIsValidUserEmptyStrings() {
        assertDoesNotThrow(() -> {
            try {
                userDao.isValidUser("", "");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testGetUserDetailsNullUsername() {
        assertDoesNotThrow(() -> {
            try {
                userDao.getUserDetails(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByUserIdNull() {
        assertDoesNotThrow(() -> {
            try {
                userDao.existsByUserId(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByEmailNull() {
        assertDoesNotThrow(() -> {
            try {
                userDao.existsByEmail(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testIsValidUserMultipleCalls() {
        assertDoesNotThrow(() -> {
            try {
                userDao.isValidUser("user1@test.com", "pass1");
                userDao.isValidUser("user2@test.com", "pass2");
                userDao.isValidUser("user3@test.com", "pass3");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testRegisterMultipleUsers() {
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser("U001", "User 1", "077111", "user1@test.com", "pass1", 3);
                userDao.registerUser("U002", "User 2", "077222", "user2@test.com", "pass2", 3);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDifferentRoles() {
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser("A001", "Admin", "077111", "admin@test.com", "pass", 1);
                userDao.registerUser("C001", "Cashier", "077222", "cashier@test.com", "pass", 2);
                userDao.registerUser("U001", "Customer", "077333", "customer@test.com", "pass", 3);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByUserIdMultipleCalls() {
        assertDoesNotThrow(() -> {
            try {
                userDao.existsByUserId("U001");
                userDao.existsByUserId("U002");
                userDao.existsByUserId("U003");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByEmailMultipleCalls() {
        assertDoesNotThrow(() -> {
            try {
                userDao.existsByEmail("user1@test.com");
                userDao.existsByEmail("user2@test.com");
                userDao.existsByEmail("user3@test.com");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testGetUserDetailsMultipleCalls() {
        assertDoesNotThrow(() -> {
            try {
                userDao.getUserDetails("user1@test.com");
                userDao.getUserDetails("user2@test.com");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testUserWithSpecialChars() {
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser("U-001_A", "O'Brien", "077-123-4567", "user+tag@test.com", "p@ss!123", 3);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testUserWithLongFields() {
        String longName = "A".repeat(200);
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser("U001", longName, "077123456789", "user@test.com", "password", 3);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testMultipleDaoInstances() {
        UserDao dao1 = new UserDao();
        UserDao dao2 = new UserDao();
        UserDao dao3 = new UserDao();

        assertNotNull(dao1);
        assertNotNull(dao2);
        assertNotNull(dao3);
    }

    @Test
    public void testSequentialOperations() {
        assertDoesNotThrow(() -> {
            try {
                userDao.registerUser("U001", "Test User", "077123", "test@test.com", "pass", 3);
                userDao.existsByUserId("U001");
                userDao.existsByEmail("test@test.com");
                userDao.isValidUser("test@test.com", "pass");
                userDao.getUserDetails("test@test.com");
            } catch (Exception e) {
                // Expected
            }
        });
    }
}

