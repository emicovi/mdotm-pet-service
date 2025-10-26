package com.mdotm.pets.domain.query;

import java.util.Optional;

public record PetCriteria(Optional<String> species, Optional<String> nameContains) {
    public static PetCriteria empty() { return new PetCriteria(Optional.empty(), Optional.empty()); }
}
