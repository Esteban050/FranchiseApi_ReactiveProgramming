package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    private String id;
    private String name;
    private Integer stock;
}
