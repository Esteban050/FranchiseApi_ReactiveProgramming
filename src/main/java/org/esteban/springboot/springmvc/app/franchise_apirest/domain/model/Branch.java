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
public class Branch {
    private String id;
    private String name;

    @Builder.Default
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public boolean removeProduct(String productId) {
        return this.products.removeIf(p -> p.getId().equals(productId));
    }

    public Optional<Product> findProductById(String productId) {
        return this.products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();
    }

    public Optional<Product> getProductWithMaxStock() {
        return this.products.stream()
                .max((p1, p2) -> Integer.compare(p1.getStock(), p2.getStock()));
    }
}