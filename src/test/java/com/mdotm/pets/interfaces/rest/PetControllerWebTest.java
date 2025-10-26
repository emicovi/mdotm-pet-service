package com.mdotm.pets.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdotm.pets.application.PetService;
import com.mdotm.pets.domain.model.Pet;
import com.mdotm.pets.domain.pagination.PagedResult;
import com.mdotm.pets.interfaces.rest.dto.PetRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@Import(PetControllerWebTest.TestConfig.class)
class PetControllerWebTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired PetService service;

    @Test
    void post_validates_and_returns_201() throws Exception {
        PetRequest req = new PetRequest("Rex", "Dog", 3, "Alice");
        Mockito.when(service.create(any())).thenReturn(new Pet(1L, "Rex", "Dog", 3, "Alice"));

        mvc.perform(post("/api/v1/pets")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(om.writeValueAsString(req))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Objects.requireNonNull(containsString("/api/v1/pets/1"))))
                .andExpect(jsonPath("$.id", Objects.requireNonNull(is(1))))
                .andExpect(jsonPath("$.name", Objects.requireNonNull(is("Rex"))));
    }

    @Test
    void post_invalid_returns_problem_400() throws Exception {
        String payload = "{\"species\":\"Dog\"}"; // missing name
        mvc.perform(post("/api/v1/pets").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", Objects.requireNonNull(containsString("application/problem+json"))))
                .andExpect(jsonPath("$.title", Objects.requireNonNull(containsString("Invalid"))))
                .andExpect(jsonPath("$.errors.fields.name", Objects.requireNonNull(notNullValue())));
    }

    @Test
    void list_returns_page() throws Exception {
        Mockito.when(service.list(any(), any())).thenReturn(new PagedResult<>(List.of(new Pet(1L, "Rex", "Dog", 3, null)), 1, 0, 20));
        mvc.perform(get("/api/v1/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", Objects.requireNonNull(is(1))))
                .andExpect(jsonPath("$.content[0].name", Objects.requireNonNull(is("Rex"))));

    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        PetService petService() {
            return Mockito.mock(PetService.class);
        }
    }
}
