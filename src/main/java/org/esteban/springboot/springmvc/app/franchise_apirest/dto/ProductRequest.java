package org.esteban.springboot.springmvc.app.franchise_apirest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
}
