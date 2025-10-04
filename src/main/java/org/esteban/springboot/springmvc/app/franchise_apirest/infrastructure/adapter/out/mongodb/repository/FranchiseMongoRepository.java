package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.repository;

import org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.adapter.out.mongodb.entity.FranchiseEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseEntity, String> {
}
