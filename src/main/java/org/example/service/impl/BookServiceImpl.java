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
        return booksRepo.getAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Book getById(long id) {
        Optional<Book> oBook = booksRepo.getById(id);
        if (oBook.isEmpty()) {
            throw new BookNotFoundException(localizationService.getMessage(BOOKS_NOT_FOUND, id));
        }
        return oBook.get();
    }

    @Transactional
    @Override
    public Book create(Book book) {
        long authorId = book.getAuthor().getId();
        Optional<Author> oAuthor = authorsRepo.getById(authorId);
        if (oAuthor.isEmpty()) {
            throw new AuthorNotFoundException(localizationService.getMessage(AUTHORS_NOT_FOUND, authorId));
        }
        long genreId = book.getGenre().getId();
        Optional<Genre> oGenre = genresRepo.getById(genreId);
        if (oGenre.isEmpty()) {
            throw new GenreNotFoundException(localizationService.getMessage(GENRES_NOT_FOUND, genreId));
        }
        book.setGenre(oGenre.get());
        book.setAuthor(oAuthor.get());
        return booksRepo.create(book);
    }

    @Transactional
    @Override
    public Book update(Book book) {
        Optional<Book> oBook = booksRepo.getById(book.getId());
        if (oBook.isEmpty()) {
            throw new BookNotFoundException(localizationService.getMessage(BOOKS_NOT_FOUND, book.getId()));
        }
        Book bookForUpdate = oBook.get();
        if(Objects.nonNull(book.getAuthor())) {
            Optional<Author> oAuthor = authorsRepo.getById(book.getAuthor().getId());
            if (oAuthor.isEmpty()) {
                throw new AuthorNotFoundException(localizationService.
                        getMessage(AUTHORS_NOT_FOUND, book.getAuthor().getId()));
            }
            book.setAuthor(oAuthor.get());
        }
        if(Objects.nonNull(book.getGenre())) {
            Optional<Genre> oGenre = genresRepo.getById(book.getGenre().getId());
            if (oGenre.isEmpty()) {
                throw new GenreNotFoundException(localizationService.
                        getMessage(GENRES_NOT_FOUND, book.getGenre().getId()));
            }
            book.setGenre(oGenre.get());
        }
        fillBookWithNonNull(bookForUpdate, book);
        return booksRepo.update(bookForUpdate);
    }

    private void fillBookWithNonNull(Book bookForUpdate, Book book) {
        if(!book.getName().isEmpty()) {
            bookForUpdate.setName(book.getName());
        }
        if(Objects.nonNull(book.getReleaseDate())) {
            bookForUpdate.setReleaseDate(book.getReleaseDate());
        }
        if(!book.getDescription().isEmpty()) {
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
        booksRepo.delete(id);
    }
}
