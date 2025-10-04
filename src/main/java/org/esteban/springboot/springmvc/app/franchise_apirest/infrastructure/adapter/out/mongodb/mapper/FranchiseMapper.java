package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.mapper;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.BranchEntity;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.FranchiseEntity;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FranchiseMapper {

    public FranchiseEntity toEntity(Franchise domain) {
        if (domain == null) return null;

        return FranchiseEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .branches(domain.getBranches().stream()
                        .map(this::toBranchEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public Franchise toDomain(FranchiseEntity entity) {
        if (entity == null) return null;

        return Franchise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .branches(entity.getBranches().stream()
                        .map(this::toBranchDomain)
                        .collect(Collectors.toList()))
                .build();
    }

    private BranchEntity toBranchEntity(Branch domain) {
        return BranchEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .products(domain.getProducts().stream()
                        .map(this::toProductEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    private Branch toBranchDomain(BranchEntity entity) {
        return Branch.builder()
                .id(entity.getId())
                .name(entity.getName())
                .products(entity.getProducts().stream()
                        .map(this::toProductDomain)
                        .collect(Collectors.toList()))
                .build();
    }

    private ProductEntity toProductEntity(Product domain) {
        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .stock(domain.getStock())
                .build();
    }

    private Product toProductDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .stock(entity.getStock())
                .build();
    }
}
