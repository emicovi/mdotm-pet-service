package com.mdotm.pets.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PetPatchRequest(
        Optional<String> name,
        Optional<String> species,
        Optional<@Min(0) Integer> age,
        Optional<String> ownerName
) {}
