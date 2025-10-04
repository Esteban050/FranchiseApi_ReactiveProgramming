package org.esteban.springboot.springmvc.app.franchise_apirest.application.service;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.exception.ResourceNotFoundException;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.out.FranchiseRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Franchise Service Tests")
class FranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @InjectMocks
    private FranchiseService franchiseService;

    private Franchise testFranchise;
    private Branch testBranch;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("prod-1")
                .name("Laptop")
                .stock(50)
                .build();

        testBranch = Branch.builder()
                .id("branch-1")
                .name("Main Branch")
                .build();
        testBranch.addProduct(testProduct);

        testFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Tech Store")
                .build();
        testFranchise.addBranch(testBranch);
    }

    @Test
    @DisplayName("Should create franchise successfully")
    void shouldCreateFranchiseSuccessfully() {
        // Given
        String franchiseName = "New Franchise";
        Franchise savedFranchise = Franchise.builder()
                .id("new-id")
                .name(franchiseName)
                .build();

        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        // When
        Mono<Franchise> result = franchiseService.createFranchise(franchiseName);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise ->
                        franchise.getName().equals(franchiseName) &&
                        franchise.getId().equals("new-id")
                )
                .verifyComplete();

        verify(franchiseRepositoryPort, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should add branch to franchise successfully")
    void shouldAddBranchToFranchiseSuccessfully() {
        // Given
        String branchName = "New Branch";
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.addBranch("franchise-1", branchName);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise ->
                        franchise.getBranches().size() == 2 &&
                        franchise.getBranches().stream()
                                .anyMatch(b -> b.getName().equals(branchName))
                )
                .verifyComplete();

        verify(franchiseRepositoryPort, times(1)).findById("franchise-1");
        verify(franchiseRepositoryPort, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should throw exception when franchise not found while adding branch")
    void shouldThrowExceptionWhenFranchiseNotFoundWhileAddingBranch() {
        // Given
        when(franchiseRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // When
        Mono<Franchise> result = franchiseService.addBranch("non-existent", "Branch");

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("Franchise not found")
                )
                .verify();

        verify(franchiseRepositoryPort, times(1)).findById(anyString());
        verify(franchiseRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should add product to branch successfully")
    void shouldAddProductToBranchSuccessfully() {
        // Given
        String productName = "Mouse";
        Integer stock = 100;

        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.addProduct(
                "franchise-1", "branch-1", productName, stock
        );

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.findBranchById("branch-1").orElse(null);
                    return branch != null && branch.getProducts().size() == 2;
                })
                .verifyComplete();

        verify(franchiseRepositoryPort, times(1)).findById("franchise-1");
        verify(franchiseRepositoryPort, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should throw exception when branch not found while adding product")
    void shouldThrowExceptionWhenBranchNotFoundWhileAddingProduct() {
        // Given
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.addProduct(
                "franchise-1", "non-existent-branch", "Product", 50
        );

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("Branch not found")
                )
                .verify();
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Given
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.deleteProduct(
                "franchise-1", "branch-1", "prod-1"
        );

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.findBranchById("branch-1").orElse(null);
                    return branch != null && branch.getProducts().isEmpty();
                })
                .verifyComplete();

        verify(franchiseRepositoryPort, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found while deleting")
    void shouldThrowExceptionWhenProductNotFoundWhileDeleting() {
        // Given
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.deleteProduct(
                "franchise-1", "branch-1", "non-existent-product"
        );

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("Product not found")
                )
                .verify();
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        // Given
        Integer newStock = 200;
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.updateProductStock(
                "franchise-1", "branch-1", "prod-1", newStock
        );

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.findBranchById("branch-1").orElse(null);
                    Product product = branch != null ? branch.findProductById("prod-1").orElse(null) : null;
                    return product != null && product.getStock().equals(newStock);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get top products by branch")
    void shouldGetTopProductsByBranch() {
        // Given
        Product product2 = Product.builder()
                .id("prod-2")
                .name("Mouse")
                .stock(100)
                .build();
        testBranch.addProduct(product2);

        Branch branch2 = Branch.builder()
                .id("branch-2")
                .name("Secondary Branch")
                .build();
        Product product3 = Product.builder()
                .id("prod-3")
                .name("Keyboard")
                .stock(150)
                .build();
        branch2.addProduct(product3);
        testFranchise.addBranch(branch2);

        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));

        // When
        Flux<Map.Entry<Branch, Product>> result = franchiseService.getTopProductsByBranch("franchise-1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(entry ->
                        entry.getKey().getId().equals("branch-1") &&
                        entry.getValue().getStock() == 100
                )
                .expectNextMatches(entry ->
                        entry.getKey().getId().equals("branch-2") &&
                        entry.getValue().getStock() == 150
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get franchise by id")
    void shouldGetFranchiseById() {
        // Given
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.getFranchiseById("franchise-1");

        // Then
        StepVerifier.create(result)
                .expectNext(testFranchise)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw exception when franchise not found by id")
    void shouldThrowExceptionWhenFranchiseNotFoundById() {
        // Given
        when(franchiseRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // When
        Mono<Franchise> result = franchiseService.getFranchiseById("non-existent");

        // Then
        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get all franchises")
    void shouldGetAllFranchises() {
        // Given
        Franchise franchise2 = Franchise.builder()
                .id("franchise-2")
                .name("Another Store")
                .build();

        when(franchiseRepositoryPort.findAll())
                .thenReturn(Flux.just(testFranchise, franchise2));

        // When
        Flux<Franchise> result = franchiseService.getAllFranchises();

        // Then
        StepVerifier.create(result)
                .expectNext(testFranchise)
                .expectNext(franchise2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Given
        String newName = "Updated Franchise";
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.updateFranchiseName("franchise-1", newName);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise -> franchise.getName().equals(newName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchNameSuccessfully() {
        // Given
        String newName = "Updated Branch";
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.updateBranchName(
                "franchise-1", "branch-1", newName
        );

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.findBranchById("branch-1").orElse(null);
                    return branch != null && branch.getName().equals(newName);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update product name successfully")
    void shouldUpdateProductNameSuccessfully() {
        // Given
        String newName = "Updated Product";
        when(franchiseRepositoryPort.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        // When
        Mono<Franchise> result = franchiseService.updateProductName(
                "franchise-1", "branch-1", "prod-1", newName
        );

        // Then
        StepVerifier.create(result)
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.findBranchById("branch-1").orElse(null);
                    Product product = branch != null ? branch.findProductById("prod-1").orElse(null) : null;
                    return product != null && product.getName().equals(newName);
                })
                .verifyComplete();
    }
}
