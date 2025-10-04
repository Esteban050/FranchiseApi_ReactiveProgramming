package org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface AddBranchUseCase {
    Mono<Franchise> addBranch(String franchiseId, String branchName);
}