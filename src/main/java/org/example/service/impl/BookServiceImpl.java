package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.exceptions.AuthorNotFoundException;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.GenreNotFoundException;
import org.example.client.GenresClient;
import org.example.dto.GenreDto;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorsRepo;
import org.example.repository.BooksRepo;
import org.example.service.BooksService;
import org.example.util.LocalizationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.util.LocalizationServiceImpl.*;

@Service
@AllArgsConstructor
public class BookServiceImpl  implements BooksService {

    private final BooksRepo booksRepo;
    private final GenresClient genresClient;
    private final AuthorsRepo authorsRepo;
    private final LocalizationService localizationService;

    @Transactional(readOnly = true)
    @Override
    public List<Book> getAll() {
        List<Book> books = booksRepo.findAll();
        books.forEach(this::enrichGenre);
        return books;
    }

    @Transactional(readOnly = true)
    @Override
    public Book getById(long id) {
        Optional<Book> oBook = booksRepo.findById(id);
        if (oBook.isEmpty()) {
            throw new BookNotFoundException(localizationService.getMessage(BOOKS_NOT_FOUND, id));
        }
        Book book = oBook.get();
        enrichGenre(book);
        return book;
    }

    @Transactional
    @Override
    public Book create(Book book) {
        if (Objects.isNull(book.getAuthor()) || Objects.isNull(book.getAuthor().getId())) {
            throw new IllegalArgumentException("Author is required");
        }
        long authorId = book.getAuthor().getId();
        Optional<Author> oAuthor = authorsRepo.findById(authorId);
        if (oAuthor.isEmpty()) {
            throw new AuthorNotFoundException(localizationService.getMessage(AUTHORS_NOT_FOUND, authorId));
        }
        if (Objects.isNull(book.getGenreId())) {
            if (Objects.nonNull(book.getGenre()) && Objects.nonNull(book.getGenre().getId())) {
                book.setGenreId(book.getGenre().getId());
            } else {
                throw new IllegalArgumentException("Genre is required");
            }
        }
        long genreId = book.getGenreId();
        Optional<GenreDto> oGenre = genresClient.getById(genreId);
        if (oGenre.isEmpty()) {
            throw new GenreNotFoundException(localizationService.getMessage(GENRES_NOT_FOUND, genreId));
        }
        book.setGenre(new Genre(oGenre.get().getId(), oGenre.get().getName()));
        book.setAuthor(oAuthor.get());
        return booksRepo.save(book);
    }

    @Transactional
    @Override
    public Book update(Book book) {
        Optional<Book> oBook = booksRepo.findById(book.getId());
        if (oBook.isEmpty()) {
            throw new BookNotFoundException(localizationService.getMessage(BOOKS_NOT_FOUND, book.getId()));
        }
        Book bookForUpdate = oBook.get();
        if(Objects.nonNull(book.getAuthor())) {
            Optional<Author> oAuthor = authorsRepo.findById(book.getAuthor().getId());
            if (oAuthor.isEmpty()) {
                throw new AuthorNotFoundException(localizationService.
                        getMessage(AUTHORS_NOT_FOUND, book.getAuthor().getId()));
            }
            book.setAuthor(oAuthor.get());
        }
        Long newGenreId = null;
        if (Objects.nonNull(book.getGenreId())) {
            newGenreId = book.getGenreId();
        } else if (Objects.nonNull(book.getGenre()) && Objects.nonNull(book.getGenre().getId())) {
            newGenreId = book.getGenre().getId();
        }

        if (Objects.nonNull(newGenreId)) {
            Optional<GenreDto> oGenre = genresClient.getById(newGenreId);
            if (oGenre.isEmpty()) {
                throw new GenreNotFoundException(localizationService.
                        getMessage(GENRES_NOT_FOUND, newGenreId));
            }
            book.setGenreId(newGenreId);
            book.setGenre(new Genre(oGenre.get().getId(), oGenre.get().getName()));
        }
        fillBookWithNonNull(bookForUpdate, book);
        return booksRepo.save(bookForUpdate);
    }

    private void fillBookWithNonNull(Book bookForUpdate, Book book) {
        if (Objects.nonNull(book.getName()) && !book.getName().isBlank()) {
            bookForUpdate.setName(book.getName());
        }
        if(Objects.nonNull(book.getReleaseDate())) {
            bookForUpdate.setReleaseDate(book.getReleaseDate());
        }
        if (Objects.nonNull(book.getDescription()) && !book.getDescription().isBlank()) {
            bookForUpdate.setDescription(book.getDescription());
        }
        if(Objects.nonNull(book.getAuthor())) {
            bookForUpdate.setAuthor(book.getAuthor());
        }
        if (Objects.nonNull(book.getGenreId())) {
            bookForUpdate.setGenreId(book.getGenreId());
        }
    }

    private void enrichGenre(Book book) {
        if (book == null || book.getGenreId() == null) {
            return;
        }
        Optional<GenreDto> genre = genresClient.getById(book.getGenreId());
        if (genre.isEmpty()) {
            return;
        }
        genre.ifPresent(g -> book.setGenre(new Genre(g.getId(), g.getName())));
    }

    @Transactional
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(long id) {
        booksRepo.deleteById(id);
    }
}
