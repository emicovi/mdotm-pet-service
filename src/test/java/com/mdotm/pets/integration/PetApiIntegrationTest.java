package com.mdotm.pets.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdotm.pets.interfaces.rest.dto.PetRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inmemory")
class PetApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void end_to_end_crud() throws Exception {
        // Create
        var req = new PetRequest("Milo", "Cat", 2, "Eve");
        String location = mvc.perform(post("/api/v1/pets")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(om.writeValueAsString(req))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        location = Objects.requireNonNull(location, "Location header missing");

        // Get
        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Objects.requireNonNull(is("Milo"))));

        // List
        mvc.perform(get("/api/v1/pets").param("species", "Cat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", Objects.requireNonNull(is(1))));

        // Delete
        mvc.perform(delete(location)).andExpect(status().isNoContent());

        // Not found
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }
}
