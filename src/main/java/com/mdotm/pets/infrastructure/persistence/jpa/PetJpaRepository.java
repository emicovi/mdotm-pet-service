package com.mdotm.pets.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PetJpaRepository extends JpaRepository<PetEntity, Long>, JpaSpecificationExecutor<PetEntity> { }
