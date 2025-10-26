package com.mdotm.pets.application;

import com.mdotm.pets.domain.exception.PetNotFoundException;
import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.port.PetRepository;
import com.mdotm.pets.domain.query.PetCriteria;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository repository;

    public PetServiceImpl(PetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pet create(Pet pet) {
        // ID is assigned by repository
        return repository.save(pet);
    }

    @Override
    public Pet get(Long id) {
        return repository.findById(id).orElseThrow(() -> new PetNotFoundException(id));
    }

    @Override
    public Pet update(Long id, Pet pet) {
        // Ensure existence
        repository.findById(id).orElseThrow(() -> new PetNotFoundException(id));
        return repository.save(pet.withId(id));
    }

    @Override
    public void delete(Long id) {
        // Ensure existence
        repository.findById(id).orElseThrow(() -> new PetNotFoundException(id));
        repository.deleteById(id);
    }

    @Override
    public PagedResult<Pet> list(PetCriteria criteria, PageRequest pageRequest) {
        return repository.findAll(criteria, pageRequest);
    }
}
