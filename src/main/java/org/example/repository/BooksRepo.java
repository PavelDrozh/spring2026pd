package org.example.repository;

import jakarta.annotation.Nonnull;
import org.example.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "books")
public interface BooksRepo extends JpaRepository<Book, Long> {

    @Nonnull
    @EntityGraph(value = "books-author-entity-graph")
    List<Book> findAll();

    @Nonnull
    @EntityGraph(value = "books-author-entity-graph")
    Optional<Book> findById(@Nonnull Long id);
}
