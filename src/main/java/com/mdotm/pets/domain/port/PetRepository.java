package com.mdotm.pets.domain.port;

import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.query.PetCriteria;

import java.util.Optional;

public interface PetRepository {
    Pet save(Pet pet);
    Optional<Pet> findById(Long id);
    void deleteById(Long id);
    PagedResult<Pet> findAll(PetCriteria criteria, PageRequest pageRequest);
}
