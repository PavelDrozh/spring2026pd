package org.example.repository;

import jakarta.annotation.Nonnull;
import org.example.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BooksRepo extends JpaRepository<Book, Long> {

    @Nonnull
    @EntityGraph(value = "books-genre-author-entity-graph")
    List<Book> findAll();

    @Nonnull
    @EntityGraph(value = "books-genre-author-entity-graph")
    Optional<Book> findById(@Nonnull Long id);
}
