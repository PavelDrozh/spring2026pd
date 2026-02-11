package org.example.service.impl;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorsRepo;
import org.example.repository.BooksRepo;
import org.example.repository.GenresRepo;
import org.example.util.IOService;
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

import static org.example.util.LocalizationServiceImpl.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookServiceImplTest {

    public static final String MESSAGE = "message";
    public static final String NEW_BOOK = "New Book";
    public static final String TEST_BOOK = "Test Book";
    public static final String DATE = "2023-01-15";
    public static final String NEW_DESCRIPTION = "New Description";
    @Mock
    private BooksRepo booksRepo;

    @Mock
    private GenresRepo genresRepo;

    @Mock
    private AuthorsRepo authorsRepo;

    @Mock
    private LocalizationService localizationService;

    @Mock
    private IOService ioService;

    private BookServiceImpl bookService;

    private Book testBook;
    private Author testAuthor;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(booksRepo, genresRepo, authorsRepo, localizationService, ioService);
        testAuthor = new Author(1L, "John", "Doe", LocalDate.of(1980, 1, 15));
        testGenre = new Genre(1L, "Fiction");
        testBook = new Book(1L, TEST_BOOK, "Test Description", LocalDate.of(2020, 5, 10), testGenre, testAuthor);
    }

    @Test
    void printAll() {
        List<Book> books = List.of(testBook);

        when(booksRepo.getAll()).thenReturn(books);
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.printAll();

        verify(booksRepo).getAll();
        verify(localizationService).getMessage(eq(BOOKS_TOTAL), eq(1));
        verify(localizationService).getMessage(eq(BOOKS_ID), eq(1L));
        verify(localizationService).getMessage(eq(BOOKS_NAME), eq(TEST_BOOK));
        verify(ioService, atLeastOnce()).outputString(anyString());
    }

    @Test
    void printByIdFound() {
        when(ioService.readString()).thenReturn("1");
        when(booksRepo.getById(1L)).thenReturn(Optional.of(testBook));
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.printById();

        verify(booksRepo).getById(1L);
        verify(localizationService).getMessage(eq(BOOKS_ID), eq(1L));
        verify(localizationService).getMessage(eq(BOOKS_NAME), eq(TEST_BOOK));
        verify(localizationService).getMessage(eq(BOOKS_RELEASE_DATE), eq(LocalDate.of(2020, 5, 10)));
        verify(localizationService).getMessage(eq(BOOKS_DESCRIPTION), eq("Test Description"));
    }

    @Test
    void printByIdNotFound() {
        when(ioService.readString()).thenReturn("999");
        when(booksRepo.getById(999L)).thenReturn(Optional.empty());
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.printById();

        verify(booksRepo).getById(999L);
        verify(localizationService).getMessage(eq(BOOKS_FIND_BY_NOT_FOUND), eq(999L));
    }

    @Test
    void createSuccess() {
        when(ioService.readString())
                .thenReturn(NEW_BOOK)
                .thenReturn(DATE)
                .thenReturn(NEW_DESCRIPTION)
                .thenReturn("1")
                .thenReturn("1");
        when(authorsRepo.getById(1L)).thenReturn(Optional.of(testAuthor));
        when(genresRepo.getById(1L)).thenReturn(Optional.of(testGenre));
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.create();

        verify(booksRepo).create(any(Book.class));
        verify(authorsRepo).getById(1L);
        verify(genresRepo).getById(1L);
    }

    @Test
    void createAuthorNotFound() {
        when(ioService.readString())
                .thenReturn(NEW_BOOK)
                .thenReturn(DATE)
                .thenReturn(NEW_DESCRIPTION)
                .thenReturn("999");
        when(authorsRepo.getById(999L)).thenReturn(Optional.empty());
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.create();

        verify(booksRepo, never()).create(any(Book.class));
        verify(localizationService).getMessage(eq(AUTHORS_NOT_FOUND), eq(999L));
    }

    @Test
    void createGenreNotFound() {
        when(ioService.readString())
                .thenReturn(NEW_BOOK)
                .thenReturn(DATE)
                .thenReturn(NEW_DESCRIPTION)
                .thenReturn("1")
                .thenReturn("999");
        when(authorsRepo.getById(1L)).thenReturn(Optional.of(testAuthor));
        when(genresRepo.getById(999L)).thenReturn(Optional.empty());
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.create();

        verify(booksRepo, never()).create(any(Book.class));
        verify(localizationService).getMessage(eq(GENRES_NOT_FOUND), eq(999L));
    }

    @Test
    void updateSuccess() {
        when(ioService.readString())
                .thenReturn("1")
                .thenReturn("Updated Name")
                .thenReturn("")
                .thenReturn("Updated Description")
                .thenReturn("")
                .thenReturn("");
        when(booksRepo.getById(1L)).thenReturn(Optional.of(testBook));
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.update();

        verify(booksRepo).update(any(Book.class));
    }

    @Test
    void updateBookNotFound() {
        when(ioService.readString()).thenReturn("999");
        when(booksRepo.getById(999L)).thenReturn(Optional.empty());
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.update();

        verify(booksRepo, never()).update(any(Book.class));
        verify(localizationService).getMessage(eq(BOOKS_NOT_FOUND), eq(999L));
    }

    @Test
    void delete() {
        when(ioService.readString()).thenReturn("1");
        when(localizationService.getMessage(anyString(), any())).thenReturn(MESSAGE);

        bookService.delete();

        verify(booksRepo).delete(1L);
    }
}
