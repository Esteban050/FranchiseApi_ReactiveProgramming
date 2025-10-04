package org.esteban.springboot.springmvc.app.franchise_apirest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequest {
    @NotBlank(message = "Franchise name is required")
    private String name;
}