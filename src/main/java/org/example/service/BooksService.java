package org.example.service;

import org.example.model.Book;

import java.util.List;

public interface BooksService {
    List<Book> getAll();
    Book getById(long id);
    Book create(Book book);
    Book update(Book book);
    void delete(long id);
}
