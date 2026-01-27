package org.example.repository;

import org.example.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenresRepo {
    List<Genre> getAll();
    Optional<Genre> getById(long id);
    Genre create(Genre genre);
    Genre update(Genre genre);
    void delete(long id);
}
