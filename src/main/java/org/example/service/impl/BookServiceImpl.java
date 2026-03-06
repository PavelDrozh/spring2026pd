package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.exceptions.AuthorNotFoundException;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.GenreNotFoundException;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorsRepo;
import org.example.repository.BooksRepo;
import org.example.repository.GenresRepo;
import org.example.service.BooksService;
import org.example.util.LocalizationService;
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
    private final GenresRepo genresRepo;
    private final AuthorsRepo authorsRepo;
    private final LocalizationService localizationService;

    @Transactional(readOnly = true)
    @Override
    public List<Book> getAll() {
        return booksRepo.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Book getById(long id) {
        Optional<Book> oBook = booksRepo.findById(id);
        if (oBook.isEmpty()) {
            throw new BookNotFoundException(localizationService.getMessage(BOOKS_NOT_FOUND, id));
        }
        return oBook.get();
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
        if (Objects.isNull(book.getGenre()) || Objects.isNull(book.getGenre().getId())) {
            throw new IllegalArgumentException("Genre is required");
        }
        long genreId = book.getGenre().getId();
        Optional<Genre> oGenre = genresRepo.findById(genreId);
        if (oGenre.isEmpty()) {
            throw new GenreNotFoundException(localizationService.getMessage(GENRES_NOT_FOUND, genreId));
        }
        book.setGenre(oGenre.get());
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
        if(Objects.nonNull(book.getGenre())) {
            Optional<Genre> oGenre = genresRepo.findById(book.getGenre().getId());
            if (oGenre.isEmpty()) {
                throw new GenreNotFoundException(localizationService.
                        getMessage(GENRES_NOT_FOUND, book.getGenre().getId()));
            }
            book.setGenre(oGenre.get());
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
        if(Objects.nonNull(book.getGenre())) {
            bookForUpdate.setGenre(book.getGenre());
        }
    }

    @Transactional
    @Override
    public void delete(long id) {
        booksRepo.deleteById(id);
    }
}
