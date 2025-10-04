package org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface DeleteProductUseCase {
    Mono<Franchise> deleteProduct(String franchiseId, String branchId, String productId);
}
