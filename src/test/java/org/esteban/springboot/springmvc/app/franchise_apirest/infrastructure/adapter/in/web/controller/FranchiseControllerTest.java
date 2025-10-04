package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.in.web.controller;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.exception.ResourceNotFoundException;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in.*;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.in.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(FranchiseController.class)
@DisplayName("Franchise Controller Integration Tests")
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @MockBean
    private AddBranchUseCase addBranchUseCase;

    @MockBean
    private AddProductUseCase addProductUseCase;

    @MockBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockBean
    private UpdateProductStockUseCase updateProductStockUseCase;

    @MockBean
    private GetTopProductsByBranchUseCase getTopProductsByBranchUseCase;

    @MockBean
    private GetFranchiseUseCase getFranchiseUseCase;

    @MockBean
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @MockBean
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @MockBean
    private UpdateProductNameUseCase updateProductNameUseCase;

    private Franchise testFranchise;

    @BeforeEach
    void setUp() {
        testFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Tech Store")
                .build();
    }

    @Test
    @DisplayName("POST /api/franchises - Should create franchise")
    void shouldCreateFranchise() {
        // Given
        FranchiseRequest request = new FranchiseRequest("New Franchise");
        when(createFranchiseUseCase.createFranchise(anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("franchise-1")
                .jsonPath("$.name").isEqualTo("Tech Store");
    }

    @Test
    @DisplayName("POST /api/franchises - Should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() {
        // Given
        FranchiseRequest request = new FranchiseRequest("");

        // When & Then
        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/franchises/{franchiseId}/branches - Should add branch")
    void shouldAddBranch() {
        // Given
        BranchRequest request = new BranchRequest("New Branch");
        Branch branch = Branch.builder().id("branch-1").name("New Branch").build();
        testFranchise.addBranch(branch);

        when(addBranchUseCase.addBranch(anyString(), anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.post()
                .uri("/api/franchises/franchise-1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.branches").isArray()
                .jsonPath("$.branches[0].name").isEqualTo("New Branch");
    }

    @Test
    @DisplayName("POST /api/franchises/{franchiseId}/branches/{branchId}/products - Should add product")
    void shouldAddProduct() {
        // Given
        ProductRequest request = new ProductRequest("Laptop", 50);
        Branch branch = Branch.builder().id("branch-1").name("Branch").build();
        Product product = Product.builder().id("prod-1").name("Laptop").stock(50).build();
        branch.addProduct(product);
        testFranchise.addBranch(branch);

        when(addProductUseCase.addProduct(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.post()
                .uri("/api/franchises/franchise-1/branches/branch-1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.branches[0].products[0].name").isEqualTo("Laptop")
                .jsonPath("$.branches[0].products[0].stock").isEqualTo(50);
    }

    @Test
    @DisplayName("POST /api/franchises/.../products - Should return 400 when stock is negative")
    void shouldReturn400WhenStockIsNegative() {
        // Given
        ProductRequest request = new ProductRequest("Invalid Product", -10);

        // When & Then
        webTestClient.post()
                .uri("/api/franchises/franchise-1/branches/branch-1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("DELETE /api/franchises/.../products/{productId} - Should delete product")
    void shouldDeleteProduct() {
        // Given
        when(deleteProductUseCase.deleteProduct(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.delete()
                .uri("/api/franchises/franchise-1/branches/branch-1/products/prod-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("PUT /api/franchises/.../products/{productId}/stock - Should update stock")
    void shouldUpdateStock() {
        // Given
        UpdateStockRequest request = new UpdateStockRequest(200);
        Branch branch = Branch.builder().id("branch-1").name("Branch").build();
        Product product = Product.builder().id("prod-1").name("Laptop").stock(200).build();
        branch.addProduct(product);
        testFranchise.addBranch(branch);

        when(updateProductStockUseCase.updateProductStock(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.put()
                .uri("/api/franchises/franchise-1/branches/branch-1/products/prod-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].products[0].stock").isEqualTo(200);
    }

    @Test
    @DisplayName("GET /api/franchises/{franchiseId}/top-products - Should get top products")
    void shouldGetTopProducts() {
        // Given
        Branch branch = Branch.builder().id("branch-1").name("Branch").build();
        Product product = Product.builder().id("prod-1").name("Laptop").stock(100).build();

        when(getTopProductsByBranchUseCase.getTopProductsByBranch(anyString()))
                .thenReturn(Flux.just(new AbstractMap.SimpleEntry<>(branch, product)));

        // When & Then
        webTestClient.get()
                .uri("/api/franchises/franchise-1/top-products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TopProductResponse.class)
                .hasSize(1)
                .value(list -> {
                    TopProductResponse response = list.get(0);
                    assert response.getBranchId().equals("branch-1");
                    assert response.getProductName().equals("Laptop");
                    assert response.getStock() == 100;
                });
    }

    @Test
    @DisplayName("GET /api/franchises/{franchiseId} - Should get franchise by id")
    void shouldGetFranchiseById() {
        // Given
        when(getFranchiseUseCase.getFranchiseById(anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.get()
                .uri("/api/franchises/franchise-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("franchise-1")
                .jsonPath("$.name").isEqualTo("Tech Store");
    }

    @Test
    @DisplayName("GET /api/franchises/{franchiseId} - Should return 404 when not found")
    void shouldReturn404WhenFranchiseNotFound() {
        // Given
        when(getFranchiseUseCase.getFranchiseById(anyString()))
                .thenReturn(Mono.error(new ResourceNotFoundException("Franchise not found")));

        // When & Then
        webTestClient.get()
                .uri("/api/franchises/non-existent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /api/franchises - Should get all franchises")
    void shouldGetAllFranchises() {
        // Given
        Franchise franchise2 = Franchise.builder()
                .id("franchise-2")
                .name("Another Store")
                .build();

        when(getFranchiseUseCase.getAllFranchises())
                .thenReturn(Flux.just(testFranchise, franchise2));

        // When & Then
        webTestClient.get()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Franchise.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("PATCH /api/franchises/{franchiseId}/name - Should update franchise name")
    void shouldUpdateFranchiseName() {
        // Given
        UpdateNameRequest request = new UpdateNameRequest("Updated Name");
        testFranchise.setName("Updated Name");

        when(updateFranchiseNameUseCase.updateFranchiseName(anyString(), anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.patch()
                .uri("/api/franchises/franchise-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("PATCH /api/franchises/.../branches/{branchId}/name - Should update branch name")
    void shouldUpdateBranchName() {
        // Given
        UpdateNameRequest request = new UpdateNameRequest("Updated Branch");
        Branch branch = Branch.builder().id("branch-1").name("Updated Branch").build();
        testFranchise.addBranch(branch);

        when(updateBranchNameUseCase.updateBranchName(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.patch()
                .uri("/api/franchises/franchise-1/branches/branch-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].name").isEqualTo("Updated Branch");
    }

    @Test
    @DisplayName("PATCH /api/franchises/.../products/{productId}/name - Should update product name")
    void shouldUpdateProductName() {
        // Given
        UpdateNameRequest request = new UpdateNameRequest("Updated Product");
        Branch branch = Branch.builder().id("branch-1").name("Branch").build();
        Product product = Product.builder().id("prod-1").name("Updated Product").stock(50).build();
        branch.addProduct(product);
        testFranchise.addBranch(branch);

        when(updateProductNameUseCase.updateProductName(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(testFranchise));

        // When & Then
        webTestClient.patch()
                .uri("/api/franchises/franchise-1/branches/branch-1/products/prod-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branches[0].products[0].name").isEqualTo("Updated Product");
    }
}
