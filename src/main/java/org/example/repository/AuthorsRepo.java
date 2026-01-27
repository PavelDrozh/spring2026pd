package org.example.repository;

import org.example.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorsRepo {
    List<Author> getAll();
    Optional<Author> getById(long id);
    Author create(Author author);
    Author update(Author author);
    void delete(long id);
}
