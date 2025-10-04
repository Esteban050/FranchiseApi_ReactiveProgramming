package org.esteban.springboot.springmvc.app.franchise_apirest.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Franchise {
    private String id;
    private String name;

    @Builder.Default
    private List<Branch> branches = new ArrayList<>();

    public void addBranch(Branch branch) {
        this.branches.add(branch);
    }

    public Optional<Branch> findBranchById(String branchId) {
        return this.branches.stream()
                .filter(b -> b.getId().equals(branchId))
                .findFirst();
    }
}