package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb;

import lombok.RequiredArgsConstructor;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.model.Franchise;
import org.esteban.springboot.springmvc.app.franchise_apirest.domain.port.out.FranchiseRepositoryPort;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.mapper.FranchiseMapper;
import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.repository.FranchiseMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final FranchiseMongoRepository mongoRepository;
    private final FranchiseMapper mapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.just(franchise)
                .map(mapper::toEntity)
                .flatMap(mongoRepository::save)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}
