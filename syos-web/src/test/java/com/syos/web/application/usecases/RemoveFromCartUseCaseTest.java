package com.syos.web.application.usecases;

import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RemoveFromCartUseCaseTest {

    @Mock
    private ShoppingCartDao cartDao;

    private RemoveFromCartUseCase removeFromCartUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        removeFromCartUseCase = new RemoveFromCartUseCase();
    }

    @Test
    public void testExecuteSuccess() throws SQLException {
        Integer cartId = 1;
        String userId = "U001";

        when(cartDao.removeItem(cartId, userId)).thenReturn(true);

        boolean result = removeFromCartUseCase.execute(cartId, userId);

        assertTrue(result);
        verify(cartDao, times(1)).removeItem(cartId, userId);
    }

    @Test
    public void testExecuteItemNotFound() throws SQLException {
        Integer cartId = 999;
        String userId = "U001";

        when(cartDao.removeItem(cartId, userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> removeFromCartUseCase.execute(cartId, userId));
    }

    @Test
    public void testExecuteUnauthorized() throws SQLException {
        Integer cartId = 1;
        String userId = "U002";

        when(cartDao.removeItem(cartId, userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> removeFromCartUseCase.execute(cartId, userId));
    }

    @Test
    public void testExecuteSQLException() throws SQLException {
        Integer cartId = 1;
        String userId = "U001";

        when(cartDao.removeItem(cartId, userId))
            .thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class,
            () -> removeFromCartUseCase.execute(cartId, userId));
    }

    @Test
    public void testExecuteNullCartId() throws SQLException {
        assertThrows(Exception.class,
            () -> removeFromCartUseCase.execute(null, "U001"));
    }

    @Test
    public void testExecuteNullUserId() throws SQLException {
        assertThrows(Exception.class,
            () -> removeFromCartUseCase.execute(1, null));
    }

    @Test
    public void testExecuteMultipleItems() throws SQLException {
        String userId = "U001";

        when(cartDao.removeItem(1, userId)).thenReturn(true);
        when(cartDao.removeItem(2, userId)).thenReturn(true);
        when(cartDao.removeItem(3, userId)).thenReturn(true);

        assertTrue(removeFromCartUseCase.execute(1, userId));
        assertTrue(removeFromCartUseCase.execute(2, userId));
        assertTrue(removeFromCartUseCase.execute(3, userId));

        verify(cartDao, times(3)).removeItem(anyInt(), eq(userId));
    }

    @Test
    public void testExecuteVerifyInteraction() throws SQLException {
        Integer cartId = 5;
        String userId = "U001";

        when(cartDao.removeItem(cartId, userId)).thenReturn(true);

        removeFromCartUseCase.execute(cartId, userId);

        verify(cartDao, times(1)).removeItem(cartId, userId);
        verifyNoMoreInteractions(cartDao);
    }

    @Test
    public void testDefaultConstructor() {
        RemoveFromCartUseCase useCase = new RemoveFromCartUseCase();
        assertNotNull(useCase);
    }

    @Test
    public void testExecuteWithZeroCartId() throws SQLException {
        when(cartDao.removeItem(0, "U001")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> removeFromCartUseCase.execute(0, "U001"));
    }

    @Test
    public void testExecuteWithNegativeCartId() throws SQLException {
        when(cartDao.removeItem(-1, "U001")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> removeFromCartUseCase.execute(-1, "U001"));
    }

    @Test
    public void testExecuteWithLargeCartId() throws SQLException {
        Integer largeId = Integer.MAX_VALUE;
        String userId = "U001";

        when(cartDao.removeItem(largeId, userId)).thenReturn(true);

        assertTrue(removeFromCartUseCase.execute(largeId, userId));
    }

    @Test
    public void testExecuteEmptyUserId() throws SQLException {
        assertThrows(Exception.class,
            () -> removeFromCartUseCase.execute(1, ""));
    }

    @Test
    public void testExecuteDifferentUserIds() throws SQLException {
        String[] userIds = {"U001", "U002", "CUSTOMER123"};

        for (int i = 0; i < userIds.length; i++) {
            when(cartDao.removeItem(i + 1, userIds[i])).thenReturn(true);
            assertTrue(removeFromCartUseCase.execute(i + 1, userIds[i]));
        }
    }

    @Test
    public void testExecuteSequentialRemoval() throws SQLException {
        String userId = "U001";

        for (int i = 1; i <= 10; i++) {
            when(cartDao.removeItem(i, userId)).thenReturn(true);
            assertTrue(removeFromCartUseCase.execute(i, userId));
        }

        verify(cartDao, times(10)).removeItem(anyInt(), eq(userId));
    }

    @Test
    public void testExecutePartialFailure() throws SQLException {
        String userId = "U001";

        when(cartDao.removeItem(1, userId)).thenReturn(true);
        when(cartDao.removeItem(2, userId)).thenReturn(false);

        assertTrue(removeFromCartUseCase.execute(1, userId));
        assertThrows(IllegalArgumentException.class,
            () -> removeFromCartUseCase.execute(2, userId));
    }

    @Test
    public void testExecuteWithLongUserId() throws SQLException {
        String longUserId = "U" + "0".repeat(100);

        when(cartDao.removeItem(1, longUserId)).thenReturn(true);

        assertTrue(removeFromCartUseCase.execute(1, longUserId));
    }

    @Test
    public void testExecuteWithSpecialCharUserId() throws SQLException {
        String userId = "user@test.com";

        when(cartDao.removeItem(1, userId)).thenReturn(true);

        assertTrue(removeFromCartUseCase.execute(1, userId));
    }

    @Test
    public void testExecuteReturnValue() throws SQLException {
        when(cartDao.removeItem(1, "U001")).thenReturn(true);

        boolean result = removeFromCartUseCase.execute(1, "U001");

        assertEquals(true, result);
        assertTrue(result);
    }
}

