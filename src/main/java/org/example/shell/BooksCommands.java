package org.example.shell;

import lombok.AllArgsConstructor;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.service.BooksService;
import org.example.util.LocalizedIOservice;
import org.example.util.UsersCommandGetter;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.example.util.LocalizationServiceImpl.*;
import static org.example.util.LocalizationServiceImpl.BOOKS_AUTHOR;
import static org.example.util.LocalizationServiceImpl.BOOKS_GENRE;

@ShellComponent
@AllArgsConstructor
public class BooksCommands {

    private final BooksService booksService;
    private final LocalizedIOservice ioService;
    private final UsersCommandGetter commandGetter;

    @ShellMethod(key = "printAllBooks", value = "Print All Books")
    public void printAllBooks() {
        List<Book> books = booksService.getAll();
        ioService.printMessage(BOOKS_TOTAL,books.size());
        for (Book b : books) {
            printBook(b);
            ioService.printString("=============");
        }
    }

    @ShellMethod(key = "printBook", value = "Print Book")
    public void printBook() {
        ioService.printMessage(BOOKS_FIND_BY_ID_REQ);
        long bookId = commandGetter.getIdFromUser(BOOKS_FIND_BY_ID_ERR);
        try {
            Book book = booksService.getById(bookId);
            printBookFull(book);
        } catch (RuntimeException e) {
            ioService.printString(e.getMessage());
        }
    }

    @ShellMethod(key = "createBook", value = "Create Book")
    public void createBook() {
        ioService.printMessage(BOOKS_CREATE_NAME);
        String bookName = commandGetter.getNotEmptyString();
        ioService.printMessage(BOOKS_CREATE_RELEASE);
        LocalDate bookRelease = commandGetter.getLocalDateFromUser();
        ioService.printMessage(BOOKS_CREATE_DESCRIPTION);
        String description = commandGetter.getNotEmptyString();
        ioService.printMessage(BOOKS_CREATE_AUTHOR);
        long authorId = commandGetter.getIdFromUser(AUTHORS_PARSE_ID_ERR);
        ioService.printMessage(BOOKS_CREATE_GENRE);
        long genreId = commandGetter.getIdFromUser(GENRES_PARSE_ID_ERR);
        Genre genre = new Genre(genreId);
        Author author = new Author(authorId);
        try {
            Book created = booksService.create(new Book(0, bookName, description, bookRelease, genre, author));
            printBookFull(created);
        } catch (RuntimeException e) {
            ioService.printString(e.getMessage());
        }
    }

    @ShellMethod(key = "updateBook", value = "Update Book")
    public void updateBook() {
        ioService.printMessage(BOOKS_UPDATE_START);
        long bookId = commandGetter.getIdFromUser(AUTHORS_PARSE_ID_ERR);
        ioService.printMessage(BOOKS_UPDATE_NAME);
        String bookName = ioService.readString();
        ioService.printMessage(BOOKS_UPDATE_RELEASE);
        String bookReleaseBeforePars = ioService.readString();
        LocalDate bookRelease = null;
        if(!bookReleaseBeforePars.isEmpty()) {
            try {
                bookRelease = LocalDate.parse(commandGetter.getNotEmptyString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                bookRelease = commandGetter.getLocalDateFromUser();
            }
        }
        ioService.printMessage(BOOKS_UPDATE_DESCRIPTION);
        String description = ioService.readString();
        ioService.printMessage(BOOKS_UPDATE_AUTHOR);
        String authorIdBeforePars = ioService.readString();
        Author author = null;
        if(!authorIdBeforePars.isEmpty()) {
            long authorId;
            try {
                authorId = Long.parseLong(commandGetter.getNotEmptyString());
            } catch (NumberFormatException e) {
                authorId = commandGetter.getIdFromUser(AUTHORS_PARSE_ID_ERR);
            }
            author = new Author(authorId);
        }
        ioService.printMessage(BOOKS_UPDATE_GENRE);
        String genreIdBeforePars = ioService.readString();
        Genre genre = null;
        if(!genreIdBeforePars.isEmpty()) {
            long genreId;
            try {
                genreId = Long.parseLong(commandGetter.getNotEmptyString());
            } catch (NumberFormatException e) {
                genreId = commandGetter.getIdFromUser(AUTHORS_PARSE_ID_ERR);
            }
            genre = new Genre(genreId);
        }
        try {
            Book updated = booksService.update(new Book(bookId, bookName, description, bookRelease, genre, author));
            printBookFull(updated);
        } catch (RuntimeException e) {
            ioService.printMessage(e.getMessage());
        }

    }

    @ShellMethod(key = "deleteBook", value = "Delete Book")
    public void deleteBook() {
        ioService.printMessage(BOOKS_DELETE_START);
        long bookId = commandGetter.getIdFromUser(AUTHORS_PARSE_ID_ERR);
        booksService.delete(bookId);
    }

    private void printBook(Book b) {
        ioService.printMessage(BOOKS_ID,b.getId());
        ioService.printMessage(BOOKS_NAME,b.getName());
        ioService.printMessage(BOOKS_AUTHOR,b.getAuthor().getName());
        ioService.printMessage(BOOKS_GENRE,b.getGenre().getName());
    }

    private void printBookFull(Book b) {
        ioService.printMessage(BOOKS_ID,b.getId());
        ioService.printMessage(BOOKS_NAME,b.getName());
        ioService.printMessage(BOOKS_RELEASE_DATE,b.getReleaseDate());
        ioService.printMessage(BOOKS_DESCRIPTION,b.getDescription());
        ioService.printMessage(BOOKS_AUTHOR,b.getAuthor().getName());
        ioService.printMessage(BOOKS_GENRE,b.getGenre().getName());
    }

}
