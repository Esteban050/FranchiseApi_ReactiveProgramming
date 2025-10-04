package org.esteban.springboot.springmvc.app.franchise_apirest.application.service;

import lombok.RequiredArgsConstructor;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.exception.ResourceNotFoundException;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in.*;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.out.FranchiseRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FranchiseService implements
        CreateFranchiseUseCase,
        AddBranchUseCase,
        AddProductUseCase,
        DeleteProductUseCase,
        UpdateProductStockUseCase,
        GetTopProductsByBranchUseCase,
        GetFranchiseUseCase,
        UpdateFranchiseNameUseCase,
        UpdateBranchNameUseCase,
        UpdateProductNameUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;

    @Override
    public Mono<Franchise> createFranchise(String name) {
        Franchise franchise = Franchise.builder()
                .name(name)
                .build();
        return franchiseRepositoryPort.save(franchise);
    }

    @Override
    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = Branch.builder()
                            .id(UUID.randomUUID().toString())
                            .name(branchName)
                            .build();
                    franchise.addBranch(branch);
                    return franchiseRepositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, Integer stock) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

                    Product product = Product.builder()
                            .id(UUID.randomUUID().toString())
                            .name(productName)
                            .stock(stock)
                            .build();

                    branch.addProduct(product);
                    return franchiseRepositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> deleteProduct(String franchiseId, String branchId, String productId) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

                    boolean removed = branch.removeProduct(productId);
                    if (!removed) {
                        return Mono.error(new ResourceNotFoundException("Product not found with id: " + productId));
                    }

                    return franchiseRepositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, Integer newStock) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

                    Product product = branch.findProductById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

                    product.updateStock(newStock);
                    return franchiseRepositoryPort.save(franchise);
                });
    }

    @Override
    public Flux<Map.Entry<Branch, Product>> getTopProductsByBranch(String franchiseId) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches())
                        .flatMap(branch -> branch.getProductWithMaxStock()
                                .map(product -> new AbstractMap.SimpleEntry<>(branch, product))
                                .map(Mono::just)
                                .orElse(Mono.empty())
                        )
                );
    }

    @Override
    public Mono<Franchise> getFranchiseById(String franchiseId) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)));
    }

    @Override
    public Flux<Franchise> getAllFranchises() {
        return franchiseRepositoryPort.findAll();
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    franchise.setName(newName);
                    return franchiseRepositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateBranchName(String franchiseId, String branchId, String newName) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

                    branch.setName(newName);
                    return franchiseRepositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId, String newName) {
        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

                    Product product = branch.findProductById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

                    product.setName(newName);
                    return franchiseRepositoryPort.save(franchise);
                });
    }
}
