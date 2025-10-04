package org.esteban.springboot.springmvc.app.franchise_apirest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductResponse {
    private String branchId;
    private String branchName;
    private String productId;
    private String productName;
    private Integer stock;
}