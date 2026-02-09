package com.syos.web.application.usecases;

import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.dto.UpdateProductRequest;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateProductUseCaseTest {

    @Mock
    private IProductRepository productRepository;

    private UpdateProductUseCase updateProductUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        updateProductUseCase = new UpdateProductUseCase(productRepository);
    }

    @Test
    public void testExecuteSuccess() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Updated Product");
        request.setUnitPrice(new BigDecimal("150.00"));

        Product existingProduct = new Product("P001", "Old Product", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "Updated Product", new BigDecimal("150.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);

        assertNotNull(result);
        assertEquals("P001", result.getProductCode());
        assertEquals("Updated Product", result.getName());
        verify(productRepository).findByProductCode("P001");
        verify(productRepository).update(any(Product.class));
    }

    @Test
    public void testExecuteProductNotFound() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P999");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
        verify(productRepository, never()).update(any(Product.class));
    }

    @Test
    public void testExecuteWithImageUrl() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setImageUrl("http://new-image.jpg");

        Product existingProduct = new Product("P001", "Product", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("100.00"));
        updatedProduct.setImageUrl("http://new-image.jpg");

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteWithCategoryId() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(5);

        Product existingProduct = new Product("P001", "Product", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("100.00"));
        updatedProduct.setCategoryId(5);

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteNullProductCode() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode(null);
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteEmptyProductCode() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteNullName() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName(null);
        request.setUnitPrice(new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteNullPrice() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(null);

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteZeroPrice() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteNegativePrice() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("-10.00"));

        assertThrows(IllegalArgumentException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteRepositoryUpdateFails() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));

        Product existingProduct = new Product("P001", "Product", new BigDecimal("50.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> updateProductUseCase.execute(request));
    }

    @Test
    public void testExecuteMultipleUpdates() {
        for (int i = 1; i <= 5; i++) {
            UpdateProductRequest request = new UpdateProductRequest();
            request.setProductCode("P00" + i);
            request.setName("Product " + i);
            request.setUnitPrice(new BigDecimal(i * 10 + ".00"));

            Product existingProduct = new Product("P00" + i, "Old", new BigDecimal("50.00"));
            Product updatedProduct = new Product("P00" + i, "Product " + i, new BigDecimal(i * 10 + ".00"));

            when(productRepository.findByProductCode("P00" + i)).thenReturn(Optional.of(existingProduct));
            when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

            ProductDTO result = updateProductUseCase.execute(request);
            assertNotNull(result);
        }
    }

    @Test
    public void testExecuteWithNullImageUrl() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setImageUrl(null);

        Product existingProduct = new Product("P001", "Product", new BigDecimal("50.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteWithNullCategoryId() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(null);

        Product existingProduct = new Product("P001", "Product", new BigDecimal("50.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteVerifyInteractions() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("100.00"));

        Product existingProduct = new Product("P001", "Old", new BigDecimal("50.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        updateProductUseCase.execute(request);

        verify(productRepository, times(1)).findByProductCode("P001");
        verify(productRepository, times(1)).update(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testExecutePriceIncrease() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("200.00"));

        Product existingProduct = new Product("P001", "Product", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("200.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecutePriceDecrease() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product");
        request.setUnitPrice(new BigDecimal("50.00"));

        Product existingProduct = new Product("P001", "Product", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "Product", new BigDecimal("50.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteNameChange() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("New Name");
        request.setUnitPrice(new BigDecimal("100.00"));

        Product existingProduct = new Product("P001", "Old Name", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "New Name", new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteWithLongName() {
        String longName = "A".repeat(500);
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName(longName);
        request.setUnitPrice(new BigDecimal("100.00"));

        Product existingProduct = new Product("P001", "Short", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", longName, new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteWithSpecialCharsInName() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setProductCode("P001");
        request.setName("Product & Items (50%)");
        request.setUnitPrice(new BigDecimal("100.00"));

        Product existingProduct = new Product("P001", "Old", new BigDecimal("100.00"));
        Product updatedProduct = new Product("P001", "Product & Items (50%)", new BigDecimal("100.00"));

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = updateProductUseCase.execute(request);
        assertNotNull(result);
    }
}

