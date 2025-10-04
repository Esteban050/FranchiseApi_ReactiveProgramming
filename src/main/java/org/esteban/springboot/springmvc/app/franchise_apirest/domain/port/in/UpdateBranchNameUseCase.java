package org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.in;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface UpdateBranchNameUseCase {
    Mono<Franchise> updateBranchName(String franchiseId, String branchId, String newName);
}
