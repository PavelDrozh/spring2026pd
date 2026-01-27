package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorsRepo;
import org.example.repository.BooksRepo;
import org.example.repository.GenresRepo;
import org.example.service.BooksService;
import org.example.util.IOService;
import org.example.util.LocalizationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final IOService ioService;

    @Override
    public void printAll() {
        List<Book> books = booksRepo.getAll();
        printMessage(BOOKS_TOTAL,books.size());
        for (Book b : books) {
            printBook(b);
            ioService.outputString("============="); // Add an empty line between questions
        }
    }

    @Override
    public void printById() {
        printMessage(BOOKS_FIND_BY_ID_REQ);
        long bookId = getIdFromUser(BOOKS_FIND_BY_ID_ERR);
        Optional<Book> book = booksRepo.getById(bookId);
        if (book.isPresent()) {
            printBookFull(book.get());
        } else {
            printMessage(BOOKS_FIND_BY_NOT_FOUND, bookId);
        }
    }

    @Override
    public void create() {
        printMessage(BOOKS_CREATE_NAME);
        String bookName = getNotEmptyString();
        printMessage(BOOKS_CREATE_RELEASE);
        LocalDate bookRelease = getLocalDateFromUser();
        printMessage(BOOKS_CREATE_DESCRIPTION);
        String description = getNotEmptyString();
        printMessage(BOOKS_CREATE_AUTHOR);
        long authorId = getIdFromUser(AUTHORS_PARSE_ID_ERR);
        Optional<Author> oAuthor = authorsRepo.getById(authorId);
        if (oAuthor.isEmpty()) {
            printMessage(AUTHORS_NOT_FOUND, authorId);
            return;
        }
        Author author = oAuthor.get();
        printMessage(BOOKS_CREATE_GENRE);
        long genreId = getIdFromUser(GENRES_PARSE_ID_ERR);
        Optional<Genre> oGenre = genresRepo.getById(genreId);
        if (oGenre.isEmpty()) {
            printMessage(GENRES_NOT_FOUND, genreId);
            return;
        }
        Genre genre = oGenre.get();
        booksRepo.create(new Book(0, bookName, description, bookRelease, genre, author));
    }

    private LocalDate getLocalDateFromUser() {
        LocalDate answer = null;
        boolean notDate = true;
        while (notDate) {
            try {
                answer = LocalDate.parse(getNotEmptyString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                notDate = false;
            } catch (DateTimeParseException e) {
                printMessage(BOOKS_PARSE_LOCAL_DATE_ERR);
            }
        }
        return answer;
    }

    @Override
    public void update() {
        printMessage(BOOKS_UPDATE_START);
        long bookId = getIdFromUser(AUTHORS_PARSE_ID_ERR);
        Optional<Book> oBook = booksRepo.getById(bookId);
        if (oBook.isEmpty()) {
            printMessage(BOOKS_NOT_FOUND, bookId);
            return;
        }
        Book book = oBook.get();
        printMessage(BOOKS_UPDATE_NAME);
        String bookName = ioService.readString();
        printMessage(BOOKS_UPDATE_RELEASE);
        String bookReleaseBeforePars = ioService.readString();
        LocalDate bookRelease = null;
        if(!bookReleaseBeforePars.isEmpty()) {
            try {
                bookRelease = LocalDate.parse(getNotEmptyString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                bookRelease = getLocalDateFromUser();
            }
        }
        printMessage(BOOKS_UPDATE_DESCRIPTION);
        String description = ioService.readString();
        printMessage(BOOKS_UPDATE_AUTHOR);
        String authorIdBeforePars = ioService.readString();
        Author author = null;
        if(!authorIdBeforePars.isEmpty()) {
            long authorId;
            try {
                authorId = Long.parseLong(getNotEmptyString());
            } catch (NumberFormatException e) {
                authorId = getIdFromUser(AUTHORS_PARSE_ID_ERR);
            }
            Optional<Author> oAuthor = authorsRepo.getById(authorId);
            if (oAuthor.isEmpty()) {
                printMessage(AUTHORS_NOT_FOUND, authorId);
                return;
            }
            author = oAuthor.get();
        }
        printMessage(BOOKS_UPDATE_GENRE);
        String genreIdBeforePars = ioService.readString();
        Genre genre = null;
        if(!genreIdBeforePars.isEmpty()) {
            long genreId;
            try {
                genreId = Long.parseLong(getNotEmptyString());
            } catch (NumberFormatException e) {
                genreId = getIdFromUser(AUTHORS_PARSE_ID_ERR);
            }
            Optional<Genre> oGenre = genresRepo.getById(genreId);
            if (oGenre.isEmpty()) {
                printMessage(GENRES_NOT_FOUND, genreId);
                return;
            }
            genre = oGenre.get();
        }
        fillBookWithNonNull(book, bookName, bookRelease, description, author, genre);
        booksRepo.update(book);
    }

    private void fillBookWithNonNull(Book book, String bookName, LocalDate bookRelease,
                                     String description, Author author, Genre genre) {
        if(!bookName.isEmpty()) {
            book.setName(bookName);
        }
        if(Objects.nonNull(bookRelease)) {
            book.setReleaseDate(bookRelease);
        }
        if(!description.isEmpty()) {
            book.setDescription(description);
        }
        if(Objects.nonNull(author)) {
            book.setAuthor(author);
        }
        if(Objects.nonNull(genre)) {
            book.setGenre(genre);
        }
    }

    private String getNotEmptyString() {
        String answer = "";
        boolean notString = true;
        while (notString) {
            answer = ioService.readString();
            if(!answer.isEmpty()) {
            notString = false;
            }
        }
        return answer;
    }

    @Override
    public void delete() {
        printMessage(BOOKS_DELETE_START);
        long bookId = getIdFromUser(AUTHORS_PARSE_ID_ERR);
        booksRepo.delete(bookId);
    }

    private long getIdFromUser (String errMessage) {
        long answer = 0;
        boolean notNumber = true;
        while (notNumber) {
            try {
                answer = Long.parseLong(getNotEmptyString());
                notNumber = false;
            } catch (NumberFormatException e) {
                printMessage(errMessage);
            }
        }
        return answer;
    }

    private void printBook(Book b) {
        printMessage(BOOKS_ID,b.getId());
        printMessage(BOOKS_NAME,b.getName());
        printMessage(BOOKS_AUTHOR,b.getAuthor().getName());
        printMessage(BOOKS_GENRE,b.getGenre().getName());
    }

    private void printBookFull(Book b) {
        printMessage(BOOKS_ID,b.getId());
        printMessage(BOOKS_NAME,b.getName());
        printMessage(BOOKS_RELEASE_DATE,b.getReleaseDate());
        printMessage(BOOKS_DESCRIPTION,b.getDescription());
        printMessage(BOOKS_AUTHOR,b.getAuthor().getName());
        printMessage(BOOKS_GENRE,b.getGenre().getName());
    }
    private void printMessage(String key, Object ...args) {
        ioService.outputString(localizationService.getMessage(key,args));
    }
}
