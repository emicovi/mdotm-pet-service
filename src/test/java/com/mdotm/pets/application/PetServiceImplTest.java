package com.mdotm.pets.application;

import com.mdotm.pets.domain.exception.PetNotFoundException;
import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.PageRequest;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.domain.port.PetRepository;
import com.mdotm.pets.domain.query.PetCriteria;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PetServiceImplTest {

    private final PetRepository repo = mock(PetRepository.class);
    private final PetService service = new PetServiceImpl(repo);

    @Test
    void create_assignsIdFromRepo() {
        Pet input = new Pet(null, "Rex", "Dog", 3, "Alice");
        Pet saved = new Pet(1L, "Rex", "Dog", 3, "Alice");
        when(repo.save(input)).thenReturn(saved);

        Pet result = service.create(input);
        assertThat(result.id()).isEqualTo(1L);
        verify(repo).save(input);
    }

    @Test
    void get_returnsPetOrThrows() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Pet(1L, "Milo", "Cat", 2, null)));
        Pet p = service.get(1L);
        assertThat(p.name()).isEqualTo("Milo");

        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(PetNotFoundException.class, () -> service.get(2L));
    }

    @Test
    void update_checksExistenceAndSavesWithId() {
        when(repo.findById(10L)).thenReturn(Optional.of(new Pet(10L, "Old", "Dog", 5, null)));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Pet toUpdate = new Pet(null, "New", "Dog", 6, "Bob");
        Pet result = service.update(10L, toUpdate);
        assertThat(result.id()).isEqualTo(10L);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(10L);
    }

    @Test
    void update_throwsWhenMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PetNotFoundException.class, () -> service.update(99L, new Pet(null, "A", "Dog", 1, null)));
    }

    @Test
    void delete_checksExistenceThenDeletes() {
        when(repo.findById(3L)).thenReturn(Optional.of(new Pet(3L, "Nala", "Cat", 1, null)));
        service.delete(3L);
        verify(repo).deleteById(3L);
    }

    @Test
    void list_delegatesToRepo() {
        when(repo.findAll(any(), any())).thenReturn(new PagedResult<>(java.util.List.of(), 0, 0, 10));
        PagedResult<Pet> pr = service.list(PetCriteria.empty(), PageRequest.of(0, 10));
        assertThat(pr.totalElements()).isZero();
        verify(repo).findAll(any(), any());
    }
}
