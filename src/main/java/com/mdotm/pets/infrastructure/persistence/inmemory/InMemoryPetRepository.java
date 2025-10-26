package com.mdotm.pets.infrastructure.persistence.inmemory;

import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.Direction;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.pagination.SortOrder;
import com.mdotm.pets.domain.port.PetRepository;
import com.mdotm.pets.domain.query.PetCriteria;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class InMemoryPetRepository implements PetRepository {
    private final Map<Long, Pet> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Pet save(Pet pet) {
        Long id = pet.id();
        if (id == null) {
            id = seq.incrementAndGet();
        }
        Pet toSave = pet.withId(id);
        store.put(id, toSave);
        return toSave;
    }

    @Override
    public Optional<Pet> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public PagedResult<Pet> findAll(PetCriteria criteria, PageRequest pageRequest) {
        List<Pet> all = new ArrayList<>(store.values());

        // Filtering
        if (criteria != null) {
            if (criteria.species().isPresent()) {
                String species = criteria.species().get();
                all = all.stream().filter(p -> species.equalsIgnoreCase(p.species())).collect(Collectors.toList());
            }
            if (criteria.nameContains().isPresent()) {
                String needle = criteria.nameContains().get().toLowerCase();
                all = all.stream().filter(p -> p.name() != null && p.name().toLowerCase().contains(needle)).collect(Collectors.toList());
            }
        }

        // Sorting
        Comparator<Pet> comparator;
        if (pageRequest.sort() != null && !pageRequest.sort().isEmpty()) {
            Comparator<Pet> c = null;
            for (SortOrder order : pageRequest.sort()) {
                Comparator<Pet> next = comparatorFor(order.property());
                if (order.direction() == Direction.DESC) next = next.reversed();
                c = (c == null) ? next : c.thenComparing(next);
            }
            comparator = (c == null)
                    ? Comparator.comparing(Pet::id, Comparator.nullsLast(Long::compareTo))
                    : c.thenComparing(Comparator.comparing(Pet::id, Comparator.nullsLast(Long::compareTo)));
        } else {
            comparator = Comparator.comparing(Pet::id, Comparator.nullsLast(Long::compareTo));
        }
        all.sort(comparator);

        // Pagination
        int from = Math.min(pageRequest.page() * pageRequest.size(), all.size());
        int to = Math.min(from + pageRequest.size(), all.size());
        List<Pet> pageContent = all.subList(from, to);

        return new PagedResult<>(pageContent, all.size(), pageRequest.page(), pageRequest.size());
    }

    private Comparator<Pet> comparatorFor(String property) {
        return switch (property) {
            case "name" -> Comparator.comparing(Pet::name, Comparator.nullsLast(String::compareToIgnoreCase));
            case "species" -> Comparator.comparing(Pet::species, Comparator.nullsLast(String::compareToIgnoreCase));
            case "age" -> Comparator.comparing(p -> Optional.ofNullable(p.age()).orElse(Integer.MAX_VALUE));
            case "ownerName" -> Comparator.comparing(Pet::ownerName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "id" -> Comparator.comparing(Pet::id, Comparator.nullsLast(Long::compareTo));
            default -> Comparator.comparing(Pet::id);
        };
    }
}
