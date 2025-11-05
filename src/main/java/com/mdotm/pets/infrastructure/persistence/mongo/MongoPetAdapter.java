package com.mdotm.pets.infrastructure.persistence.mongo;

import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.port.PetRepository;
import com.mdotm.pets.domain.query.PetCriteria;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("mongo")
public class MongoPetAdapter implements PetRepository {

    private final PetMongoRepository mongoRepo;
    private final AtomicLong seq = new AtomicLong(0);

    public MongoPetAdapter(PetMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public Pet save(Pet pet) {
        Long id = pet.id();
        if (id == null) {
            id = seq.incrementAndGet();
        }
        PetDocument doc = toDocument(pet.withId(id));
        PetDocument saved = mongoRepo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<Pet> findById(Long id) {
        return mongoRepo.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mongoRepo.deleteById(id);
    }

    @Override
    public PagedResult<Pet> findAll(PetCriteria criteria, PageRequest pageRequest) {
        // TODO: per ora non implemento i filtri
        Pageable pageable = toPageable(pageRequest);
        Page<PetDocument> page = mongoRepo.findAll(pageable);
        List<Pet> content = page.getContent().stream().map(this::toDomain).toList();
        return new PagedResult<>(content, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }

    // --- Metodi Helper ---
    private Pageable toPageable(PageRequest pr) {
        return org.springframework.data.domain.PageRequest.of(pr.page(), pr.size(), Sort.by("id"));
    }

    private PetDocument toDocument(Pet pet) {
        PetDocument e = new PetDocument();
        e.setId(pet.id());
        e.setName(pet.name());
        e.setSpecies(pet.species());
        e.setAge(pet.age());
        e.setOwnerName(pet.ownerName());
        return e;
    }

    private Pet toDomain(PetDocument e) {
        return new Pet(e.getId(), e.getName(), e.getSpecies(), e.getAge(), e.getOwnerName());
    }
}