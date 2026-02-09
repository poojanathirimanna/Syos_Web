package com.syos.web.application.usecases;

import com.syos.web.application.dto.AddToCartRequest;
import com.syos.web.domain.model.Product;
import com.syos.web.domain.model.ShoppingCart;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddToCartUseCaseTest {

    @Test
    public void testExecuteSuccess() throws SQLException {
        try (MockedConstruction<ProductDao> productDaoMock = mockConstruction(ProductDao.class, (mock, context) -> {
            Product product = new Product();
            product.setProductCode("P001");
            product.setWebsiteQuantity(10);
            product.setDeleted(false);
            when(mock.findByCode("P001")).thenReturn(Optional.of(product));
        });
             MockedConstruction<ShoppingCartDao> cartDaoMock = mockConstruction(ShoppingCartDao.class, (mock, context) -> {
                 when(mock.addToCart(anyString(), anyString(), anyInt())).thenReturn(new ShoppingCart());
             })) {

            AddToCartUseCase useCase = new AddToCartUseCase();
            AddToCartRequest request = new AddToCartRequest("P001", 5);
            
            ShoppingCart result = useCase.execute("user1", request);
            
            assertNotNull(result);
            verify(productDaoMock.constructed().get(0)).findByCode("P001");
            verify(cartDaoMock.constructed().get(0)).addToCart("user1", "P001", 5);
        }
    }

    @Test
    public void testExecuteProductNotFound() throws SQLException {
        try (MockedConstruction<ProductDao> productDaoMock = mockConstruction(ProductDao.class, (mock, context) -> {
            when(mock.findByCode("P001")).thenReturn(Optional.empty());
        });
             MockedConstruction<ShoppingCartDao> cartDaoMock = mockConstruction(ShoppingCartDao.class)) {

            AddToCartUseCase useCase = new AddToCartUseCase();
            AddToCartRequest request = new AddToCartRequest("P001", 5);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute("user1", request));
            assertEquals("Product not found: P001", exception.getMessage());
        }
    }

    @Test
    public void testExecuteProductDeleted() throws SQLException {
        try (MockedConstruction<ProductDao> productDaoMock = mockConstruction(ProductDao.class, (mock, context) -> {
            Product product = new Product();
            product.setDeleted(true);
            when(mock.findByCode("P001")).thenReturn(Optional.of(product));
        });
             MockedConstruction<ShoppingCartDao> cartDaoMock = mockConstruction(ShoppingCartDao.class)) {

            AddToCartUseCase useCase = new AddToCartUseCase();
            AddToCartRequest request = new AddToCartRequest("P001", 5);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute("user1", request));
            assertEquals("Product is no longer available", exception.getMessage());
        }
    }

    @Test
    public void testExecuteOutOfStock() throws SQLException {
        try (MockedConstruction<ProductDao> productDaoMock = mockConstruction(ProductDao.class, (mock, context) -> {
            Product product = new Product();
            product.setWebsiteQuantity(0);
            product.setDeleted(false);
            when(mock.findByCode("P001")).thenReturn(Optional.of(product));
        });
             MockedConstruction<ShoppingCartDao> cartDaoMock = mockConstruction(ShoppingCartDao.class)) {

            AddToCartUseCase useCase = new AddToCartUseCase();
            AddToCartRequest request = new AddToCartRequest("P001", 5);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute("user1", request));
            assertEquals("Product is out of stock", exception.getMessage());
        }
    }

    @Test
    public void testExecuteInsufficientStock() throws SQLException {
        try (MockedConstruction<ProductDao> productDaoMock = mockConstruction(ProductDao.class, (mock, context) -> {
            Product product = new Product();
            product.setWebsiteQuantity(3);
            product.setDeleted(false);
            when(mock.findByCode("P001")).thenReturn(Optional.of(product));
        });
             MockedConstruction<ShoppingCartDao> cartDaoMock = mockConstruction(ShoppingCartDao.class)) {

            AddToCartUseCase useCase = new AddToCartUseCase();
            AddToCartRequest request = new AddToCartRequest("P001", 5);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute("user1", request));
            assertEquals("Only 3 units available. Requested: 5", exception.getMessage());
        }
    }
}
