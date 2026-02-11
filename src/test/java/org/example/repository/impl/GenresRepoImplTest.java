package org.example.repository.impl;

import org.example.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenresRepoImplTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private GenresRepoImpl genresRepo;

    private Genre testGenre;

    @BeforeEach
    void setUp() {
        genresRepo = new GenresRepoImpl(namedParameterJdbcTemplate);
        testGenre = new Genre(1L, "Fiction");
    }

    @Test
    void getAll() {
        List<Genre> expectedGenres = List.of(
                testGenre,
                new Genre(2L, "Science Fiction")
        );

        when(namedParameterJdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(expectedGenres);

        List<Genre> result = genresRepo.getAll();

        assertEquals(2, result.size());
        assertEquals(expectedGenres, result);
        verify(namedParameterJdbcTemplate).query(contains("SELECT * FROM GENRES"), any(RowMapper.class));
    }

    @Test
    void getById() {
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(testGenre);

        Optional<Genre> result = genresRepo.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testGenre, result.get());
        verify(namedParameterJdbcTemplate).queryForObject(
                contains("WHERE GENRE_ID = :genreId"),
                any(SqlParameterSource.class),
                any(RowMapper.class)
        );
    }

    @Test
    void create() {
        Genre newGenre = new Genre(null, "Horror");

        doAnswer(invocation -> {
            GeneratedKeyHolder keyHolder = invocation.getArgument(2);
            keyHolder.getKeyList().add(Map.of("GENRE_ID", 5L));
            return 1;
        }).when(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class), any(KeyHolder.class));

        Genre result = genresRepo.create(newGenre);

        assertEquals(5L, result.getId());
        verify(namedParameterJdbcTemplate).update(
                contains("INSERT INTO GENRES"),
                any(SqlParameterSource.class),
                any(KeyHolder.class)
        );
    }

    @Test
    void update() {
        Genre updatedGenre = new Genre(1L, "Updated Genre");

        when(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenReturn(1);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(updatedGenre);

        Genre result = genresRepo.update(updatedGenre);

        assertNotNull(result);
        assertEquals("Updated Genre", result.getName());
        verify(namedParameterJdbcTemplate).update(
                contains("UPDATE GENRES SET"),
                any(SqlParameterSource.class)
        );
    }

    @Test
    void delete() {
        when(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenReturn(1);

        genresRepo.delete(1L);

        verify(namedParameterJdbcTemplate).update(
                contains("DELETE FROM GENRES WHERE GENRE_ID"),
                any(SqlParameterSource.class)
        );
    }
}
