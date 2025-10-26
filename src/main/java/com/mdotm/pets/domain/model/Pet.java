package com.mdotm.pets.domain.model;

import java.util.Objects;

public record Pet(
        Long id,
        String name,
        String species,
        Integer age,
        String ownerName
) {
    public Pet {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(species, "species must not be null");
        if (age != null && age < 0) {
            throw new IllegalArgumentException("age must be >= 0");
        }
    }

    public Pet withId(Long newId) {
        return new Pet(newId, name, species, age, ownerName);
    }
}
