package org.example.repository;

import org.example.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "authors")
public interface AuthorsRepo extends JpaRepository<Author, Long> {
    Optional<Author> findFirstByIdAfter(Long idAfter);
}
