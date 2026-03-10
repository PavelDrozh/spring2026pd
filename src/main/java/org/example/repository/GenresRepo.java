package org.example.repository;

import org.example.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "genres")
public interface GenresRepo extends JpaRepository<Genre, Long> {
}
