package org.esteban.springboot.springmvc.app.franchise_apirest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchises")
public class Franchise {
    @Id
    private String id;
    private String name;

    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
}