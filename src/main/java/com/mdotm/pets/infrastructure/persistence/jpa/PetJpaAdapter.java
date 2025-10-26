package com.mdotm.pets.infrastructure.persistence.jpa;

import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.Direction;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.port.PetRepository;
import com.mdotm.pets.domain.query.PetCriteria;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class PetJpaAdapter implements PetRepository {

    private final PetJpaRepository jpaRepository;

    public PetJpaAdapter(PetJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pet save(Pet pet) {
        PetEntity entity = toEntity(pet);
        PetEntity saved = jpaRepository.save(Objects.requireNonNull(entity));
        return toDomain(Objects.requireNonNull(saved));
    }

    @Override
    public Optional<Pet> findById(Long id) {
        return jpaRepository.findById(Objects.requireNonNull(id)).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(Objects.requireNonNull(id));
    }

    @Override
    public PagedResult<Pet> findAll(PetCriteria criteria, PageRequest pageRequest) {
        Specification<PetEntity> spec = Specification.allOf();
        if (criteria != null) {
            if (criteria.species().isPresent()) {
                String species = criteria.species().get();
                spec = spec.and((root, q, cb) -> cb.equal(cb.lower(root.get("species")), species.toLowerCase()));
            }
            if (criteria.nameContains().isPresent()) {
                String needle = "%" + criteria.nameContains().get().toLowerCase() + "%";
                spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), needle));
            }
        }

        Pageable pageable = toPageable(pageRequest);
        Page<PetEntity> page = jpaRepository.findAll(spec, Objects.requireNonNull(pageable));
        List<Pet> content = page.getContent().stream().map(this::toDomain).toList();
        return new PagedResult<>(content, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }

    private Pageable toPageable(PageRequest pr) {
        Sort sort = Sort.by("id");
        if (pr.sort() != null && !pr.sort().isEmpty()) {
            Sort.Order[] orders = pr.sort().stream()
                    .map(o -> new Sort.Order(
                            o.direction() == Direction.DESC ? Sort.Direction.DESC : Sort.Direction.ASC,
                            Objects.requireNonNull(o.property())
                    ))
                    .toArray(Sort.Order[]::new);
            sort = Sort.by(Objects.requireNonNull(orders));
        }
        return org.springframework.data.domain.PageRequest.of(pr.page(), pr.size(), sort);
    }

    private PetEntity toEntity(Pet pet) {
        PetEntity e = new PetEntity();
        e.setId(pet.id());
        e.setName(pet.name());
        e.setSpecies(pet.species());
        e.setAge(pet.age());
        e.setOwnerName(pet.ownerName());
        return e;
    }

    private Pet toDomain(PetEntity e) {
        return new Pet(e.getId(), e.getName(), e.getSpecies(), e.getAge(), e.getOwnerName());
    }
}
