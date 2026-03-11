package org.example.genres.api;

import org.example.genres.model.Genre;
import org.example.genres.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreRepository genreRepository;

    @Test
    void getAllReturnsList() throws Exception {
        when(genreRepository.findAll()).thenReturn(List.of(
                new Genre(1L, "Comedy"),
                new Genre(2L, "Drama")
        ));

        mockMvc.perform(get("/api/genres").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Comedy"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Drama"));
    }

    @Test
    void getByIdReturnsGenre() throws Exception {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(new Genre(1L, "Comedy")));

        mockMvc.perform(get("/api/genres/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Comedy"));
    }

    @Test
    void getByIdReturns404() throws Exception {
        when(genreRepository.findById(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/genres/404").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
