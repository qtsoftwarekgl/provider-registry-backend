package com.frpr.repo;

import com.frpr.model.ProviderRegistry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProviderRepository extends MongoRepository<ProviderRegistry, String> {

    ProviderRegistry findBy_id(String prId);

    List<ProviderRegistry> findAllByLicenseNumber(String lNo);

    List<ProviderRegistry> findAllByDocumentNumber(String doc);
}