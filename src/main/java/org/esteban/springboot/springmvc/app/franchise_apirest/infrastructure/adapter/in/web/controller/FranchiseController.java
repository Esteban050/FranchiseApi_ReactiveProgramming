package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in.*;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.in.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetTopProductsByBranchUseCase getTopProductsByBranchUseCase;
    private final GetFranchiseUseCase getFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> createFranchise(@Valid @RequestBody FranchiseRequest request) {
        return createFranchiseUseCase.createFranchise(request.getName());
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addBranch(
            @PathVariable String franchiseId,
            @Valid @RequestBody BranchRequest request) {
        return addBranchUseCase.addBranch(franchiseId, request.getName());
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody ProductRequest request) {
        return addProductUseCase.addProduct(franchiseId, branchId, request.getName(), request.getStock());
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Franchise> deleteProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId) {
        return deleteProductUseCase.deleteProduct(franchiseId, branchId, productId);
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<Franchise> updateProductStock(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateStockRequest request) {
        return updateProductStockUseCase.updateProductStock(franchiseId, branchId, productId, request.getStock());
    }

    @GetMapping("/{franchiseId}/top-products")
    public Flux<TopProductResponse> getTopProductsByBranch(@PathVariable String franchiseId) {
        return getTopProductsByBranchUseCase.getTopProductsByBranch(franchiseId)
                .map(entry -> TopProductResponse.builder()
                        .branchId(entry.getKey().getId())
                        .branchName(entry.getKey().getName())
                        .productId(entry.getValue().getId())
                        .productName(entry.getValue().getName())
                        .stock(entry.getValue().getStock())
                        .build());
    }

    @GetMapping("/{franchiseId}")
    public Mono<Franchise> getFranchiseById(@PathVariable String franchiseId) {
        return getFranchiseUseCase.getFranchiseById(franchiseId);
    }

    @GetMapping
    public Flux<Franchise> getAllFranchises() {
        return getFranchiseUseCase.getAllFranchises();
    }

    @PatchMapping("/{franchiseId}/name")
    public Mono<Franchise> updateFranchiseName(
            @PathVariable String franchiseId,
            @Valid @RequestBody UpdateNameRequest request) {
        return updateFranchiseNameUseCase.updateFranchiseName(franchiseId, request.getName());
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/name")
    public Mono<Franchise> updateBranchName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody UpdateNameRequest request) {
        return updateBranchNameUseCase.updateBranchName(franchiseId, branchId, request.getName());
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    public Mono<Franchise> updateProductName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateNameRequest request) {
        return updateProductNameUseCase.updateProductName(franchiseId, branchId, productId, request.getName());
    }
}
