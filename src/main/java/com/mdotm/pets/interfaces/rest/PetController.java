package com.mdotm.pets.interfaces.rest;

import com.mdotm.pets.application.PetService;
import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.Direction;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.pagination.SortOrder;
import com.mdotm.pets.domain.query.PetCriteria;
import com.mdotm.pets.interfaces.rest.dto.PageResponse;
import com.mdotm.pets.interfaces.rest.dto.PetPatchRequest;
import com.mdotm.pets.interfaces.rest.dto.PetRequest;
import com.mdotm.pets.interfaces.rest.dto.PetResponse;
import com.mdotm.pets.interfaces.rest.mapper.PetDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pets")
public class PetController {
    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest request) {
        Pet created = service.create(PetDtoMapper.toDomain(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(PetDtoMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public PetResponse get(@PathVariable Long id) {
        Pet pet = service.get(id);
        return PetDtoMapper.toResponse(pet);
    }

    @PutMapping("/{id}")
    public PetResponse update(@PathVariable Long id, @Valid @RequestBody PetRequest request) {
        Pet updated = service.update(id, new Pet(id, request.name(), request.species(), request.age(), request.ownerName()));
        return PetDtoMapper.toResponse(updated);
    }

    @PatchMapping("/{id}")
    public PetResponse patch(@PathVariable Long id, @Valid @RequestBody PetPatchRequest request) {
        Pet current = service.get(id);
        String name = request.name() != null && request.name().isPresent() ? request.name().get() : current.name();
        String species = request.species() != null && request.species().isPresent() ? request.species().get() : current.species();
        Integer age = request.age() != null && request.age().isPresent() ? request.age().get() : current.age();
        String ownerName = request.ownerName() != null && request.ownerName().isPresent() ? request.ownerName().get() : current.ownerName();
        Pet updated = service.update(id, new Pet(id, name, species, age, ownerName));
        return PetDtoMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<PetResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String species,
            @RequestParam(required = false, name = "name") String nameContains
    ) {
        List<SortOrder> orders = parseSort(sort);
        PageRequest pr = new PageRequest(page, size, orders);
        PetCriteria criteria = new PetCriteria(Optional.ofNullable(species), Optional.ofNullable(nameContains));
        PagedResult<Pet> result = service.list(criteria, pr);
        List<PetResponse> content = result.content().stream().map(PetDtoMapper::toResponse).toList();
        return new PageResponse<>(content, result.totalElements(), result.page(), result.size());
    }

    private List<SortOrder> parseSort(List<String> sort) {
        List<SortOrder> orders = new ArrayList<>();
        if (sort == null || sort.isEmpty()) return orders;

        List<String> tokens = new ArrayList<>();
        for (String s : sort) {
            if (s == null) continue;
            s = s.trim();
            if (s.isEmpty()) continue;
            if (s.contains(",")) {
                for (String part : s.split(",")) {
                    if (part != null && !part.isBlank()) tokens.add(part.trim());
                }
            } else {
                tokens.add(s);
            }
        }

        for (int i = 0; i < tokens.size();) {
            String property = tokens.get(i);
            String next = (i + 1 < tokens.size()) ? tokens.get(i + 1) : null;

            if ("asc".equalsIgnoreCase(property) || "desc".equalsIgnoreCase(property)) {
                i++;
                continue;
            }

            Direction dir = Direction.ASC;
            if (next != null && ("desc".equalsIgnoreCase(next) || "asc".equalsIgnoreCase(next))) {
                dir = "desc".equalsIgnoreCase(next) ? Direction.DESC : Direction.ASC;
                i += 2;
            } else {
                i += 1;
            }

            orders.add(new SortOrder(property, dir));
        }
        return orders;
    }
}
