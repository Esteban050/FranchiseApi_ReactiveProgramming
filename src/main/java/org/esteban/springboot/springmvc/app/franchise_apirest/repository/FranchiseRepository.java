package org.esteban.springboot.springmvc.app.franchise_apirest.repository;

import org.esteban.springboot.springmvc.app.franchise_apirest.model.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseRepository extends ReactiveMongoRepository<Franchise, String> {
}