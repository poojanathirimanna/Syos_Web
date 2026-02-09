package com.syos.web.application.usecases;

import com.syos.web.application.dto.CreateProductRequest;
import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateProductUseCaseTest {

    @Mock
    private IProductRepository productRepository;

    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        createProductUseCase = new CreateProductUseCase(productRepository);
    }

    @Test
    public void testExecuteSuccess() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testExecuteProductCodeExists() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void testExecuteNullProductCode() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode(null);
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteEmptyProductCode() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteNullName() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName(null);
        request.setUnitPrice(new BigDecimal("100.00"));

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteNullUnitPrice() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithImageUrl() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setImageUrl("image.jpg");

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));
        savedProduct.setCategoryId(1);
        savedProduct.setImageUrl("image.jpg");

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteWithSpecialCharacters() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P@001");
        request.setName("Product #1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P@001", "Product #1", new BigDecimal("100.00"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P@001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteWithLongName() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("A".repeat(255));
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "A".repeat(255), new BigDecimal("100.00"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteWithLargePrice() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("999999.99"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("999999.99"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteWithNullCategoryId() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(null);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testExecuteWithVerySmallPrice() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("0.01"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("0.01"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);

        assertNotNull(result);
    }

    @Test
    public void testVerifyRepositoryInteractions() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        createProductUseCase.execute(request);

        verify(productRepository, times(1)).existsByProductCode("P001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testExecuteWithZeroPrice() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(BigDecimal.ZERO);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithNegativePrice() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("-50.00"));

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithWhitespaceProductCode() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("   ");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithWhitespaceName() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("   ");
        request.setUnitPrice(new BigDecimal("100.00"));

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithUnicodeProductName() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("产品 Продукт مُنتَج");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "产品 Продукт مُنتَج", new BigDecimal("100.00"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteWithPrecisePrice() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("99.999999"));
        request.setCategoryId(1);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("99.999999"));
        savedProduct.setCategoryId(1);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteRepositorySaveFails() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> createProductUseCase.execute(request));
    }

    @Test
    public void testExecuteRepositoryExistsCheckFails() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));

        when(productRepository.existsByProductCode("P001")).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> createProductUseCase.execute(request));
    }

    @Test
    public void testExecuteMultipleCalls() {
        CreateProductRequest request1 = new CreateProductRequest();
        request1.setProductCode("P001");
        request1.setName("Product 1");
        request1.setUnitPrice(new BigDecimal("100.00"));

        CreateProductRequest request2 = new CreateProductRequest();
        request2.setProductCode("P002");
        request2.setName("Product 2");
        request2.setUnitPrice(new BigDecimal("200.00"));

        Product savedProduct1 = new Product("P001", "Product 1", new BigDecimal("100.00"));
        Product savedProduct2 = new Product("P002", "Product 2", new BigDecimal("200.00"));

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.existsByProductCode("P002")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct1, savedProduct2);

        ProductDTO result1 = createProductUseCase.execute(request1);
        ProductDTO result2 = createProductUseCase.execute(request2);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    public void testExecuteWithCategoryIdZero() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(0);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithNegativeCategoryId() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(-1);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> createProductUseCase.execute(request));
        assertNotNull(exception);
    }

    @Test
    public void testExecuteWithLargeCategoryId() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCategoryId(99999);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));
        savedProduct.setCategoryId(99999);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteWithEmptyImageUrl() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setImageUrl("");

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));
        savedProduct.setImageUrl("");

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);
        assertNotNull(result);
    }

    @Test
    public void testExecuteWithLongImageUrl() {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductCode("P001");
        request.setName("Product 1");
        request.setUnitPrice(new BigDecimal("100.00"));
        String longUrl = "https://example.com/" + "a".repeat(500) + ".jpg";
        request.setImageUrl(longUrl);

        Product savedProduct = new Product("P001", "Product 1", new BigDecimal("100.00"));
        savedProduct.setImageUrl(longUrl);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = createProductUseCase.execute(request);
        assertNotNull(result);
    }
}

