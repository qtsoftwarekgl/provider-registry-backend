package com.frpr.repo;

import com.frpr.model.FacilityRegistry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacilityRepository extends MongoRepository<FacilityRegistry, String> {

}