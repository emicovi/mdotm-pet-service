package com.mdotm.pets.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PetRequest(
        @NotBlank String name,
        @NotBlank String species,
        @Min(0) Integer age,
        String ownerName
) {}
