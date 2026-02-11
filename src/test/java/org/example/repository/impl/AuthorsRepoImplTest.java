package org.example.repository.impl;

import org.example.model.Author;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorsRepoImplTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private AuthorsRepoImpl authorsRepo;

    private Author testAuthor;

    @BeforeEach
    void setUp() {
        authorsRepo = new AuthorsRepoImpl(namedParameterJdbcTemplate);
        testAuthor = new Author(1L, "John", "Doe", LocalDate.of(1980, 1, 15));
    }

    @Test
    void getAll() {
        List<Author> expectedAuthors = List.of(
                testAuthor,
                new Author(2L, "Jane", "Smith", LocalDate.of(1990, 5, 20))
        );

        when(namedParameterJdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(expectedAuthors);

        List<Author> result = authorsRepo.getAll();

        assertEquals(2, result.size());
        assertEquals(expectedAuthors, result);
        verify(namedParameterJdbcTemplate).query(contains("SELECT * FROM AUTHORS"), any(RowMapper.class));
    }

    @Test
    void getById() {
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(testAuthor);

        Optional<Author> result = authorsRepo.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testAuthor, result.get());
        verify(namedParameterJdbcTemplate).queryForObject(
                contains("WHERE AUTHOR_ID = :authorId"),
                any(SqlParameterSource.class),
                any(RowMapper.class)
        );
    }

    @Test
    void create() {
        Author newAuthor = new Author(null, "New", "Author", LocalDate.of(2000, 3, 10));

        doAnswer(invocation -> {
            GeneratedKeyHolder keyHolder = invocation.getArgument(2);
            keyHolder.getKeyList().add(Map.of("AUTHOR_ID", 5L));
            return 1;
        }).when(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class), any(KeyHolder.class));

        Author result = authorsRepo.create(newAuthor);

        assertEquals(5L, result.getId());
        verify(namedParameterJdbcTemplate).update(
                contains("INSERT INTO AUTHORS"),
                any(SqlParameterSource.class),
                any(KeyHolder.class)
        );
    }

    @Test
    void update() {
        Author updatedAuthor = new Author(1L, "Updated", "Name", LocalDate.of(1985, 6, 25));

        when(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenReturn(1);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(updatedAuthor);

        Author result = authorsRepo.update(updatedAuthor);

        assertNotNull(result);
        assertEquals("Updated", result.getName());
        verify(namedParameterJdbcTemplate).update(
                contains("UPDATE AUTHORS SET"),
                any(SqlParameterSource.class)
        );
    }

    @Test
    void delete() {
        when(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenReturn(1);

        authorsRepo.delete(1L);

        verify(namedParameterJdbcTemplate).update(
                contains("DELETE FROM AUTHORS WHERE AUTHOR_ID"),
                any(SqlParameterSource.class)
        );
    }
}
