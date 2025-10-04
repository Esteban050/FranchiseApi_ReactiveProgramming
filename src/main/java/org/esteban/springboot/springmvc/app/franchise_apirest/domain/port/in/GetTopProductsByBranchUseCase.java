package org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Branch;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Product;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface GetTopProductsByBranchUseCase {
    Flux<Map.Entry<Branch, Product>> getTopProductsByBranch(String franchiseId);
}
