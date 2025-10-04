package org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetFranchiseUseCase {
    Mono<Franchise> getFranchiseById(String franchiseId);
    Flux<Franchise> getAllFranchises();
}
