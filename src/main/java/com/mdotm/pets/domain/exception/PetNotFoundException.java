package com.mdotm.pets.domain.exception;

public class PetNotFoundException extends RuntimeException {
    public PetNotFoundException(Long id) {
        super("Pet not found: " + id);
    }
}
