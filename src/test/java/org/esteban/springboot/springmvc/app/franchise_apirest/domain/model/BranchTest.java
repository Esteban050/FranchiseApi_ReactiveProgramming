package org.esteban.springboot.springmvc.app.franchise_apirest.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Branch Domain Model Tests")
class BranchTest {

    private Branch branch;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id("branch-1")
                .name("Main Branch")
                .build();

        product1 = Product.builder()
                .id("prod-1")
                .name("Laptop")
                .stock(50)
                .build();

        product2 = Product.builder()
                .id("prod-2")
                .name("Mouse")
                .stock(100)
                .build();
    }

    @Test
    @DisplayName("Should add product to branch")
    void shouldAddProductToBranch() {
        // When
        branch.addProduct(product1);

        // Then
        assertEquals(1, branch.getProducts().size());
        assertTrue(branch.getProducts().contains(product1));
    }

    @Test
    @DisplayName("Should remove product from branch")
    void shouldRemoveProductFromBranch() {
        // Given
        branch.addProduct(product1);
        branch.addProduct(product2);

        // When
        boolean removed = branch.removeProduct("prod-1");

        // Then
        assertTrue(removed);
        assertEquals(1, branch.getProducts().size());
        assertFalse(branch.getProducts().contains(product1));
    }

    @Test
    @DisplayName("Should return false when removing non-existent product")
    void shouldReturnFalseWhenRemovingNonExistentProduct() {
        // Given
        branch.addProduct(product1);

        // When
        boolean removed = branch.removeProduct("non-existent-id");

        // Then
        assertFalse(removed);
        assertEquals(1, branch.getProducts().size());
    }

    @Test
    @DisplayName("Should find product by id")
    void shouldFindProductById() {
        // Given
        branch.addProduct(product1);
        branch.addProduct(product2);

        // When
        Optional<Product> found = branch.findProductById("prod-1");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when product not found")
    void shouldReturnEmptyWhenProductNotFound() {
        // Given
        branch.addProduct(product1);

        // When
        Optional<Product> found = branch.findProductById("non-existent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should get product with max stock")
    void shouldGetProductWithMaxStock() {
        // Given
        Product product3 = Product.builder()
                .id("prod-3")
                .name("Keyboard")
                .stock(200)
                .build();

        branch.addProduct(product1);  // stock: 50
        branch.addProduct(product2);  // stock: 100
        branch.addProduct(product3);  // stock: 200

        // When
        Optional<Product> maxStockProduct = branch.getProductWithMaxStock();

        // Then
        assertTrue(maxStockProduct.isPresent());
        assertEquals("Keyboard", maxStockProduct.get().getName());
        assertEquals(200, maxStockProduct.get().getStock());
    }

    @Test
    @DisplayName("Should return empty when no products in branch")
    void shouldReturnEmptyWhenNoProductsInBranch() {
        // When
        Optional<Product> maxStockProduct = branch.getProductWithMaxStock();

        // Then
        assertFalse(maxStockProduct.isPresent());
    }

    @Test
    @DisplayName("Should get product with max stock when multiple products have same max")
    void shouldGetProductWithMaxStockWhenMultipleProductsHaveSameMax() {
        // Given
        Product product3 = Product.builder()
                .id("prod-3")
                .name("Keyboard")
                .stock(100)
                .build();

        branch.addProduct(product2);  // stock: 100
        branch.addProduct(product3);  // stock: 100

        // When
        Optional<Product> maxStockProduct = branch.getProductWithMaxStock();

        // Then
        assertTrue(maxStockProduct.isPresent());
        assertEquals(100, maxStockProduct.get().getStock());
    }
}
