package org.example.service.impl;

import org.example.exceptions.AuthorNotFoundException;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.GenreNotFoundException;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorsRepo;
import org.example.repository.BooksRepo;
import org.example.repository.GenresRepo;
import org.example.util.LocalizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookServiceImplTest {

    public static final String TEST_BOOK = "Test Book";
    @Mock
    private BooksRepo booksRepo;

    @Mock
    private GenresRepo genresRepo;

    @Mock
    private AuthorsRepo authorsRepo;

    @Mock
    private LocalizationService localizationService;

    private BookServiceImpl bookService;

    private Book testBook;
    private Author testAuthor;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(booksRepo, genresRepo, authorsRepo, localizationService);
        testAuthor = new Author(1L, "John", "Doe",
                LocalDate.of(1980, 1, 15));
        testGenre = new Genre(1L, "Fiction");
        testBook = new Book(1L, TEST_BOOK, "Test Description",
                LocalDate.of(2020, 5, 10), testGenre, testAuthor);
    }

    @Test
    void printAll() {
        List<Book> books = List.of(testBook);

        when(booksRepo.findAll()).thenReturn(books);

        List<Book> result = bookService.getAll();

        verify(booksRepo).findAll();
        assertIterableEquals(books, result);
    }

    @Test
    void getByIdFound() {
        when(booksRepo.findById(1L)).thenReturn(Optional.of(testBook));

        bookService.getById(1L);

        verify(booksRepo).findById(1L);
    }

    @Test
    void getByIdNotFound() {
        when(booksRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getById(999L));
    }

    @Test
    void createSuccess() {
        when(authorsRepo.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(genresRepo.findById(1L)).thenReturn(Optional.of(testGenre));

        bookService.create(testBook);

        verify(booksRepo).save(any(Book.class));
        verify(authorsRepo).findById(1L);
        verify(genresRepo).findById(1L);
    }

    @Test
    void createAuthorNotFound() {
        testBook.getAuthor().setId(999L);
        when(authorsRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> bookService.create(testBook));
    }

    @Test
    void createGenreNotFound() {
        testBook.getGenre().setId(999L);
        when(authorsRepo.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(genresRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> bookService.create(testBook));
    }

    @Test
    void updateSuccess() {
        Book updateTestBook = new Book(1L, "Updated Book", "Updated Description", null, null, null);
        Book expectedBook = new Book(1L, "Updated Book", "Updated Description", testBook.getReleaseDate(), testGenre, testAuthor);
        when(booksRepo.findById(1L)).thenReturn(Optional.of(testBook));
        when(booksRepo.save(expectedBook)).thenReturn(expectedBook);

        Book result = bookService.update(updateTestBook);

        assertEquals(updateTestBook.getId(), result.getId());
        assertEquals(updateTestBook.getName(), result.getName());
        assertEquals(updateTestBook.getDescription(), result.getDescription());
    }

    @Test
    void updateBookNotFound() {
        Book updateTestBook = new Book(999L, null, null, null, null, null);
        when(booksRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.update(updateTestBook));

    }

    @Test
    void delete() {
        bookService.delete(1L);

        verify(booksRepo).deleteById(1L);
    }
}
