package org.example.repository;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface BooksRepo {

    List<Book> getAll();

    Optional<Book> getById(long id);

    Book create(Book book);

    Book update(Book book);

    void delete(long id);
}
