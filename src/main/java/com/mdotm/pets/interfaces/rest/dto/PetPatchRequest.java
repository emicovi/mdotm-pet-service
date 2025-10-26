package com.mdotm.pets.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PetPatchRequest(
        Optional<String> name,
        Optional<String> species,
        @Min(0) Optional<Integer> age,
        Optional<String> ownerName
) {}
