package org.esteban.springboot.springmvc.app.franchise_apirest.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Model Tests")
class ProductTest {

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProductWithValidData() {
        // Given
        String id = "prod-123";
        String name = "Laptop";
        Integer stock = 50;

        // When
        Product product = Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .build();

        // Then
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(stock, product.getStock());
    }

    @Test
    @DisplayName("Should update stock successfully")
    void shouldUpdateStockSuccessfully() {
        // Given
        Product product = Product.builder()
                .id("prod-1")
                .name("Mouse")
                .stock(100)
                .build();

        // When
        product.updateStock(200);

        // Then
        assertEquals(200, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when updating stock with negative value")
    void shouldThrowExceptionWhenUpdatingStockWithNegativeValue() {
        // Given
        Product product = Product.builder()
                .id("prod-1")
                .name("Keyboard")
                .stock(50)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.updateStock(-10)
        );

        assertEquals("Stock cannot be negative", exception.getMessage());
        assertEquals(50, product.getStock()); // Stock should remain unchanged
    }

    @Test
    @DisplayName("Should update stock to zero")
    void shouldUpdateStockToZero() {
        // Given
        Product product = Product.builder()
                .id("prod-1")
                .name("Monitor")
                .stock(25)
                .build();

        // When
        product.updateStock(0);

        // Then
        assertEquals(0, product.getStock());
    }
}