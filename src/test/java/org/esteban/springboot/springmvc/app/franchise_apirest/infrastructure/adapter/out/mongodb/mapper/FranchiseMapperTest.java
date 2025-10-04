package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.mapper;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.BranchEntity;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.FranchiseEntity;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Franchise Mapper Tests")
class FranchiseMapperTest {

    private FranchiseMapper franchiseMapper;

    @BeforeEach
    void setUp() {
        franchiseMapper = new FranchiseMapper();
    }

    @Test
    @DisplayName("Should map domain Franchise to FranchiseEntity")
    void shouldMapDomainFranchiseToEntity() {
        // Given
        Product product = Product.builder()
                .id("prod-1")
                .name("Laptop")
                .stock(50)
                .build();

        Branch branch = Branch.builder()
                .id("branch-1")
                .name("Main Branch")
                .build();
        branch.addProduct(product);

        Franchise franchise = Franchise.builder()
                .id("franchise-1")
                .name("Tech Store")
                .build();
        franchise.addBranch(branch);

        // When
        FranchiseEntity entity = franchiseMapper.toEntity(franchise);

        // Then
        assertNotNull(entity);
        assertEquals(franchise.getId(), entity.getId());
        assertEquals(franchise.getName(), entity.getName());
        assertEquals(1, entity.getBranches().size());

        BranchEntity branchEntity = entity.getBranches().get(0);
        assertEquals(branch.getId(), branchEntity.getId());
        assertEquals(branch.getName(), branchEntity.getName());
        assertEquals(1, branchEntity.getProducts().size());

        ProductEntity productEntity = branchEntity.getProducts().get(0);
        assertEquals(product.getId(), productEntity.getId());
        assertEquals(product.getName(), productEntity.getName());
        assertEquals(product.getStock(), productEntity.getStock());
    }

    @Test
    @DisplayName("Should map FranchiseEntity to domain Franchise")
    void shouldMapEntityToDomainFranchise() {
        // Given
        ProductEntity productEntity = ProductEntity.builder()
                .id("prod-1")
                .name("Mouse")
                .stock(100)
                .build();

        BranchEntity branchEntity = BranchEntity.builder()
                .id("branch-1")
                .name("Downtown")
                .build();
        branchEntity.getProducts().add(productEntity);

        FranchiseEntity entity = FranchiseEntity.builder()
                .id("franchise-1")
                .name("Electronics Store")
                .build();
        entity.getBranches().add(branchEntity);

        // When
        Franchise franchise = franchiseMapper.toDomain(entity);

        // Then
        assertNotNull(franchise);
        assertEquals(entity.getId(), franchise.getId());
        assertEquals(entity.getName(), franchise.getName());
        assertEquals(1, franchise.getBranches().size());

        Branch branch = franchise.getBranches().get(0);
        assertEquals(branchEntity.getId(), branch.getId());
        assertEquals(branchEntity.getName(), branch.getName());
        assertEquals(1, branch.getProducts().size());

        Product product = branch.getProducts().get(0);
        assertEquals(productEntity.getId(), product.getId());
        assertEquals(productEntity.getName(), product.getName());
        assertEquals(productEntity.getStock(), product.getStock());
    }

    @Test
    @DisplayName("Should handle null franchise when mapping to entity")
    void shouldHandleNullFranchiseWhenMappingToEntity() {
        // When
        FranchiseEntity entity = franchiseMapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    @DisplayName("Should handle null entity when mapping to domain")
    void shouldHandleNullEntityWhenMappingToDomain() {
        // When
        Franchise franchise = franchiseMapper.toDomain(null);

        // Then
        assertNull(franchise);
    }

    @Test
    @DisplayName("Should map franchise with empty branches")
    void shouldMapFranchiseWithEmptyBranches() {
        // Given
        Franchise franchise = Franchise.builder()
                .id("franchise-1")
                .name("Empty Store")
                .build();

        // When
        FranchiseEntity entity = franchiseMapper.toEntity(franchise);

        // Then
        assertNotNull(entity);
        assertNotNull(entity.getBranches());
        assertTrue(entity.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should map franchise with multiple branches and products")
    void shouldMapFranchiseWithMultipleBranchesAndProducts() {
        // Given
        Product product1 = Product.builder().id("p1").name("Product1").stock(10).build();
        Product product2 = Product.builder().id("p2").name("Product2").stock(20).build();

        Branch branch1 = Branch.builder().id("b1").name("Branch1").build();
        branch1.addProduct(product1);

        Branch branch2 = Branch.builder().id("b2").name("Branch2").build();
        branch2.addProduct(product2);

        Franchise franchise = Franchise.builder()
                .id("f1")
                .name("Multi Store")
                .build();
        franchise.addBranch(branch1);
        franchise.addBranch(branch2);

        // When
        FranchiseEntity entity = franchiseMapper.toEntity(franchise);

        // Then
        assertNotNull(entity);
        assertEquals(2, entity.getBranches().size());
        assertEquals(1, entity.getBranches().get(0).getProducts().size());
        assertEquals(1, entity.getBranches().get(1).getProducts().size());
    }
}
