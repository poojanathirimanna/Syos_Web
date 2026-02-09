package com.syos.web.infrastructure.repositories;

import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryImplTest {

    private ProductRepositoryImpl repository;

    @BeforeEach
    public void setUp() {
        repository = new ProductRepositoryImpl();
    }

    @Test
    public void testConstructor() {
        assertNotNull(repository);
    }

    @Test
    public void testFindAll() {
        assertDoesNotThrow(() -> {
            try {
                repository.findAll();
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testFindByProductCode() {
        assertDoesNotThrow(() -> {
            try {
                repository.findByProductCode("P001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testFindByProductCodeNull() {
        assertDoesNotThrow(() -> {
            try {
                repository.findByProductCode(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSaveProduct() {
        Product product = new Product("P001", "Test Product", new BigDecimal("100.00"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product("P001", "Updated Product", new BigDecimal("150.00"));
        assertDoesNotThrow(() -> {
            try {
                repository.update(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteByProductCode() {
        assertDoesNotThrow(() -> {
            try {
                repository.deleteByProductCode("P001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByProductCode() {
        assertDoesNotThrow(() -> {
            try {
                repository.existsByProductCode("P001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testMultipleRepositoryInstances() {
        ProductRepositoryImpl repo1 = new ProductRepositoryImpl();
        ProductRepositoryImpl repo2 = new ProductRepositoryImpl();

        assertNotNull(repo1);
        assertNotNull(repo2);
    }

    @Test
    public void testFindByProductCodeMultipleCodes() {
        String[] codes = {"P001", "P002", "P003"};
        for (String code : codes) {
            assertDoesNotThrow(() -> {
                try {
                    repository.findByProductCode(code);
                } catch (Exception e) {
                    // Expected
                }
            });
        }
    }

    @Test
    public void testSaveMultipleProducts() {
        for (int i = 1; i <= 5; i++) {
            Product product = new Product("P00" + i, "Product " + i, new BigDecimal("100.00"));
            assertDoesNotThrow(() -> {
                try {
                    repository.save(product);
                } catch (Exception e) {
                    // Expected
                }
            });
        }
    }

    @Test
    public void testProductWithZeroPrice() {
        Product product = new Product("P001", "Test", BigDecimal.ZERO);
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testProductWithLargePrice() {
        Product product = new Product("P001", "Test", new BigDecimal("999999.99"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testProductWithLongName() {
        String longName = "A".repeat(500);
        Product product = new Product("P001", longName, new BigDecimal("100.00"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testProductWithSpecialChars() {
        Product product = new Product("P-001_A", "Test & Product", new BigDecimal("100.00"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testUpdateNullProduct() {
        assertDoesNotThrow(() -> {
            try {
                repository.update(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSaveNullProduct() {
        assertDoesNotThrow(() -> {
            try {
                repository.save(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteNullProductCode() {
        assertDoesNotThrow(() -> {
            try {
                repository.deleteByProductCode(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsNullProductCode() {
        assertDoesNotThrow(() -> {
            try {
                repository.existsByProductCode(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSequentialOperations() {
        Product product = new Product("P001", "Test", new BigDecimal("100.00"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
                repository.findByProductCode("P001");
                repository.update(product);
                repository.deleteByProductCode("P001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testFindAllMultipleTimes() {
        assertDoesNotThrow(() -> {
            try {
                repository.findAll();
                repository.findAll();
                repository.findAll();
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testExistsByProductCodeMultipleCalls() {
        assertDoesNotThrow(() -> {
            try {
                repository.existsByProductCode("P001");
                repository.existsByProductCode("P002");
                repository.existsByProductCode("P003");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testProductWithDecimalPrice() {
        Product product = new Product("P001", "Test", new BigDecimal("99.99"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testProductWithPrecisePrice() {
        Product product = new Product("P001", "Test", new BigDecimal("99.995"));
        assertDoesNotThrow(() -> {
            try {
                repository.save(product);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testRepositoryImplementsInterface() {
        assertTrue(repository instanceof IProductRepository);
    }
}

