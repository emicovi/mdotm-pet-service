package com.mdotm.pets.interfaces.rest.mapper;

import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.interfaces.rest.dto.PetRequest;
import com.mdotm.pets.interfaces.rest.dto.PetResponse;

public final class PetDtoMapper {
    private PetDtoMapper() {}

    public static Pet toDomain(PetRequest req) {
        return new Pet(null, req.name(), req.species(), req.age(), req.ownerName());
    }

    public static PetResponse toResponse(Pet pet) {
        return new PetResponse(pet.id(), pet.name(), pet.species(), pet.age(), pet.ownerName());
    }
}
