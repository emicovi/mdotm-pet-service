package com.mdotm.pets.application;

import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.query.PetCriteria;

public interface PetService {
    Pet create(Pet pet);
    Pet get(Long id);
    Pet update(Long id, Pet pet);
    void delete(Long id);
    PagedResult<Pet> list(PetCriteria criteria, PageRequest pageRequest);
}
