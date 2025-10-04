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
public class BranchRequest {
    @NotBlank(message = "Branch name is required")
    private String name;
}