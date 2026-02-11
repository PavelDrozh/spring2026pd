package org.example.repository.impl;

import org.example.model.Author;
import org.example.model.Book;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BooksRepoImplTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private BooksRepoImpl booksRepo;

    private Book testBook;
    private Author testAuthor;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        booksRepo = new BooksRepoImpl(namedParameterJdbcTemplate);
        testAuthor = new Author(1L, "John", "Doe", LocalDate.of(1980, 1, 15));
        testGenre = new Genre(1L, "Fiction");
        testBook = new Book(1L, "Test Book", "Test Description", LocalDate.of(2020, 5, 10), testGenre, testAuthor);
    }

    @Test
    void getAll() {
        List<Book> expectedBooks = List.of(
                testBook,
                new Book(2L, "Another Book", "Another Description", LocalDate.of(2021, 3, 20), testGenre, testAuthor)
        );

        when(namedParameterJdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(expectedBooks);

        List<Book> result = booksRepo.getAll();

        assertEquals(2, result.size());
        assertEquals(expectedBooks, result);
        verify(namedParameterJdbcTemplate).query(contains("FROM BOOKS"), any(RowMapper.class));
    }

    @Test
    void getById() {
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(testBook);

        Optional<Book> result = booksRepo.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
        verify(namedParameterJdbcTemplate).queryForObject(
                contains("BOOK_ID = (:bookId)"),
                any(SqlParameterSource.class),
                any(RowMapper.class)
        );
    }

    @Test
    void create() {
        Book newBook = new Book(0L, "New Book", "New Description", LocalDate.of(2022, 1, 1), testGenre, testAuthor);

        doAnswer(invocation -> {
            GeneratedKeyHolder keyHolder = invocation.getArgument(2);
            keyHolder.getKeyList().add(Map.of("BOOK_ID", 5L));
            return 1;
        }).when(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class), any(KeyHolder.class));

        Book result = booksRepo.create(newBook);

        assertEquals(5L, result.getId());
        verify(namedParameterJdbcTemplate).update(
                contains("INSERT INTO BOOKS"),
                any(SqlParameterSource.class),
                any(KeyHolder.class)
        );
    }

    @Test
    void update() {
        Book updatedBook = new Book(1L, "Updated Book", "Updated Description", LocalDate.of(2023, 6, 15), testGenre, testAuthor);

        when(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenReturn(1);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(updatedBook);

        Book result = booksRepo.update(updatedBook);

        assertNotNull(result);
        assertEquals("Updated Book", result.getName());
        verify(namedParameterJdbcTemplate).update(
                contains("UPDATE BOOKS SET"),
                any(SqlParameterSource.class)
        );
    }

    @Test
    void delete() {
        when(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenReturn(1);

        booksRepo.delete(1L);

        verify(namedParameterJdbcTemplate).update(
                contains("DELETE FROM BOOKS WHERE BOOK_ID"),
                any(SqlParameterSource.class)
        );
    }
}
