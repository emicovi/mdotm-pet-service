package com.mdotm.pets.infrastructure.persistence.mongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PetMongoRepository extends MongoRepository<PetDocument, Long> {
}