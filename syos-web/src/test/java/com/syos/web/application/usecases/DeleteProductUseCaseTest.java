package com.syos.web.application.usecases;

import com.syos.web.application.ports.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeleteProductUseCaseTest {

    @Mock
    private IProductRepository productRepository;

    private DeleteProductUseCase deleteProductUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteProductUseCase = new DeleteProductUseCase(productRepository);
    }

    @Test
    public void testExecuteSuccess() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

        boolean result = deleteProductUseCase.execute(productCode);

        assertTrue(result);
        verify(productRepository).existsByProductCode(productCode);
        verify(productRepository).deleteByProductCode(productCode);
    }

    @Test
    public void testExecuteNullProductCode() {
        assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute(null));
    }

    @Test
    public void testExecuteEmptyProductCode() {
        assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute(""));
    }

    @Test
    public void testExecuteWhitespaceProductCode() {
        assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute("   "));
    }

    @Test
    public void testExecuteProductNotFound() {
        String productCode = "P999";

        when(productRepository.existsByProductCode(productCode)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute(productCode));

        verify(productRepository).existsByProductCode(productCode);
        verify(productRepository, never()).deleteByProductCode(anyString());
    }

    @Test
    public void testExecuteDeleteFails() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(false);

        boolean result = deleteProductUseCase.execute(productCode);

        assertFalse(result);
        verify(productRepository).deleteByProductCode(productCode);
    }

    @Test
    public void testExecuteMultipleProducts() {
        String[] productCodes = {"P001", "P002", "P003"};

        for (String code : productCodes) {
            when(productRepository.existsByProductCode(code)).thenReturn(true);
            when(productRepository.deleteByProductCode(code)).thenReturn(true);

            assertTrue(deleteProductUseCase.execute(code));
        }

        verify(productRepository, times(3)).deleteByProductCode(anyString());
    }

    @Test
    public void testExecuteVerifyInteractions() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

        deleteProductUseCase.execute(productCode);

        verify(productRepository, times(1)).existsByProductCode(productCode);
        verify(productRepository, times(1)).deleteByProductCode(productCode);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testExecuteWithLongProductCode() {
        String longCode = "P" + "0".repeat(100);

        when(productRepository.existsByProductCode(longCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(longCode)).thenReturn(true);

        assertTrue(deleteProductUseCase.execute(longCode));
    }

    @Test
    public void testExecuteWithSpecialChars() {
        String productCode = "P-001_A";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

        assertTrue(deleteProductUseCase.execute(productCode));
    }

    @Test
    public void testExecuteRepositoryException() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode))
            .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class,
            () -> deleteProductUseCase.execute(productCode));
    }

    @Test
    public void testExecuteExistsCheckThrowsException() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode))
            .thenThrow(new RuntimeException("Connection error"));

        assertThrows(RuntimeException.class,
            () -> deleteProductUseCase.execute(productCode));

        verify(productRepository, never()).deleteByProductCode(anyString());
    }

    @Test
    public void testExecuteDeleteThrowsException() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode))
            .thenThrow(new RuntimeException("Delete failed"));

        assertThrows(RuntimeException.class,
            () -> deleteProductUseCase.execute(productCode));
    }

    @Test
    public void testExecuteSequentialDeletions() {
        for (int i = 1; i <= 10; i++) {
            String productCode = "P" + String.format("%03d", i);
            when(productRepository.existsByProductCode(productCode)).thenReturn(true);
            when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

            assertTrue(deleteProductUseCase.execute(productCode));
        }
    }

    @Test
    public void testExecutePartialSuccess() {
        when(productRepository.existsByProductCode("P001")).thenReturn(true);
        when(productRepository.deleteByProductCode("P001")).thenReturn(true);

        when(productRepository.existsByProductCode("P002")).thenReturn(false);

        assertTrue(deleteProductUseCase.execute("P001"));
        assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute("P002"));
    }

    @Test
    public void testExecuteWithUnicode() {
        String productCode = "产品001";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

        assertTrue(deleteProductUseCase.execute(productCode));
    }

    @Test
    public void testExecuteWithNumericCode() {
        String productCode = "12345";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

        assertTrue(deleteProductUseCase.execute(productCode));
    }

    @Test
    public void testExecuteReturnValue() {
        String productCode = "P001";

        when(productRepository.existsByProductCode(productCode)).thenReturn(true);
        when(productRepository.deleteByProductCode(productCode)).thenReturn(true);

        boolean result = deleteProductUseCase.execute(productCode);

        assertEquals(true, result);
    }

    @Test
    public void testExecuteExceptionMessage() {
        String productCode = "P999";

        when(productRepository.existsByProductCode(productCode)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute(productCode));

        assertTrue(exception.getMessage().contains("P999"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testExecuteNullProductCodeExceptionMessage() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute(null));

        assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    public void testExecuteEmptyProductCodeExceptionMessage() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> deleteProductUseCase.execute(""));

        assertTrue(exception.getMessage().contains("empty"));
    }
}

