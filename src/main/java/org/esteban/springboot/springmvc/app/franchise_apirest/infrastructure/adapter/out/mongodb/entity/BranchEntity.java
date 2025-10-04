package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {
    private String id;
    private String name;

    @Builder.Default
    private List<ProductEntity> products = new ArrayList<>();
}
