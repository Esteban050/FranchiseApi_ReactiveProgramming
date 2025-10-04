package org.esteban.springboot.springmvc.app.franchise_apirest.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Franchise Domain Model Tests")
class FranchiseTest {

    private Franchise franchise;
    private Branch branch1;
    private Branch branch2;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id("franchise-1")
                .name("Tech Store")
                .build();

        branch1 = Branch.builder()
                .id("branch-1")
                .name("Downtown")
                .build();

        branch2 = Branch.builder()
                .id("branch-2")
                .name("Uptown")
                .build();
    }

    @Test
    @DisplayName("Should create franchise with valid data")
    void shouldCreateFranchiseWithValidData() {
        // Then
        assertNotNull(franchise);
        assertEquals("franchise-1", franchise.getId());
        assertEquals("Tech Store", franchise.getName());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Should add branch to franchise")
    void shouldAddBranchToFranchise() {
        // When
        franchise.addBranch(branch1);

        // Then
        assertEquals(1, franchise.getBranches().size());
        assertTrue(franchise.getBranches().contains(branch1));
    }

    @Test
    @DisplayName("Should add multiple branches")
    void shouldAddMultipleBranches() {
        // When
        franchise.addBranch(branch1);
        franchise.addBranch(branch2);

        // Then
        assertEquals(2, franchise.getBranches().size());
        assertTrue(franchise.getBranches().contains(branch1));
        assertTrue(franchise.getBranches().contains(branch2));
    }

    @Test
    @DisplayName("Should find branch by id")
    void shouldFindBranchById() {
        // Given
        franchise.addBranch(branch1);
        franchise.addBranch(branch2);

        // When
        Optional<Branch> found = franchise.findBranchById("branch-1");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Downtown", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when branch not found")
    void shouldReturnEmptyWhenBranchNotFound() {
        // Given
        franchise.addBranch(branch1);

        // When
        Optional<Branch> found = franchise.findBranchById("non-existent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should handle empty branches list")
    void shouldHandleEmptyBranchesList() {
        // When
        Optional<Branch> found = franchise.findBranchById("branch-1");

        // Then
        assertFalse(found.isPresent());
        assertTrue(franchise.getBranches().isEmpty());
    }
}
